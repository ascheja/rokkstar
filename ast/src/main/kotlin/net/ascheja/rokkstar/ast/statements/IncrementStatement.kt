package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class IncrementStatement(val identifier: Identifier, val amount: Int): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitIncrementStatement(this)
}