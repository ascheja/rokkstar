package net.ascheja.rockstar.typesystem.values

import net.ascheja.rockstar.typesystem.UndefinedBehaviorException
import net.ascheja.rockstar.typesystem.ValueTest

class BooleanValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { BooleanValue(true) +  UndefinedValue.INSTANCE} shouldFailWith UndefinedBehaviorException("+ not defined for boolean"),
            { BooleanValue(true) -  UndefinedValue.INSTANCE} shouldFailWith UndefinedBehaviorException("- not defined for boolean"),
            { BooleanValue(true) *  UndefinedValue.INSTANCE} shouldFailWith UndefinedBehaviorException("* not defined for boolean"),
            { BooleanValue(true) /  UndefinedValue.INSTANCE} shouldFailWith UndefinedBehaviorException("/ not defined for boolean"),

            { BooleanValue(true) +  NumberValue(0.0)} shouldFailWith UndefinedBehaviorException("+ not defined for boolean"),
            { BooleanValue(true) -  NumberValue(0.0)} shouldFailWith UndefinedBehaviorException("- not defined for boolean"),
            { BooleanValue(true) *  NumberValue(0.0)} shouldFailWith UndefinedBehaviorException("* not defined for boolean"),
            { BooleanValue(true) /  NumberValue(0.0)} shouldFailWith UndefinedBehaviorException("/ not defined for boolean"),

            { BooleanValue(true) +  StringValue(" story")} shouldSucceedWith StringValue("true story"),
            { BooleanValue(false) +  StringValue(" friends")} shouldSucceedWith StringValue("false friends"),
            { BooleanValue(true) -  StringValue("")} shouldFailWith UndefinedBehaviorException("- not defined for boolean"),
            { BooleanValue(true) *  StringValue("")} shouldFailWith UndefinedBehaviorException("* not defined for boolean"),
            { BooleanValue(true) /  StringValue("")} shouldFailWith UndefinedBehaviorException("/ not defined for boolean")
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(
            BooleanValue(true).withConversions("true", true, 1.0),
            BooleanValue(false).withConversions("false", false, 0.0)
        )
}