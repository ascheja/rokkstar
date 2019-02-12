package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class AssignmentStatement(val identifier: Identifier, val expression: Expression): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitAssignmentStatement(this)
}