package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.*
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*
import net.ascheja.rokkstar.parser.Token.*

class StatementParser(private val lastNameDelegate: LastNameDelegate): BaseParser(lastNameDelegate) {

    constructor() : this(LastNameDelegate())

    fun parseProgram(source: TokenSource): Program {
        return Program(parseBlockStatement(source, false))
    }

    fun parseStatement(source: TokenSource): Statement {
        return when {
            source.current == KW_ELSE -> throw UnexpectedTokenException("else without if${source.current.getPositionInfo()}")
            source.matchSeq(KW_LISTEN, Space, KW_TO) -> parseListenTo(source)
            source.current == KW_IF -> parseIf(source)
            source.current in setOf(KW_SAY, KW_SCREAM, KW_SHOUT, KW_WHISPER) -> parseSay(source)
            source.current == KW_PUT -> parsePutInto(source)
            source.current == KW_WHILE -> parseWhileLoop(source)
            source.current == KW_UNTIL -> parseUntilLoop(source)
            source.matchSeq(KW_CONTINUE) -> parseContinue()
            source.matchSeq(KW_TAKE, Space, KW_IT, Space, KW_TO, Space, KW_THE, Space, KW_TOP) -> parseContinue()
            source.matchSeq(KW_BREAK, Space, KW_IT, Space, KW_DOWN) -> parseBreak()
            source.matchSeq(KW_BREAK) -> parseBreak()
            source.current == KW_BUILD -> parseIncrement(source)
            source.current == KW_KNOCK -> parseDecrement(source)
            source.matchSeq(KW_GIVE, Space, KW_BACK) -> parseReturn(source)
            else -> {
                val currentPosition = source.index
                val identifier = parseIdentifier(source)
                source.skipToNextWordIfNecessary()
                when (source.current) {
                    KW_IS, KW_WAS, KW_WERE -> parseLiteralAssignment(identifier, source)
                    KW_TAKES -> parseFunctionDeclaration(identifier, source)
                    KW_TAKING -> {
                        source.skipToNextEolOrEof()
                        parseExpression(source.subList(currentPosition, source.index))
                    }
                    KW_SAYS -> parsePoeticStringLiteralAssignment(identifier, source)
                    else -> throw UnexpectedTokenException(source.current.text)
                }
            }
        }
    }

    private fun parsePoeticStringLiteralAssignment(identifier: Identifier, source: TokenSource): AssignmentStatement {
        lastName = identifier.value
        source.current mustBe KW_SAYS
        source.next() mustBe Space
        source.next()
        return AssignmentStatement(identifier, StringConstant(source.skipToNextEolOrEof().joinToString("") { it.text}))
    }

    private fun parseLiteralAssignment(identifier: Identifier, source: TokenSource): Statement {
        lastName = identifier.value
        source.current mustBe any(KW_IS, KW_WAS, KW_WERE)
        source.next() mustBe Space
        source.next()
        val value = when (source.current) {
            is StringLiteral -> StringConstant(source.current.text).also { source.next() }
            KW_MYSTERIOUS -> UndefinedConstant().also { source.next() }
            in NULL_ALIASES -> NullConstant().also { source.next() }
            in TRUE_ALIASES -> BooleanConstant(true).also { source.next() }
            in FALSE_ALIASES -> BooleanConstant(false).also { source.next() }
            else -> {
                val numberAsString = if (source.current.text.matches(NUMERIC_CHECK)) {
                    var tmp = ""
                    while (source.current !in setOf(Eol, Eof)) {
                        if (source.current == Garbage('.') && !tmp.contains('.')) {
                            tmp += "."
                        }
                        if (source.current is Word && source.current.text.matches(NUMERIC_CHECK)) {
                            tmp += source.current.text
                        }
                        source.next()
                    }
                    tmp
                } else {
                    var tmp = ""
                    while (source.current !in setOf(Eol, Eof)) {
                        if (source.current == Garbage('.') && !tmp.contains('.')) {
                            tmp += "."
                        }
                        if (source.current is Word) {
                            tmp += source.current.text.length % 10
                        }
                        source.next()
                    }
                    tmp
                }
                NumberConstant(numberAsString.trimEnd('.').toDouble())
            }
        }
        source.current mustBe any(Eol, Eof)
        return AssignmentStatement(identifier, value)
    }

    private fun parseFunctionDeclaration(identifier: Identifier, source: TokenSource): FunctionDeclaration {
        source.current mustBe KW_TAKES
        source.next()
        val parameters = extractParameters(source).map {
            Identifier(it.joinToString("") {token -> token.text}.trim())
        }
        source.next()
        return FunctionDeclaration(identifier, parameters, parseBlockStatement(source))
    }

    private fun parseReturn(source: TokenSource): ReturnStatement {
        source.current mustBe Space
        return ReturnStatement(parseExpression(source.skipToNextEolOrEof()))
    }

    private fun parseIncrement(source: TokenSource): IncrementStatement {
        source.current mustBe KW_BUILD
        source.next() mustBe Space
        source.next()
        val identifier = parseIdentifier(source)
        while (source.current is Space) {
            source.next()
        }
        source.current mustBe KW_UP
        source.next()
        var amount = 1
        while (source.current == Garbage(',')) {
            source.skipToNextWordIfNecessary()
            if (source.current != KW_UP) {
                throw UnexpectedTokenException(source.current.text)
            }
            source.next()
            amount++
        }
        return IncrementStatement(identifier, amount)
    }

    private fun parseDecrement(source: TokenSource): DecrementStatement {
        source.current mustBe KW_KNOCK
        source.next() mustBe Space
        source.next()
        val identifier = parseIdentifier(source)
        source.current mustBe KW_DOWN
        source.next()
        var amount = 1
        while (source.current == Garbage(',')) {
            source.skipToNextWordIfNecessary()
            if (source.current != KW_DOWN) {
                throw UnexpectedTokenException(source.current.text)
            }
            source.next()
            amount++
        }
        return DecrementStatement(identifier, amount)
    }

    private fun parseContinue(): ContinueStatement {
        return ContinueStatement()
    }

    private fun parseBreak(): BreakStatement {
        return BreakStatement()
    }

    private fun parseIf(source: TokenSource): IfStatement {
        source.current mustBe KW_IF
        source.next()
        source.current mustBe Space
        source.next()
        val condition = parseExpression(source.skipToNextEolOrEof())
        source.next()
        val thenBlock = parseBlockStatement(source)
        val elseBlock = if (source.current == KW_ELSE) {
            source.next()
            source.current mustBe Eol
            source.next()
            parseBlockStatement(source)
        } else null
        return IfStatement(condition, thenBlock, elseBlock)
    }

    private fun parseWhileLoop(source: TokenSource): WhileLoopStatement {
        source.current mustBe KW_WHILE
        source.next() mustBe Space
        source.next()
        val condition = parseExpression(source.skipToNextEolOrEof())
        source.next()
        return WhileLoopStatement(condition, parseBlockStatement(source))
    }

    private fun parseUntilLoop(source: TokenSource): UntilLoopStatement {
        source.current mustBe KW_UNTIL
        source.next() mustBe Space
        source.next()
        val condition = parseExpression(source.skipToNextEolOrEof())
        source.next()
        return UntilLoopStatement(condition, parseBlockStatement(source))
    }

    private fun parseBlockStatement(source: TokenSource, breakOnEol: Boolean = true): BlockStatement {
        val statements = mutableListOf<Statement>()
        if (source.current == Eof) {
            throw UnexpectedTokenException("Found Eof at start of a block")
        }
        while (source.current != Eof) {
            if (source.current == KW_ELSE) {
                break
            }
            while (source.current !is Word && source.current !is Eol) {
                source.next()
            }
            while (!breakOnEol && source.current is Eol) {
                source.next()
            }
            if (source.current is Eof || (breakOnEol && source.current is Eol)) {
                break
            }
            statements.add(parseStatement(source))
            source.current mustBe any(Eof, Eol)
            source.next()
            while (source.current is Space) {
                source.next()
            }
            while (!breakOnEol && source.current is Eol) {
                source.next()
            }
        }
        return BlockStatement(statements)
    }

    private fun parseListenTo(source: TokenSource): ReadLineStatement {
        source.current mustBe Space
        source.next()
        return ReadLineStatement(parseIdentifier(source))
    }

    private fun parseSay(source: TokenSource): PrintLineStatement {
        source.current mustBe any(KW_SAY, KW_SHOUT, KW_SCREAM, KW_WHISPER)
        source.next() mustBe Space
        source.next()
        return PrintLineStatement(parseExpression(source.skipToNextEolOrEof()))
    }

    private fun parsePutInto(source: TokenSource): AssignmentStatement {
        source.current mustBe KW_PUT
        source.next() mustBe Space
        source.next()
        val start = source.index
        while (source.current != KW_INTO) {
            if (source.current is Eol || source.current is Eof) {
                throw ParserException("Put without into")
            }
            source.next()
        }
        source.next() mustBe Space
        source.next()
        val expression = parseExpression(source.subList(start, source.index))
        return AssignmentStatement(parseIdentifier(source), expression).also {
            lastName = it.identifier.value
        }
    }

    private fun extractParameters(source: TokenSource): List<TokenSource> {
        val argumentTokens: MutableList<TokenSource> = mutableListOf()
        var start = source.index
        while (source.current != Eof) {
            if (source.current in setOf(AMPERSAND, COMMA, Word("n"), KW_AND, Eol)) {
                argumentTokens.add(source.subList(start, source.index))
                source.matchSeq(COMMA, Space, KW_AND) || source.matchSeq(COMMA, KW_AND)
                if (source.current == Eol) {
                    break
                }
                source.next()
                start = source.index
            } else {
                source.next()
            }
        }
        return argumentTokens
    }

    private fun parseExpression(source: TokenSource): Expression {
        return ExpressionParser(lastNameDelegate).parseExpression(source.filtered { it !is Space })
    }
}