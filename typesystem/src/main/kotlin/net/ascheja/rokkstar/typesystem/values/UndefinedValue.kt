package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.UndefinedBehaviorException
import net.ascheja.rokkstar.typesystem.Value
import net.ascheja.rokkstar.typesystem.ValueConversionException

class UndefinedValue private constructor(): Value {

    companion object {
        val INSTANCE = UndefinedValue()
    }

    override fun toBoolean(): Boolean = false

    override fun toNumber(): Double = throw ValueConversionException(
        "Cannot convert undefined to number"
    )

    override fun toString(): String = "mysterious"

    override fun hashCode(): Int = 0

    override fun equals(other: Any?): Boolean = other is UndefinedValue

    override fun plus(other: Value): Value = when (other) {
        is StringValue -> StringValue(toString() + other.value)
        else -> throw UndefinedBehaviorException("+ not defined for undefined")
    }

    override fun minus(other: Value): Value = throw UndefinedBehaviorException("- not defined for undefined")

    override fun times(other: Value): Value = throw UndefinedBehaviorException("* not defined for undefined")

    override fun div(other: Value): Value = throw UndefinedBehaviorException("/ not defined for undefined")

    override fun inc(): Value = throw UndefinedBehaviorException("++ not defined for undefined")

    override fun dec(): Value = throw UndefinedBehaviorException("-- not defined for undefined")

    override fun compareTo(other: Value): Int = when (other) {
        is UndefinedValue -> 0
        else -> -1
    }
}