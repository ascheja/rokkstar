package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.expressions.BinaryOperatorExpression.Operator.*
import net.ascheja.rokkstar.parser.Token.*

class ExpressionParser internal constructor(lastNameDelegate: LastNameDelegate): BaseParser(lastNameDelegate) {

    constructor(): this(LastNameDelegate())

    fun parseExpression(source: TokenSource): Expression {
        return parseLogicalExpression(source)
    }

    private fun parseLogicalExpression(source: TokenSource): Expression {
        var left = parseComparisonExpression(source)
        while (source.current in setOf(KW_AND, KW_OR, KW_NOR)) {
            val operator = when (source.current) {
                KW_AND -> AND
                KW_OR -> OR
                else -> NOR
            }
            source.next()
            left = BinaryOperatorExpression(operator, left, parseComparisonExpression(source))
        }
        return left
    }

    private fun parseComparisonExpression(source: TokenSource): Expression {
        var left = parseAdditionSubtractionExpression(source)
        while (source.current in setOf(KW_IS, KW_ISNT, KW_AINT)) {
            val operator = when {
                source.current == KW_AINT || source.current == KW_ISNT -> NOT_EQUALS
                source.current == KW_IS && source.lookahead(1) in GREATER_ALIASES && source.lookahead(2) == KW_THAN -> {
                    source.next()
                    source.next()
                    GREATER
                }
                source.current == KW_IS && source.lookahead(1) in LESS_ALIASES && source.lookahead(2) == KW_THAN -> {
                    source.next()
                    source.next()
                    LESS
                }
                source.current == KW_IS && source.lookahead(1) == KW_AS && source.lookahead(2) in GREATER_EQUAL_ALIASES && source.lookahead(3) == KW_AS -> {
                    source.next()
                    source.next()
                    source.next()
                    GREATER_EQUALS
                }
                source.current == KW_IS && source.lookahead(1) == KW_AS && source.lookahead(2) in LESS_EQUAL_ALIASES && source.lookahead(3) == KW_AS -> {
                    source.next()
                    source.next()
                    source.next()
                    LESS_EQUALS
                }
                else -> EQUALS
            }
            source.next()
            left = BinaryOperatorExpression(operator, left, parseAdditionSubtractionExpression(source))
        }
        return left
    }

    private fun parseAdditionSubtractionExpression(source: TokenSource): Expression {
        var left = parseMultiplicationDivisionExpression(source)
        while (source.current in setOf(KW_PLUS, KW_WITH, KW_MINUS, KW_WITHOUT)) {
            val operator = if (source.current == KW_MINUS || source.current == KW_WITHOUT) SUBTRACT else ADD
            source.next()
            left = BinaryOperatorExpression(operator, left, parseMultiplicationDivisionExpression(source))
        }
        return left
    }

    private fun parseMultiplicationDivisionExpression(source: TokenSource): Expression {
        var left = parseUnaryExpression(source)
        while (source.current in setOf(KW_TIMES, KW_OF, KW_OVER)) {
            val operator = if (source.current == KW_OVER) DIVIDE else MULTIPLY
            source.next()
            left = BinaryOperatorExpression(operator, left, parseUnaryExpression(source))
        }
        return left
    }

    private fun parseUnaryExpression(source: TokenSource): Expression {
        while (source.current == KW_NOT) {
            source.next()
            return UnaryOperatorExpression(UnaryOperatorExpression.Operator.NOT, parseUnaryExpression(source))
        }
        return parseFunctionCallExpression(source)
    }

    private fun parseFunctionCallExpression(source: TokenSource): Expression {
        val left = parsePrimaryExpression(source)
        if (left !is VariableLookup || source.current != KW_TAKING) {
            return left
        }
        val functionName = left.identifier
        source.next()
        return FunctionCallExpression(functionName, extractArguments(source).map { parseExpression(it) })
    }

    private fun parsePrimaryExpression(source: TokenSource): Expression {
        return when (source.current) {
            is StringLiteral -> StringConstant(source.current.text)
            KW_MYSTERIOUS -> UndefinedConstant()
            in NULL_ALIASES -> NullConstant()
            in TRUE_ALIASES -> BooleanConstant(true)
            in FALSE_ALIASES -> BooleanConstant(false)
            else -> {
                if (source.current.text.matches(NUMERIC_CHECK)) {
                    parseNumberExpression(source)
                } else {
                    VariableLookup(parseIdentifier(source))
                }

            }
        }.also {
            if (it !is VariableLookup && it !is NumberConstant) {
                source.next()
            }
        }
    }

    private fun parseNumberExpression(source: TokenSource): Expression {
        var tmp = ""
        while (source.current.text.matches(NUMERIC_CHECK) || (source.current == Garbage('.') && ('.' !in tmp))) {
            tmp += source.current.text
            source.next()
        }
        return NumberConstant(tmp.toDouble())
    }

    private fun extractArguments(source: TokenSource): List<TokenSource> {
        val argumentTokens: MutableList<TokenSource> = mutableListOf()
        var start = source.index
        var danglingSeparator = true
        while (source.current != Eof) {
            if (source.current in setOf(AMPERSAND, COMMA, Word("n"))) {
                argumentTokens.add(source.subList(start, source.index))
                danglingSeparator = true
                if (source.current == COMMA && source.lookahead(1) == KW_AND) {
                    source.next()
                }
                source.next()
                start = source.index
            } else if (source.current in PROPER_VARIABLE_TERMINATORS) {
                argumentTokens.add(source.subList(start, source.index))
                danglingSeparator = false
                break
            } else {
                if (source.next() is Eof) {
                    argumentTokens.add(source.subList(start, source.index))
                }
                danglingSeparator = false
            }
        }
        if (danglingSeparator) {
            throw ParserException("Dangling separator${source.current.getPositionInfo()}")
        }
        return argumentTokens
    }
}