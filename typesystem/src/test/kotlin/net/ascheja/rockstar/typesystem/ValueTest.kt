package net.ascheja.rockstar.typesystem

import net.ascheja.rockstar.typesystem.values.*
import org.junit.Assert.*
import org.junit.Test

typealias Expression = () -> Value

abstract class ValueTest {

    abstract val conversionExamples: Set<ConversionExample>

    abstract val expressionExamples: Set<ExpressionExample>

    data class ConversionExample(val value: Value, val asString: String, val asBoolean: Boolean, val asNumber: Double?)

    data class ExpressionExample(val action: Expression, val expectedResult: Any)

    protected fun Value.withConversions(asString: String, asBoolean: Boolean, asNumber: Double? = null) : ConversionExample {
        return ConversionExample(this, asString, asBoolean, asNumber)
    }

    protected infix fun Expression.shouldFailWith(e: Exception): ExpressionExample {
        return ExpressionExample(this, e)
    }

    protected infix fun Expression.shouldSucceedWith(value: Value): ExpressionExample {
        return ExpressionExample(this, value)
    }

    @Test
    fun `string representation`() {
        for (example in conversionExamples) {
            assertEquals(example.asString, example.value.toString())
        }
    }

    @Test
    fun truthiness() {
        for (example in conversionExamples) {
            assertEquals(example.asBoolean, example.value.toBoolean())
        }
    }

    @Test
    fun `numeric representation`() {
        for (example in conversionExamples) {
            if (example.asNumber == null) {
                assertTrue(try { example.value.toNumber(); false } catch (e: ValueConversionException) { true })
            } else {
                assertEquals(example.asNumber, example.value.toNumber(), 0.0)
            }
        }
    }

    @Test
    fun expressions() {
        for ((idx, example) in expressionExamples.withIndex()) {
            if (example.expectedResult is Exception) {
                try {
                    example.action.invoke()
                    fail("#$idx: result should be an exception")
                } catch (e: Exception) {
                    assertEquals(example.expectedResult.message, e.message)
                }
            } else {
                val result = example.action.invoke()
                when (example.expectedResult) {
                    is UndefinedValue -> {
                        assertTrue("#$idx: result should be undefined", result is UndefinedValue)
                    }
                    is NullValue -> {
                        assertTrue("#$idx: result should be null", result is NullValue)
                    }
                    is BooleanValue -> {
                        assertTrue("#$idx: result should be boolean", result is BooleanValue)
                        assertEquals(
                            "#$idx: not matching expected value",
                            example.expectedResult.value,
                            (result as BooleanValue).value
                        )
                    }
                    is NumberValue -> {
                        assertTrue("#$idx: result should be a number", result is NumberValue)
                        assertEquals(
                            "#$idx: not matching expected value",
                            example.expectedResult.value,
                            (result as NumberValue).value,
                            0.0
                        )
                    }
                    is StringValue -> {
                        assertTrue("#$idx: result should be a string", result is StringValue)
                        assertEquals(
                            "#$idx: not matching expected value",
                            example.expectedResult.value,
                            (result as StringValue).value
                        )
                    }
                }
            }
        }
    }
}