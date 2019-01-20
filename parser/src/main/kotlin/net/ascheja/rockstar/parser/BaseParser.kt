package net.ascheja.rockstar.parser

import net.ascheja.rockstar.ast.Identifier
import net.ascheja.rockstar.parser.Token.*

open class BaseParser internal constructor(protected val tokens: List<Token>) {

    init {
        if (tokens.isEmpty()) {
            throw ParserException("No tokens to parse")
        }
    }

    internal var lastName: String? = null

    companion object {
        val PRONOUNS = setOf(
            "it", "he", "she", "him", "her", "they", "them", "ze", "hir", "zie", "zir", "xe", "xem", "ve", "ver"
        ).map { Word(it) }

        val COMMON_VARIABLE_PREFIXES = setOf(
            "a", "an", "the", "my", "your"
        ).map { Word(it) }

        val KW_WHILE = Word("While")
        val KW_UNTIL = Word("Until")
        val KW_IF = Word("If")
        val KW_ELSE = Word("Else")
        val KW_GIVE = Word("Give")
        val KW_BACK = Word("back")
        val KW_LISTEN = Word("Listen")
        val KW_TO = Word("to")
        val KW_SAY = Word("Say")
        val KW_SCREAM = Word("Scream")
        val KW_SHOUT = Word("Shout")
        val KW_WHISPER = Word("Whisper")
        val KW_SAYS = Word("says")
        val KW_PUT = Word("Put")
        val KW_INTO = Word("into")

        val KW_TAKING = Word("taking")
        val KW_TAKES = Word("takes")

        val KW_AND = Word("and")
        val KW_OR = Word("or")
        val KW_NOR = Word("nor")
        val KW_NOT = Word("not")

        val KW_IS = Word("is")
        val KW_WAS = Word("was")
        val KW_WERE = Word("were")
        val KW_ISNT = Word("isnt")
        val KW_AINT = Word("aint")

        val KW_AS = Word("as")
        val KW_THAN = Word("than")

        val KW_PLUS = Word("plus")
        val KW_WITH = Word("with")
        val KW_MINUS = Word("minus")
        val KW_WITHOUT = Word("without")
        val KW_TIMES = Word("times")
        val KW_OF = Word("of")
        val KW_OVER = Word("over")

        val KW_CONTINUE = Word("Continue")
        val KW_TAKE = Word("Take")
        val KW_IT = Word("it")
        val KW_THE = Word("the")
        val KW_TOP = Word("top")

        val KW_BREAK = Word("Break")
        val KW_BUILD = Word("Build")
        val KW_UP = Word("up")

        val KW_KNOCK = Word("Knock")
        val KW_DOWN = Word("down")

        val KW_MYSTERIOUS = Word("mysterious")

        val NULL_ALIASES = setOf("null", "nothing", "nowhere", "nobody", "empty", "gone").map { Word(it) }
        val TRUE_ALIASES = setOf("true", "right", "yes", "ok").map { Word(it) }
        val FALSE_ALIASES = setOf("false", "wrong", "no", "lies").map { Word(it) }

        val GREATER_ALIASES = setOf("higher", "greater", "bigger", "stronger").map { Word(it) }
        val LESS_ALIASES = setOf("lower", "less", "smaller", "weaker").map { Word(it) }
        val GREATER_EQUAL_ALIASES = setOf("high", "great", "big", "strong").map { Word(it) }
        val LESS_EQUAL_ALIASES = setOf("low", "little", "small", "weak").map { Word(it) }

        val PROPER_VARIABLE_TERMINATORS = setOf(
            Eol(), Eof(), KW_SAYS, KW_AND, KW_OR, KW_NOR, KW_IS, KW_ISNT, KW_AINT, KW_TAKES, KW_TAKING,
            Garbage(','), Garbage('&'), KW_UP, KW_DOWN, KW_INTO, KW_WAS, KW_WERE,
            KW_PLUS, KW_WITH, KW_MINUS, KW_WITHOUT, KW_TIMES, KW_OF, KW_OVER
        )
    }

    protected var index = 0
        private set

    protected val currentToken: Token
        get() = tokens[index]

    protected fun next(): Token {
        if (index + 1 < tokens.size) {
            index++
        }
        return currentToken
    }

    protected fun skipToNextEolOrEof(): List<Token> {
        val skipped = mutableListOf<Token>()
        while (currentToken !in setOf(Eol(), Eof())) {
            skipped.add(currentToken)
            next()
        }
        return skipped
    }

    protected fun skipToNextWordIfNecessary(): List<Token> {
        val skipped = mutableListOf<Token>()
        while (currentToken !is Word) {
            skipped.add(currentToken)
            next()
        }
        return skipped
    }

    protected fun lookahead(n: Int): Token = if (index + n < tokens.size) {
        if (index + n >= 0) tokens[index + n] else tokens.first()
    } else {
        Eof()
    }

    infix fun Token.mustBe(expectation: (Token) -> Unit): Token {
        expectation(this)
        return this
    }

    infix fun Token.mustBe(expectation: Token): Token = mustBe {
        if (type != expectation.type) {
            throw UnexpectedTokenException("expected token to be of type ${expectation.type}, found $type")
        }
        if (text.toLowerCase() != expectation.text.toLowerCase()) {
            throw UnexpectedTokenException("expected token text '${expectation.text}', found '$text'")
        }
    }

    private infix fun Token.mustBe(expectation: Token.Type): Token = mustBe {
        if (type != expectation) {
            throw UnexpectedTokenException("expected token to be of type $expectation, got $type")
        }
    }

    protected fun any(vararg tokens: Token): (Token) -> Unit = {
        var found = false
        for (token in tokens) {
            if (token == it) {
                found = true
            }
        }
        if (!found) {
            throw UnexpectedTokenException("expected either of ${tokens.toList()}, got $it")
        }
    }

    protected fun parseIdentifier(): Identifier = Identifier(parseName())

    private fun parseName(): String {
        currentToken mustBe Type.WORD
        if (currentToken in PRONOUNS) {
            next()
            return lastName ?: throw UnexpectedTokenException("found pronoun, but no identifier has been mentioned previously")
        }
        return if (currentToken in COMMON_VARIABLE_PREFIXES) {
            //common variable
            val prefix = currentToken.text
            if (lookahead(1) is Space) {
                next()
            }
            next() mustBe Type.WORD
            prefix + " " + currentToken.text
        } else {
            //proper variable
            var temp = currentToken.text
            while (true) {
                if (lookahead(1) is Space) {
                    next()
                }
                if (lookahead(1).let { it !is Word || it in PROPER_VARIABLE_TERMINATORS }) {
                    break
                }
                next()
                temp += " " + currentToken.text
            }
            temp
        }.also {
            lastName = it
            next()
        }
    }
}