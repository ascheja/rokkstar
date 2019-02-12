package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor

data class FunctionDeclaration(val identifier: Identifier, val parameters: List<Identifier>, val body: BlockStatement): Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitFunctionDeclaration(this)
}