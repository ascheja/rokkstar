package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Visitor

class UndefinedConstant: Expression {
    override fun <T> accept(v: Visitor<out T>): T = v.visitUndefinedConstant(this)

    override fun toString(): String = "UndefinedConstant()"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UndefinedConstant) return false
        return true
    }

    override fun hashCode(): Int = 0
}
