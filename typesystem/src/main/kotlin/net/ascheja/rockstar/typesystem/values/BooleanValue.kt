package net.ascheja.rockstar.typesystem.values

import net.ascheja.rockstar.typesystem.UndefinedBehaviorException
import net.ascheja.rockstar.typesystem.Value

class BooleanValue(val value: Boolean): Value {

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = when (other) {
        is BooleanValue -> value == other.value
        is NullValue -> other.toBoolean() == value
        is NumberValue -> other.toBoolean() == value
        is StringValue -> (other.toString().toLowerCase() in StringValue.KNOWN_TRUE_ALIASES) && value
        else -> false
    }

    override fun plus(other: Value): Value = when (other) {
        is StringValue -> StringValue(toString() + other.value)
        else -> throw UndefinedBehaviorException("+ not defined for boolean")
    }

    override fun minus(other: Value): Value = throw UndefinedBehaviorException("- not defined for boolean")

    override fun times(other: Value): Value = throw UndefinedBehaviorException("* not defined for boolean")

    override fun div(other: Value): Value = throw UndefinedBehaviorException("/ not defined for boolean")

    override fun inc(): Value = not()

    override fun dec(): Value = not()

    override fun compareTo(other: Value): Int = throw UndefinedBehaviorException("comparison between booleans not allowed")

    override fun toBoolean(): Boolean = value

    override fun toNumber(): Double = if (value) 1.0 else 0.0

    override fun toString(): String = if (value) "true" else "false"
}