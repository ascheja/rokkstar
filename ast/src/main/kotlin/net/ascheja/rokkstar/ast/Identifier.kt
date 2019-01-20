package net.ascheja.rokkstar.ast

class Identifier(val value: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Identifier) return false

        return value.toLowerCase() == other.value.toLowerCase()
    }

    override fun hashCode(): Int {
        return value.toLowerCase().hashCode()
    }

    override fun toString(): String = value
}