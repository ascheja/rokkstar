package net.ascheja.rokkstar.parser

class TokenSource(private val tokens: List<Token>): Iterable<Token> by tokens {

    var index: Int = 0
        private set

    val current: Token get() = if (index < tokens.size) tokens[index] else Token.Eof

    fun next(): Token {
        if (index < tokens.size) {
            index++
        }
        return current
    }

    fun lookahead(n: Int): Token = if (index + n < tokens.size) {
        if (index + n >= 0) tokens[index + n] else tokens.first()
    } else {
        Token.Eof
    }

    fun matchSeq(vararg match: Token): Boolean {
        for ((i, token) in match.withIndex()) {
            if (lookahead(i) != token) {
                return false
            }
        }
        return true.also { index += match.size - 1 }
    }

    fun skipToNextEolOrEof(): TokenSource {
        val skipped = mutableListOf<Token>()
        while (current !in setOf(Token.Eol, Token.Eof)) {
            skipped.add(current)
            next()
        }
        return TokenSource(skipped)
    }

    fun skipToNextWordIfNecessary(): TokenSource {
        val skipped = mutableListOf<Token>()
        while (current !is Token.Word) {
            skipped.add(current)
            next()
        }
        return TokenSource(skipped)
    }

    fun subList(startInclusive: Int, endExclusive: Int): TokenSource =
        TokenSource(tokens.subList(startInclusive, endExclusive))

    fun filtered(function: (Token) -> Boolean): TokenSource =
        TokenSource(tokens.filter(function))
}