package net.ascheja.rokkstar.runner

import net.ascheja.rokkstar.interpreter.Context
import net.ascheja.rokkstar.interpreter.Interpreter
import net.ascheja.rokkstar.parser.Lexer
import net.ascheja.rokkstar.parser.StatementParser
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class ProgramTest {

    @Test
    fun `easy fizz-buzz parsed without problems`() {
        val content = readFile("fizzbuzz-easy.rock")
        val expectedOutput = readFile("fizzbuzz.rock.out")
        assertEquals(expectedOutput, execute(content))
    }

    @Test
    fun `hard fizz-buzz parsed without problems`() {
        val content = readFile("fizzbuzz-hard.rock")
        val expectedOutput = readFile("fizzbuzz.rock.out")
        assertEquals(expectedOutput, execute(content))
    }

    private fun readFile(filename: String): String {
        return File(javaClass.classLoader.getResource(filename).toURI()).readText()
    }

    private fun execute(content: String): String {
        val program = StatementParser().parseProgram(Lexer(content).toTokenSource())
        val output = StringBuilder()
        val context = Context.create({ "" } , { output.append(it).append("\n") })
        val interpreter = Interpreter(context)
        interpreter.visitProgram(program)
        return output.toString()
    }
}