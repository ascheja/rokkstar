package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.UndefinedBehaviorException
import net.ascheja.rokkstar.typesystem.ValueTest

class UndefinedValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { UndefinedValue + UndefinedValue } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue - UndefinedValue } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue * UndefinedValue } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue / UndefinedValue } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue + NullValue } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue - NullValue } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue * NullValue } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue / NullValue } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue + StringValue(" world") } shouldSucceedWith StringValue("mysterious world"),
            { UndefinedValue - StringValue("") } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue * StringValue("") } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue / StringValue("") } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue + BooleanValue(false) } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue - BooleanValue(false) } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue * BooleanValue(false) } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue / BooleanValue(false) } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue + NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("+ not defined for undefined"),
            { UndefinedValue - NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("- not defined for undefined"),
            { UndefinedValue * NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("* not defined for undefined"),
            { UndefinedValue / NumberValue(0.0) } shouldFailWith UndefinedBehaviorException("/ not defined for undefined"),

            { UndefinedValue.inc() } shouldFailWith UndefinedBehaviorException("++ not defined for undefined"),
            { UndefinedValue.dec() } shouldFailWith UndefinedBehaviorException("-- not defined for undefined")
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(UndefinedValue.withConversions("mysterious", false, null))
}