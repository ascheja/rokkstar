package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

data class BooleanConstant(val value: Boolean): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitBooleanConstant(this)
}
