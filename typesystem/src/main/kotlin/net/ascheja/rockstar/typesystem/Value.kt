package net.ascheja.rockstar.typesystem

import net.ascheja.rockstar.typesystem.values.BooleanValue

interface Value: Comparable<Value> {
    fun toBoolean(): Boolean

    fun toNumber(): Double

    override fun toString(): String

    override fun equals(other: Any?): Boolean

    operator fun plus(other: Value): Value

    operator fun minus(other: Value): Value

    operator fun times(other: Value): Value

    operator fun div(other: Value): Value

    operator fun not(): Value = BooleanValue(!toBoolean())

    operator fun inc(): Value

    operator fun dec(): Value
}