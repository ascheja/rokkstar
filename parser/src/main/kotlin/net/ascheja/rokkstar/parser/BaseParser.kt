package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.parser.Token.*

open class BaseParser(lastNameDelegate: LastNameDelegate) {

    protected var lastName: String? by lastNameDelegate

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

        val KW_APO_N_APO = Word("'n'")
        val KW_AND = Word("and")
        val KW_OR = Word("or")
        val KW_NOR = Word("nor")
        val KW_NOT = Word("not")

        val KW_IS = Word("is")
        val KW_WAS = Word("was")
        val KW_WERE = Word("were")
        val KW_ISNT = Word("isn't")
        val KW_AINT = Word("ain't")

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

        val AMPERSAND = Garbage('&')
        val COMMA = Garbage(',')

        val PROPER_VARIABLE_TERMINATORS = setOf(
            Eol, Eof, KW_SAYS, KW_AND, KW_OR, KW_NOR, KW_IS, KW_ISNT, KW_AINT, KW_TAKES, KW_TAKING,
            COMMA, AMPERSAND, KW_APO_N_APO, KW_UP, KW_DOWN, KW_INTO, KW_WAS, KW_WERE,
            KW_PLUS, KW_WITH, KW_MINUS, KW_WITHOUT, KW_TIMES, KW_OF, KW_OVER
        )
        val NUMERIC_CHECK = Regex("[0-9]+")
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
            throw UnexpectedTokenException("expected token text '${expectation.text}', found '$text'${it.getPositionInfo()}")
        }
    }

    private infix fun Token.mustBe(expectation: Token.Type): Token = mustBe {
        if (type != expectation) {
            throw UnexpectedTokenException("expected token to be of type $expectation, got $type${it.getPositionInfo()}")
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
            throw UnexpectedTokenException("expected either of ${tokens.toList()}, got $it${it.getPositionInfo()}")
        }
    }

    protected fun Token.getPositionInfo(): String =when (this) {
        is Word -> position.toString()
        is Garbage -> position.toString()
        is StringLiteral -> position.toString()
        is Comment -> position.toString()
        else -> ""
    }

    private fun Position?.toString(): String {
        if (this != null) {
            return toString()
        }
        return ""
    }

    protected fun parseIdentifier(source: TokenSource): Identifier = Identifier(parseName(source))

    private fun parseName(source: TokenSource): String {
        source.current mustBe Type.WORD
        if (source.current in PRONOUNS) {
            source.next()
            return lastName ?: throw UnexpectedTokenException("found pronoun, but no identifier has been mentioned previously")
        }
        return if (source.current in COMMON_VARIABLE_PREFIXES) {
            //common variable
            val prefix = source.current.text
            if (source.lookahead(1) is Space) {
                source.next()
            }
            source.next() mustBe Type.WORD
            (prefix + " " + source.current.text).also { if (source.lookahead(1) is Space) source.next() }
        } else {
            //proper variable
            var temp = source.current.text
            while (true) {
                if (source.lookahead(1) is Space) {
                    source.next()
                }
                if (source.lookahead(1).let { it !is Word || it in PROPER_VARIABLE_TERMINATORS }) {
                    break
                }
                source.next()
                temp += " " + source.current.text
            }
            temp
        }.also {
            source.next()
        }
    }
}