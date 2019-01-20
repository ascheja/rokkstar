package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression

data class BinaryOperatorExpression(val operator: Operator, val left: Expression, val right: Expression): Expression {

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