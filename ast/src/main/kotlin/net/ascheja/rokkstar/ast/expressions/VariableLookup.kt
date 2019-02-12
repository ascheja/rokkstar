package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class VariableLookup(val identifier: Identifier): Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitVariableLookup(this)
}