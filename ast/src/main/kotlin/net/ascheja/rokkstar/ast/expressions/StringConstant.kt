package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

data class StringConstant(val value: String): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitStringConstant(this)
}
