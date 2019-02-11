package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.UndefinedBehaviorException
import net.ascheja.rokkstar.typesystem.ValueTest

class NullValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { NullValue + NullValue } shouldSucceedWith NullValue,
            { NullValue + NumberValue(1.0) } shouldSucceedWith NumberValue(1.0),
            { NullValue + StringValue(" pointer") } shouldSucceedWith StringValue("null pointer"),
            { NullValue + BooleanValue(false) } shouldFailWith UndefinedBehaviorException("+ not defined for null"),

            { NullValue - UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("- not defined for null"),
            { NullValue * UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("* not defined for null"),
            { NullValue / UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("/ not defined for null"),

            { NullValue.inc() } shouldSucceedWith NumberValue(1.0),
            { NullValue.dec() } shouldSucceedWith NumberValue(-1.0)
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(NullValue.withConversions("null", false, 0.0))
}