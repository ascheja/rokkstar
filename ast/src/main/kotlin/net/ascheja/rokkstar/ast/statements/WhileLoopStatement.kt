package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

data class WhileLoopStatement(val condition: Expression, val body: BlockStatement): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitWhileLoopStatement(this)
}