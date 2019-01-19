package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement

class ContinueStatement: Statement {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContinueStatement) return false
        return true
    }

    override fun hashCode(): Int = 0

    override fun toString(): String = "ContinueStatement()"
}