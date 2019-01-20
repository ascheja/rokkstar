package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.UndefinedBehaviorException
import net.ascheja.rokkstar.typesystem.ValueTest

class NullValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { NullValue.INSTANCE + NullValue.INSTANCE } shouldSucceedWith NullValue.INSTANCE,
            { NullValue.INSTANCE + NumberValue(1.0) } shouldSucceedWith NumberValue(1.0),
            { NullValue.INSTANCE + StringValue(" pointer") } shouldSucceedWith StringValue("null pointer"),
            { NullValue.INSTANCE + BooleanValue(false) } shouldFailWith UndefinedBehaviorException("+ not defined for null"),

            { NullValue.INSTANCE - UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("- not defined for null"),
            { NullValue.INSTANCE * UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("* not defined for null"),
            { NullValue.INSTANCE / UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("/ not defined for null"),

            { NullValue.INSTANCE.inc() } shouldFailWith  UndefinedBehaviorException("++ not defined for null"),
            { NullValue.INSTANCE.dec() } shouldFailWith  UndefinedBehaviorException("-- not defined for null")
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(NullValue.INSTANCE.withConversions("null", false, 0.0))
}