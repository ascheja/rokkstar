package net.ascheja.rokkstar.ast

import net.ascheja.rokkstar.ast.statements.BlockStatement

data class Program(val root: BlockStatement): Visitable {
    override fun <T> accept(v: Visitor<out T>): T = v.visitProgram(this)
}