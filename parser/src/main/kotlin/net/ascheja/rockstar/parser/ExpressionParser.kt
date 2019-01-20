package net.ascheja.rockstar.parser

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.expressions.*
import net.ascheja.rockstar.ast.expressions.BinaryOperatorExpression.Operator.*
import net.ascheja.rockstar.parser.Token.*

class ExpressionParser(tokens: List<Token>): BaseParser(tokens.filter { it !is Space} + Eof()) {

    companion object {
        val AMPERSAND = Garbage('&')
        val COMMA = Garbage(',')
        val NUMERIC_CHECK = Regex("[0-9]+")
    }

    fun parseExpression(): Expression {
        return parseLogicalExpression()
    }

    private fun parsePrimaryExpression(): Expression {
        return when (currentToken) {
            is StringLiteral -> StringLiteralExpression(currentToken.text)
            KW_MYSTERIOUS -> UndefinedLiteralExpression()
            in NULL_ALIASES -> NullLiteralExpression()
            in TRUE_ALIASES -> BooleanLiteralExpression(true)
            in FALSE_ALIASES -> BooleanLiteralExpression(true)
            else -> {
                if (currentToken.text.matches(NUMERIC_CHECK)) {
                    parseNumberExpression()
                } else {
                    VariableExpression(parseIdentifier())
                }

            }
        }.also {
            if (it !is VariableExpression && it !is NumberLiteralExpression) {
                next()
            }
        }
    }

    private fun parseFunctionCallExpression(): Expression {
        val left = parsePrimaryExpression()
        if (left !is VariableExpression || currentToken != KW_TAKING) {
            return left
        }
        val functionName = left.identifier
        next()
        return FunctionCallExpression(functionName, extractArguments().map { ExpressionParser(it).parseExpression() })
    }

    private fun parseUnaryExpression(): Expression {
        var expr = parseFunctionCallExpression()
        while (currentToken == KW_NOT) {
            next()
            expr = UnaryOperatorExpression(UnaryOperatorExpression.Operator.NOT, expr)
        }
        return expr
    }

    private fun parseMultiplicationDivisionExpression(): Expression {
        var left = parseUnaryExpression()
        while (currentToken in setOf(KW_TIMES, KW_OF, KW_OVER)) {
            val operator = if (currentToken == KW_OVER) DIVIDE else MULTIPLY
            next()
            left = BinaryOperatorExpression(operator, left, parseUnaryExpression())
        }
        return left
    }

    private fun parseAdditionSubtractionExpression(): Expression {
        var left = parseMultiplicationDivisionExpression()
        while (currentToken in setOf(KW_PLUS, KW_WITH, KW_MINUS, KW_WITHOUT)) {
            val operator = if (currentToken == KW_MINUS || currentToken == KW_WITHOUT) SUBTRACT else ADD
            next()
            left = BinaryOperatorExpression(operator, left, parseMultiplicationDivisionExpression())
        }
        return left
    }

    private fun parseComparisonExpression(): Expression {
        var left = parseAdditionSubtractionExpression()
        while (currentToken in setOf(KW_IS, KW_ISNT, KW_AINT)) {
            val operator = when {
                currentToken == KW_AINT || currentToken == KW_ISNT -> NOT_EQUALS
                currentToken == KW_IS && lookahead(1) in GREATER_ALIASES && lookahead(2) == KW_THAN -> {
                    next()
                    next()
                    GREATER
                }
                currentToken == KW_IS && lookahead(1) in LESS_ALIASES && lookahead(2) == KW_THAN -> {
                    next()
                    next()
                    LESS
                }
                currentToken == KW_IS && lookahead(1) == KW_AS && lookahead(2) in GREATER_EQUAL_ALIASES && lookahead(3) == KW_AS -> {
                    next()
                    next()
                    next()
                    GREATER_EQUALS
                }
                currentToken == KW_IS && lookahead(1) == KW_AS && lookahead(2) in LESS_EQUAL_ALIASES && lookahead(3) == KW_AS -> {
                    next()
                    next()
                    next()
                    LESS_EQUALS
                }
                else -> EQUALS
            }
            next()
            left = BinaryOperatorExpression(operator, left, parseAdditionSubtractionExpression())
        }
        return left
    }

    private fun parseLogicalExpression(): Expression {
        var left = parseComparisonExpression()
        while (currentToken in setOf(KW_AND, KW_OR, KW_NOR)) {
            val operator = when (currentToken) {
                KW_AND -> AND
                KW_OR -> OR
                else -> NOR
            }
            next()
            left = BinaryOperatorExpression(operator, left, parseComparisonExpression())
        }
        return left
    }

    private fun parseNumberExpression(): Expression {
        var tmp = ""
        while (currentToken.text.matches(NUMERIC_CHECK) || (currentToken == Garbage('.') && ('.' !in tmp))) {
            tmp += currentToken.text
            next()
        }
        return NumberLiteralExpression(tmp.toDouble())
    }

    private fun extractArguments(): List<List<Token>> {
        val argumentTokens: MutableList<List<Token>> = mutableListOf()
        var start = index
        while (index < tokens.size) {
            if (currentToken in setOf(ExpressionParser.AMPERSAND, ExpressionParser.COMMA, Word("n"))) {
                argumentTokens.add(tokens.subList(start, index))
                if (currentToken == ExpressionParser.COMMA && lookahead(1) == KW_AND) {
                    next()
                }
                next()
                start = index
            } else if (currentToken in PROPER_VARIABLE_TERMINATORS) {
                argumentTokens.add(tokens.subList(start, index))
                break
            } else {
                next()
            }
        }
        return argumentTokens
    }
}