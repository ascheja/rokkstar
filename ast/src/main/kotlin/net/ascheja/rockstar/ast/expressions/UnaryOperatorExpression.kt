package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression

data class UnaryOperatorExpression(val operator: Operator, val expression: Expression): Expression {

    enum class Operator {
        NOT
    }
}