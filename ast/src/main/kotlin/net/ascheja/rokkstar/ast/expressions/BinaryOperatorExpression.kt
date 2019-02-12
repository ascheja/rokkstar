package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

data class BinaryOperatorExpression(val operator: Operator, val left: Expression, val right: Expression): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitBinaryOperatorExpression(this)

    enum class Operator {
        MULTIPLY,
        DIVIDE,

        ADD,
        SUBTRACT,

        EQUALS,
        NOT_EQUALS,
        LESS,
        LESS_EQUALS,
        GREATER,
        GREATER_EQUALS,

        AND,
        OR,
        NOR
    }
}