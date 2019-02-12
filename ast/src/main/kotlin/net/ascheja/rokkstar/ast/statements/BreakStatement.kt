package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Visitor

class BreakStatement: Statement {
    override fun <T> accept(v: Visitor<out T>): T = v.visitBreakStatement(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BreakStatement) return false
        return true
    }

    override fun hashCode(): Int = 0

    override fun toString(): String = "BreakStatement()"
}