package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

class NullConstant: Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitNullConstant(this)

    override fun toString(): String = "NullConstant()"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NullConstant) return false
        return true
    }

    override fun hashCode(): Int = 0
}
