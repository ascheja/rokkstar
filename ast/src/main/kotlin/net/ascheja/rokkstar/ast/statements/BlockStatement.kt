package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

data class BlockStatement(val statements: List<Statement>): Statement, Iterable<Statement> by statements {
    constructor(vararg statements: Statement) : this(statements.toList())
    override fun <T> accept(v: Visitor<out T>): T = v.visitBlockStatement(this)
}