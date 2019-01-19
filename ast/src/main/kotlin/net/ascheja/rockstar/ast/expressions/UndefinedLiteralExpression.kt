package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression

class UndefinedLiteralExpression: Expression {
    override fun toString(): String = "UndefinedLiteralExpression()"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UndefinedLiteralExpression) return false
        return true
    }

    override fun hashCode(): Int = 0
}
