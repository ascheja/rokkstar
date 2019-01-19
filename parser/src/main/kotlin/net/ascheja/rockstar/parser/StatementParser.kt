package net.ascheja.rockstar.parser

import net.ascheja.rockstar.ast.*
import net.ascheja.rockstar.ast.expressions.NumberLiteralExpression
import net.ascheja.rockstar.ast.expressions.StringLiteralExpression
import net.ascheja.rockstar.ast.statements.*
import net.ascheja.rockstar.parser.Token.*

class StatementParser(tokens: List<Token>): BaseParser(tokens.filter { it !is Comment}) {

    companion object {
        val NUMERIC_CHECK = Regex("[0-9]")
    }

    fun parseProgram(): Program {
        return Program(parseBlockStatement())
    }

    fun parseStatement(): Statement {
        return when {
            currentToken == KW_ELSE -> throw ParserException("else without if")
            currentToken == KW_LISTEN -> parseListenTo()
            currentToken == KW_IF -> parseIf()
            currentToken in setOf(KW_SAY, KW_SCREAM, KW_SHOUT, KW_WHISPER) -> parseSay()
            currentToken == KW_PUT -> parsePutInto()
            currentToken in setOf(KW_WHILE, KW_UNTIL) -> parseLoop()
            matchContinue() -> parseContinue()
            matchBreak() -> parseBreak()
            currentToken == KW_BUILD -> parseIncrement()
            currentToken == KW_KNOCK -> parseDecrement()
            currentToken == KW_GIVE -> parseReturn()
            else -> {
                val identifier = parseIdentifier()
                when (currentToken) {
                    KW_IS, KW_WAS, KW_WERE -> parseLiteralAssignment(identifier)
                    KW_TAKES -> parseFunctionDeclaration(identifier)
                    KW_SAYS -> parseLiteralStringAssignment(identifier)
                    else -> throw UnexpectedTokenException(currentToken.text)
                }
            }
        }
    }

    private fun parseLiteralStringAssignment(identifier: Identifier): AssignmentStatement {
        val allUntilEol = mutableListOf<String>()
        while (currentToken !in setOf(Eol(), Eof())) {
            allUntilEol.add(currentToken.text)
            next()
        }
        return AssignmentStatement(identifier, StringLiteralExpression(allUntilEol.joinToString("")))
    }

    private fun parseLiteralAssignment(identifier: Identifier): Statement {
        currentToken mustBe any(KW_IS, KW_WAS, KW_WERE)
        next()
        currentToken mustBe Space()
        val value = when (currentToken) {
            is StringLiteral -> StringLiteralExpression(currentToken.text)
            else -> {
                val numberAsString = if (currentToken.text.matches(NUMERIC_CHECK)) {
                    var tmp = ""
                    while (currentToken !in setOf(Eol(), Eof())) {
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
                    while (currentToken !in setOf(Eol(), Eof())) {
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
        currentToken mustBe any(Eol(), Eof())
        return AssignmentStatement(identifier, value)
    }

    private fun parseFunctionDeclaration(identifier: Identifier): FunctionDeclaration {
        currentToken mustBe KW_TAKES
        next()
        val parameters = extractParameters().map { Identifier(it.joinToString("")) }
        currentToken mustBe Eol()
        next()
        return FunctionDeclaration(identifier, parameters, parseBlockStatement())
    }

    private fun parseReturn(): ReturnStatement {
        currentToken mustBe KW_GIVE
        next()
        currentToken mustBe KW_BACK
        next()
        val start = index
        while (currentToken !in setOf(Eol(), Eof())) {
            next()
        }
        return ReturnStatement(ExpressionParser(tokens.subList(start, index)).parseExpression())
    }

    private fun parseIncrement(): IncrementStatement {
        currentToken mustBe KW_BUILD
        next()
        val identifier = parseIdentifier()
        currentToken mustBe KW_UP
        var amount = 1
        while (lookahead(1) == Garbage(',')) {
            forwardToNext()
            if (currentToken != KW_UP) {
                throw UnexpectedTokenException(currentToken.text)
            }
            amount++
        }
        return IncrementStatement(identifier, amount)
    }

    private fun parseDecrement(): DecrementStatement {
        currentToken mustBe KW_KNOCK
        next()
        val identifier = parseIdentifier()
        currentToken mustBe KW_DOWN
        var amount = 1
        while (lookahead(1) == Garbage(',')) {
            forwardToNext()
            if (currentToken != KW_DOWN) {
                throw UnexpectedTokenException(currentToken.text)
            }
            amount++
        }
        return DecrementStatement(identifier, amount)
    }

    private fun parseContinue(): ContinueStatement {
        forwardToNext(word = false)
        return ContinueStatement()
    }

    private fun parseBreak(): BreakStatement {
        forwardToNext(word = false)
        return BreakStatement()
    }

    private fun parseIf(): IfStatement {
        currentToken mustBe KW_IF
        next()
        currentToken mustBe Space()
        next()
        val start = index
        while (currentToken !is Eol && currentToken !is Eof) {
            next()
        }
        val condition = ExpressionParser(tokens.subList(start, index)).parseExpression()
        next()
        val thenBlock = parseBlockStatement()
        val elseBlock = if (currentToken == KW_ELSE) {
            next()
            currentToken mustBe Eol()
            next()
            parseBlockStatement()
        } else null
        return IfStatement(condition, thenBlock, elseBlock)
    }

    private fun parseLoop(): LoopStatement {
        currentToken mustBe any(KW_WHILE, KW_UNTIL)
        next()
        currentToken mustBe Space()
        next()
        val start = index
        while (currentToken !is Eol && currentToken !is Eof) {
            next()
        }
        val condition = ExpressionParser(tokens.subList(start, index)).parseExpression()
        next()
        return LoopStatement(condition, parseBlockStatement())
    }

    private fun parseBlockStatement(): BlockStatement {
        val statements = mutableListOf<Statement>()
        while (index < tokens.size) {
            if (currentToken == KW_ELSE) {
                break
            }
            statements.add(parseStatement())
            currentToken mustBe { it is Eol || it is Eof }
            next()
            if (currentToken is Eol || currentToken is Eof) {
                next()
                break
            }
        }
        return BlockStatement(statements)
    }

    private fun parseListenTo(): ReadLineStatement {
        currentToken mustBe KW_LISTEN
        next()
        currentToken mustBe Space()
        next()
        currentToken mustBe KW_TO
        next()
        currentToken mustBe Space()
        next()
        return ReadLineStatement(parseIdentifier())
    }

    private fun parseSay(): PrintLineStatement {
        currentToken mustBe any(KW_SAY, KW_SHOUT, KW_SCREAM, KW_WHISPER)
        next()
        currentToken mustBe Space()
        next()
        val start = index
        while (currentToken !is Eol && currentToken !is Eof) {
            next()
        }
        return PrintLineStatement(ExpressionParser(tokens.subList(start, index)).parseExpression())
    }

    private fun parsePutInto(): AssignmentStatement {
        currentToken mustBe KW_PUT
        next()
        currentToken mustBe Space()
        next()
        val start = index
        while (currentToken != KW_INTO) {
            if (currentToken is Eol || currentToken is Eof) {
                throw ParserException("Put without into")
            }
            next()
        }
        next()
        currentToken mustBe Space()
        next()
        val expression = ExpressionParser(tokens.subList(start, index)).parseExpression()
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
            && lookahead(9) in setOf(Eol(), Eof())
    }

    private fun matchBreak(): Boolean {
        if (currentToken == KW_BREAK && lookahead(1) in setOf(Eol(), Eof())) {
            return true
        }
        return currentToken == KW_BREAK
            && lookahead(1) == Space()
            && lookahead(2) == KW_IT
            && lookahead(3) == Space()
            && lookahead(4) == KW_DOWN
            && lookahead(5) in setOf(Eol(), Eof())
    }

    private fun extractParameters(): List<List<Token>> {
        val argumentTokens: MutableList<List<Token>> = mutableListOf()
        var start = index
        while (index < tokens.size) {
            if (currentToken in setOf(ExpressionParser.AMPERSAND, ExpressionParser.COMMA, Word("n"), KW_AND)) {
                argumentTokens.add(tokens.subList(start, index))
                if (currentToken == ExpressionParser.COMMA && lookahead(1) == KW_AND) {
                    next()
                }
                next()
                start = index
            } else {
                next()
            }
        }
        return argumentTokens
    }
}