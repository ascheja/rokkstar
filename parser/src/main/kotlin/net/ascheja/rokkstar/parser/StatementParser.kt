package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.*
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*
import net.ascheja.rokkstar.parser.Token.*

class StatementParser(tokens: List<Token>): BaseParser(tokens.filter { it !is Comment}) {

    fun parseProgram(): Program {
        return Program(parseBlockStatement())
    }

    fun parseStatement(): Statement {
        return when {
            currentToken == KW_ELSE -> throw UnexpectedTokenException("else without if")
            currentToken == KW_LISTEN -> parseListenTo()
            currentToken == KW_IF -> parseIf()
            currentToken in setOf(KW_SAY, KW_SCREAM, KW_SHOUT, KW_WHISPER) -> parseSay()
            currentToken == KW_PUT -> parsePutInto()
            currentToken == KW_WHILE -> parseWhileLoop()
            currentToken == KW_UNTIL -> parseUntilLoop()
            matchContinue() -> parseContinue()
            matchBreak() -> parseBreak()
            currentToken == KW_BUILD -> parseIncrement()
            currentToken == KW_KNOCK -> parseDecrement()
            currentToken == KW_GIVE -> parseReturn()
            else -> {
                val identifier = parseIdentifier()
                skipToNextWordIfNecessary()
                when (currentToken) {
                    KW_IS, KW_WAS, KW_WERE -> parseLiteralAssignment(identifier)
                    KW_TAKES -> parseFunctionDeclaration(identifier)
                    KW_SAYS -> parsePoeticStringLiteralAssignment(identifier)
                    else -> throw UnexpectedTokenException(currentToken.text)
                }
            }
        }
    }

    private fun parsePoeticStringLiteralAssignment(identifier: Identifier): AssignmentStatement {
        currentToken mustBe KW_SAYS
        next() mustBe Space()
        next()
        return AssignmentStatement(identifier, StringLiteralExpression(skipToNextEolOrEof().joinToString("") { it.text}))
    }

    private fun parseLiteralAssignment(identifier: Identifier): Statement {
        currentToken mustBe any(KW_IS, KW_WAS, KW_WERE)
        next() mustBe Space()
        next()
        val value = when (currentToken) {
            is StringLiteral -> StringLiteralExpression(currentToken.text).also { next() }
            KW_MYSTERIOUS -> UndefinedLiteralExpression().also { next() }
            in NULL_ALIASES -> NullLiteralExpression().also { next() }
            in TRUE_ALIASES -> BooleanLiteralExpression(true).also { next() }
            in FALSE_ALIASES -> BooleanLiteralExpression(true).also { next() }
            else -> {
                val numberAsString = if (currentToken.text.matches(NUMERIC_CHECK)) {
                    var tmp = ""
                    while (currentToken !in setOf(Eol, Eof)) {
                        if (currentToken == Garbage('.') && !tmp.contains('.')) {
                            tmp += "."
                        }
                        if (currentToken is Word && currentToken.text.matches(NUMERIC_CHECK)) {
                            tmp += currentToken.text
                        }
                        next()
                    }
                    tmp
                } else {
                    var tmp = ""
                    while (currentToken !in setOf(Eol, Eof)) {
                        if (currentToken == Garbage('.') && !tmp.contains('.')) {
                            tmp += "."
                        }
                        if (currentToken is Word) {
                            tmp += currentToken.text.length % 10
                        }
                        next()
                    }
                    tmp
                }
                NumberLiteralExpression(numberAsString.trimEnd('.').toDouble())
            }
        }
        currentToken mustBe any(Eol, Eof)
        return AssignmentStatement(identifier, value)
    }

    private fun parseFunctionDeclaration(identifier: Identifier): FunctionDeclaration {
        currentToken mustBe KW_TAKES
        next()
        val parameters = extractParameters().map {
            Identifier(it.joinToString("") {token -> token.text}.trim())
        }
        next()
        return FunctionDeclaration(identifier, parameters, parseBlockStatement())
    }

    private fun parseReturn(): ReturnStatement {
        currentToken mustBe KW_GIVE
        next() mustBe Space()
        next() mustBe KW_BACK
        next() mustBe Space()
        return ReturnStatement(parseExpression(skipToNextEolOrEof()))
    }

    private fun parseIncrement(): IncrementStatement {
        currentToken mustBe KW_BUILD
        next() mustBe Space()
        next()
        val identifier = parseIdentifier()
        while (currentToken is Space) {
            next()
        }
        currentToken mustBe KW_UP
        next()
        var amount = 1
        while (currentToken == Garbage(',')) {
            skipToNextWordIfNecessary()
            if (currentToken != KW_UP) {
                throw UnexpectedTokenException(currentToken.text)
            }
            next()
            amount++
        }
        return IncrementStatement(identifier, amount)
    }

    private fun parseDecrement(): DecrementStatement {
        currentToken mustBe KW_KNOCK
        next() mustBe Space()
        next()
        val identifier = parseIdentifier()
        currentToken mustBe KW_DOWN
        next()
        var amount = 1
        while (currentToken == Garbage(',')) {
            skipToNextWordIfNecessary()
            if (currentToken != KW_DOWN) {
                throw UnexpectedTokenException(currentToken.text)
            }
            next()
            amount++
        }
        return DecrementStatement(identifier, amount)
    }

    private fun parseContinue(): ContinueStatement {
        skipToNextEolOrEof()
        return ContinueStatement()
    }

    private fun parseBreak(): BreakStatement {
        skipToNextEolOrEof()
        return BreakStatement()
    }

    private fun parseIf(): IfStatement {
        currentToken mustBe KW_IF
        next()
        currentToken mustBe Space()
        next()
        val condition = parseExpression(skipToNextEolOrEof())
        next()
        val thenBlock = parseBlockStatement()
        val elseBlock = if (currentToken == KW_ELSE) {
            next()
            currentToken mustBe Eol
            next()
            parseBlockStatement()
        } else null
        return IfStatement(condition, thenBlock, elseBlock)
    }

    private fun parseWhileLoop(): WhileLoopStatement {
        currentToken mustBe KW_WHILE
        next() mustBe Space()
        next()
        val condition = parseExpression(skipToNextEolOrEof())
        next()
        return WhileLoopStatement(condition, parseBlockStatement())
    }

    private fun parseUntilLoop(): UntilLoopStatement {
        currentToken mustBe KW_UNTIL
        next() mustBe Space()
        next()
        val condition = parseExpression(skipToNextEolOrEof())
        next()
        return UntilLoopStatement(condition, parseBlockStatement())
    }

    private fun parseBlockStatement(): BlockStatement {
        val statements = mutableListOf<Statement>()
        if (currentToken == Eof) {
            throw UnexpectedTokenException("Found Eof at start of a block")
        }
        while (currentToken != Eof) {
            if (currentToken == KW_ELSE) {
                break
            }
            while (currentToken !is Word && currentToken !is Eol) {
                next()
            }
            if (currentToken is Eof || currentToken is Eol) {
                break
            }
            statements.add(parseStatement())
            currentToken mustBe any(Eof, Eol)
            next()
            while (currentToken is Space) {
                next()
            }
            if (currentToken is Eol || currentToken is Eof) {
                break
            }
        }
        return BlockStatement(statements)
    }

    private fun parseListenTo(): ReadLineStatement {
        currentToken mustBe KW_LISTEN
        next() mustBe Space()
        next() mustBe KW_TO
        next() mustBe Space()
        next()
        return ReadLineStatement(parseIdentifier())
    }

    private fun parseSay(): PrintLineStatement {
        currentToken mustBe any(KW_SAY, KW_SHOUT, KW_SCREAM, KW_WHISPER)
        next() mustBe Space()
        next()
        return PrintLineStatement(parseExpression(skipToNextEolOrEof()))
    }

    private fun parsePutInto(): AssignmentStatement {
        currentToken mustBe KW_PUT
        next() mustBe Space()
        next()
        val start = index
        while (currentToken != KW_INTO) {
            if (currentToken is Eol || currentToken is Eof) {
                throw ParserException("Put without into")
            }
            next()
        }
        next() mustBe Space()
        next()
        val expression = parseExpression(tokens.subList(start, index))
        return AssignmentStatement(parseIdentifier(), expression)
    }

    private fun matchContinue(): Boolean {
        if (currentToken == KW_CONTINUE) {
            return true
        }
        return currentToken == KW_TAKE
            && lookahead(1) == Space()
            && lookahead(2) == KW_IT
            && lookahead(3) == Space()
            && lookahead(4) == KW_TO
            && lookahead(5) == Space()
            && lookahead(6) == KW_THE
            && lookahead(7) == Space()
            && lookahead(8) == KW_TOP
            && lookahead(9) in setOf(Eol, Eof)
    }

    private fun matchBreak(): Boolean {
        if (currentToken == KW_BREAK && lookahead(1) in setOf(Eol, Eof)) {
            return true
        }
        return currentToken == KW_BREAK
            && lookahead(1) == Space()
            && lookahead(2) == KW_IT
            && lookahead(3) == Space()
            && lookahead(4) == KW_DOWN
            && lookahead(5) in setOf(Eol, Eof)
    }

    private fun extractParameters(): List<List<Token>> {
        val argumentTokens: MutableList<List<Token>> = mutableListOf()
        var start = index
        while (currentToken != Eof) {
            if (currentToken in setOf(AMPERSAND, COMMA, Word("n"), KW_AND, Eol)) {
                argumentTokens.add(tokens.subList(start, index))
                if (currentToken == COMMA && lookahead(1) == KW_AND) {
                    next()
                }
                if (currentToken == Eol) {
                    break
                }
                next()
                start = index
            } else {
                next()
            }
        }
        return argumentTokens
    }

    private fun parseExpression(tokens: List<Token>): Expression {
        val parser = ExpressionParser(tokens)
        parser.lastName = lastName
        return parser.parseExpression().also { lastName = parser.lastName }
    }
}