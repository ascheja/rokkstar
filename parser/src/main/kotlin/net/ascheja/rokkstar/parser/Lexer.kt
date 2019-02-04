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

    fun toTokenSource(filterFunction: (Token) -> Boolean = { it !is Comment }): TokenSource {
        return TokenSource(tokens.filter(filterFunction))
    }

    private fun tokenize() = sequence {
        var currentLine = 1
        var currentCharInLine = 0
        while (index < input.length) {
            val char = input[index]
            currentCharInLine++
            val currentPosition = Position(currentLine, currentCharInLine)
            when {
                char in IGNORED -> {}
                char == '(' -> {
                    yieldAll(clearBuffer(currentPosition))
                    yieldAll(finalizeBlock(')') { Comment(it, currentPosition) })
                }
                char == '"' -> {
                    yieldAll(clearBuffer(currentPosition))
                    yieldAll(finalizeBlock('"') { StringLiteral(replaceEscapeChars(it), currentPosition) })
                }
                char == '\n' -> {
                    yieldAll(clearBuffer(currentPosition))
                    yield(Eol)
                    currentLine++
                    currentCharInLine = 0
                }
                char in WHITESPACE -> {
                    yieldAll(clearBuffer(currentPosition))
                    yield(Space)
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
                            yieldAll(clearBuffer(currentPosition))
                            yield(Space)
                            yield(Word("is", currentPosition))
                        }
                    }
                }
                else -> {
                    yieldAll(clearBuffer(currentPosition))
                    yield(Garbage(char, currentPosition))
                }
            }
            index++
        }
        yieldAll(clearBuffer(Position(currentLine, currentCharInLine)))
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

    private fun clearBuffer(currentPosition: Position) = sequence {
        if (buffer != "") {
            yield(Word(buffer, currentPosition))
            buffer = ""
        }
    }

    private fun replaceEscapeChars(string: String): String {
        return string.replace(Regex("""\\[\\nrt]""")) {
            when (it.value) {
                "\\\\" -> "\\"
                "\\n" -> "\n"
                "\\r" -> "\r"
                "\\t" -> "\t"
                else -> ""
            }
        }
    }
}