package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

class ContinueStatement: Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitContinueStatement(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContinueStatement) return false
        return true
    }

    override fun hashCode(): Int = 0

    override fun toString(): String = "ContinueStatement()"
}