package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression

class NullLiteralExpression: Expression {
    override fun toString(): String = "NullLiteralExpression()"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NullLiteralExpression) return false
        return true
    }

    override fun hashCode(): Int = 0
}
