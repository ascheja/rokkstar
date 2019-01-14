package net.ascheja.rockstar.typesystem.values

import net.ascheja.rockstar.typesystem.UndefinedBehaviorException
import net.ascheja.rockstar.typesystem.Value
import net.ascheja.rockstar.typesystem.ValueConversionException

class StringValue(val value: String): Value {

    companion object {
        internal val KNOWN_TRUE_ALIASES = setOf("true", "right", "yes", "ok")
    }

    override fun toNumber(): Double = try {
        value.toDouble()
    } catch (e: NumberFormatException) {
        throw ValueConversionException(e.message!!)
    }

    override fun toString(): String = value

    override fun toBoolean() = true

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = when (other) {
        is StringValue -> value == other.value
        is NumberValue -> try {
            toNumber() == other.toNumber()
        } catch (e: NumberFormatException) {
            false
        }
        is BooleanValue -> (value.toLowerCase() in KNOWN_TRUE_ALIASES) && other.toBoolean()
        else -> false
    }

    override fun plus(other: Value): Value = StringValue(value + other.toString())

    override fun minus(other: Value): Value = throw UndefinedBehaviorException("- not defined for strings")

    override fun times(other: Value): Value = when (other) {
        is NumberValue -> StringValue(value.repeat(other.toNumber().toInt()))
        else -> throw UndefinedBehaviorException("* not defined for strings")
    }

    override fun div(other: Value): Value  = throw UndefinedBehaviorException("/ not defined for strings")

    override fun inc(): Value = throw UndefinedBehaviorException("++ not defined for strings")

    override fun dec(): Value = throw UndefinedBehaviorException("-- not defined for strings")

    override fun compareTo(other: Value): Int = when (other) {
        is StringValue -> value.compareTo(other.value)
        else -> -1
    }
}