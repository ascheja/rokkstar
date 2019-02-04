package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression

class NullConstant: Expression {
    override fun toString(): String = "NullConstant()"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NullConstant) return false
        return true
    }

    override fun hashCode(): Int = 0
}
