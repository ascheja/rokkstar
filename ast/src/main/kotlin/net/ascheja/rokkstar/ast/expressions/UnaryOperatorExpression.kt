package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

data class UnaryOperatorExpression(val operator: Operator, val expression: Expression): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitUnaryOperatorExpression(this)

    enum class Operator {
        NOT
    }
}