package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class FunctionCallExpression(val identifier: Identifier, val arguments: List<Expression>): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitFunctionCallExpression(this)
}