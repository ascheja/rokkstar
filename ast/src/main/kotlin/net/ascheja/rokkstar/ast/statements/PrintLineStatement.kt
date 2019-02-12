package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

data class PrintLineStatement(val expression: Expression): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitPrintLineStatement(this)
}