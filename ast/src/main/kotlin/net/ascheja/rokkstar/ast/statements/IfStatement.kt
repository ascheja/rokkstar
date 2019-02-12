package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

data class IfStatement(val condition: Expression, val thenBlock: BlockStatement, val elseBlock: BlockStatement? = null): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitIfStatement(this)
}
