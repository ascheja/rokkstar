package net.ascheja.rokkstar.parser

sealed class Token(val type: Type, val text: String) {

    class Word(text: String, val position: Token.Position? = null): Token(Type.WORD, text) {
        override fun toString(): String = "WORD(\"$text\")"
    }

    object Space: Token(Type.SPACE, " ") {
        override fun toString(): String = "SPACE"
    }

    class Garbage(char: Char, val position: Token.Position? = null): Token(Type.GARBAGE, char.toString()) {
        override fun toString(): String = "GARBAGE(\"$text\")"
    }

    object Eol: Token(Type.EOL, "\n") {
        override fun toString(): String = "EOL"
    }

    object Eof: Token(Type.EOF, "") {
        override fun toString(): String = "EOF"
    }

    class StringLiteral(text: String, val position: Token.Position? = null): Token(Type.STRING_LITERAL, text) {
        override fun toString(): String = "STRING(\"$text\")"
    }

    class Comment(text: String, val position: Token.Position? = null): Token(Type.COMMENT, text) {
        override fun toString(): String = "COMMENT(\"$text\")"
    }

    data class Position(val line: Int, val charInLine: Int) {
        override fun toString(): String {
            return "in line $line, starting at position $charInLine"
        }
    }

    enum class Type {
        WORD,
        SPACE,
        GARBAGE,
        COMMENT,
        STRING_LITERAL,
        EOL,
        EOF
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Token) return false

        if (type != other.type) return false
        if (!text.equals(other.text, true)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + text.toLowerCase().hashCode()
        return result
    }

    abstract override fun toString(): String
}