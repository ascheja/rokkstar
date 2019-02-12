package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class ReadLineStatement(val identifier: Identifier): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitReadLineStatement(this)
}