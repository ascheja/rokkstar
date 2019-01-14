package net.ascheja.rockstar.ast

class FunctionName(val value: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FunctionName) return false

        return value.toLowerCase() == other.value.toLowerCase()
    }

    override fun hashCode(): Int {
        return value.toLowerCase().hashCode()
    }
}