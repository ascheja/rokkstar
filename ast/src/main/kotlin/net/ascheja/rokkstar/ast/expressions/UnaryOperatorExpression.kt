package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression

data class UnaryOperatorExpression(val operator: Operator, val expression: Expression): Expression {

    enum class Operator {
        NOT
    }
}