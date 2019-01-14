package net.ascheja.rockstar.typesystem.values

import net.ascheja.rockstar.typesystem.UndefinedBehaviorException
import net.ascheja.rockstar.typesystem.Value

class NullValue private constructor(): Value {
    override fun toBoolean(): Boolean = false

    override fun toNumber(): Double = 0.0

    override fun hashCode(): Int = 0

    override fun equals(other: Any?): Boolean = when (other) {
        is NumberValue -> other.value == 0.0
        is NullValue -> true
        else -> false
    }

    override fun plus(other: Value): Value = when (other) {
        is NullValue -> this
        is NumberValue -> other
        is StringValue -> StringValue(toString() + other.value)
        else -> throw UndefinedBehaviorException("+ not defined for null")
    }

    override fun minus(other: Value): Value = throw UndefinedBehaviorException("- not defined for null")

    override fun times(other: Value): Value = throw UndefinedBehaviorException("* not defined for null")

    override fun div(other: Value): Value = throw UndefinedBehaviorException("/ not defined for null")

    override fun inc(): Value = throw UndefinedBehaviorException("++ not defined for null")

    override fun dec(): Value = throw UndefinedBehaviorException("-- not defined for null")

    override fun compareTo(other: Value): Int = when (other) {
        is NullValue -> 0
        is NumberValue -> 0.0.compareTo(other.value)
        else -> -1
    }

    companion object {
        val INSTANCE = NullValue()
    }

    override fun toString(): String = "null"
}