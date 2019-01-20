package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.parser.Token.*

class Lexer(private val input: CharSequence) {

    companion object {
        private val IGNORED = setOf('\r')
        private val WHITESPACE = setOf(' ', '\t')
    }

    private var buffer = ""

    private var index = 0

    val tokens: List<Token> by lazy { tokenize().toList() }

    private fun tokenize() = sequence {
        while (index < input.length) {
            val char = input[index]
            when {
                char in IGNORED -> {}
                char == '(' -> {
                    yieldAll(clearBuffer())
                    yieldAll(finalizeBlock(')') { Comment(it) })
                }
                char == '"' -> {
                    yieldAll(clearBuffer())
                    yieldAll(finalizeBlock('"') { StringLiteral(it) })
                }
                char == '\n' -> {
                    yieldAll(clearBuffer())
                    yield(Eol())
                }
                char in WHITESPACE -> {
                    yieldAll(clearBuffer())
                    yield(Space())
                }
                char.isLetterOrDigit() -> {
                    buffer += char
                }
                char == '\'' -> {
                    if (index + 2 < input.length) {
                        val maybeS = input[index + 1]
                        val maybeWhitespace = input[index + 2]
                        if (maybeS == 's' && maybeWhitespace in WHITESPACE) {
                            index += 2
                            yieldAll(clearBuffer())
                            yield(Space())
                            yield(Word("is"))
                        }
                    }
                }
                else -> {
                    yieldAll(clearBuffer())
                    yield(Garbage(char))
                }
            }
            index++
        }
        yieldAll(clearBuffer())
        yield(Eof())
    }

    private fun finalizeBlock(until: Char, tokenGenerator: (String) -> Token) = sequence {
        while (index + 1 < input.length && input[index + 1] != until) {
            index++
            buffer += input[index]
        }
        index++
        if (buffer != "") {
            yield(tokenGenerator(buffer))
            buffer = ""
        }
    }

    private fun clearBuffer() = sequence {
        if (buffer != "") {
            yield(Word(buffer))
            buffer = ""
        }
    }
}