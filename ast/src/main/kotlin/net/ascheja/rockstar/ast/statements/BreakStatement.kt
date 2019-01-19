package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement

class BreakStatement: Statement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BreakStatement) return false
        return true
    }

    override fun hashCode(): Int = 0

    override fun toString(): String = "BreakStatement()"
}