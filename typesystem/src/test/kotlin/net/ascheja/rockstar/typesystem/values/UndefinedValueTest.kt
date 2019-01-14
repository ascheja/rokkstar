package net.ascheja.rockstar.typesystem.values

import net.ascheja.rockstar.typesystem.UndefinedBehaviorException
import net.ascheja.rockstar.typesystem.ValueTest

class UndefinedValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { UndefinedValue.INSTANCE + UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue.INSTANCE - UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue.INSTANCE * UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue.INSTANCE / UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.INSTANCE + NullValue.INSTANCE } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue.INSTANCE - NullValue.INSTANCE } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue.INSTANCE * NullValue.INSTANCE } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue.INSTANCE / NullValue.INSTANCE } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.INSTANCE + StringValue(" world") } shouldSucceedWith StringValue("mysterious world"),
            { UndefinedValue.INSTANCE - StringValue("") } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue.INSTANCE * StringValue("") } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue.INSTANCE / StringValue("") } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.INSTANCE + BooleanValue(false) } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue.INSTANCE - BooleanValue(false) } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue.INSTANCE * BooleanValue(false) } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue.INSTANCE / BooleanValue(false) } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.INSTANCE + NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue.INSTANCE - NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue.INSTANCE * NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue.INSTANCE / NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.INSTANCE.inc() } shouldFailWith UndefinedBehaviorException("++ not defined for undefined"),
            { UndefinedValue.INSTANCE.dec() } shouldFailWith UndefinedBehaviorException("-- not defined for undefined")
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(UndefinedValue.INSTANCE.withConversions("mysterious", false, null))
}