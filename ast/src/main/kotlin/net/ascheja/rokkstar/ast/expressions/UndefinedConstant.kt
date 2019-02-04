package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression

class UndefinedConstant: Expression {
    override fun toString(): String = "UndefinedConstant()"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UndefinedConstant) return false
        return true
    }

    override fun hashCode(): Int = 0
}
