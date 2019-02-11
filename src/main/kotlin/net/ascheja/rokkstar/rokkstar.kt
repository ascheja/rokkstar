package net.ascheja.rokkstar

import com.fasterxml.jackson.databind.SerializationFeature
import net.ascheja.rokkstar.ast.Program
import net.ascheja.rokkstar.interpreter.Context
import net.ascheja.rokkstar.interpreter.Interpreter
import java.io.File
import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.withTimeout
import net.ascheja.rokkstar.parser.*
import net.ascheja.rokkstar.typesystem.Value
import java.lang.RuntimeException
import kotlin.system.exitProcess

const val TWO_MEGS = 2 * 1024 * 1024

fun main(args: Array<String>) {
    if (args.size != 2) {
        displayHelp()
    }
    when (args[0]) {
        "run" -> {
            System.`in`.bufferedReader().use { inputReader ->
                runProgram(
                    parseProgram(File(args[1])),
                    { inputReader.readLine() ?: "" },
                    { println(it) })
            }
        }
        "api" -> runApi(args[1].toInt())
        else -> displayHelp()
    }
}

fun runApi(port: Int) {
    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            jackson {
                enable(SerializationFeature.INDENT_OUTPUT)
            }
        }
        install(CORS) {
            method(HttpMethod.Post)
            anyHost()
        }
        routing {
            post("/run") {
                val output = mutableListOf<String>()
                val errors = mutableListOf<String>()
                val success = try {
                    withTimeout(2000) {
                        try {
                            val body = readBody(call.request.receiveChannel())
                            val program = parseProgram(body)
                            runProgram(program, { "" }, { value -> output.add(value.toString()) })
                            return@withTimeout true
                        } catch (e: UnexpectedTokenException) {
                            errors.add("Unexpected token: ${e.message}")
                        } catch (e: ParserException) {
                            errors.add("Parser error: ${e.message}")
                        } catch (e: RuntimeException) {
                            errors.add(e.message ?: e.javaClass.simpleName)
                        }
                        return@withTimeout false
                    }
                } catch (e: TimeoutCancellationException) {
                    errors.add("timeout reached")
                    false
                }
                call.respond(if (success) HttpStatusCode.OK else HttpStatusCode.BadRequest,
                    RunResult(output, errors)
                )
            }
        }
    }.start(wait = true)
}

suspend fun readBody(receiveChannel: ByteReadChannel): String {
    var body = ""
    while (true) {
        val line = receiveChannel.readUTF8Line() ?: break
        body += line
        body += "\n"
        if (body.length > TWO_MEGS) {
            throw RuntimeException("content too long, max 2MiB allowed")
        }
    }
    return body
}

fun displayHelp(): Nothing {
    println("rokkstar run <file>         run the provided file with the rockstar interpreter")
    println("rokkstar api <port>         runs the api on the given port")
    exitProcess(1)
}

fun parseProgram(file: File): Program = parseProgram(file.readText())

fun parseProgram(content: String): Program {
    return StatementParser().parseProgram(Lexer(content).toTokenSource())
}

fun runProgram(program: Program, reader: () -> String, writer: (Value) -> Unit) {
    Interpreter(Context.create(reader, writer)).visitProgram(program)
}