package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.UndefinedBehaviorException
import net.ascheja.rokkstar.typesystem.ValueTest

class StringValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { StringValue("Hello ") + StringValue("World!") } shouldSucceedWith StringValue("Hello World!"),
            { StringValue("some") - StringValue("some") } shouldFailWith UndefinedBehaviorException("- not defined for strings"),
            { StringValue("some") * StringValue("some") } shouldFailWith UndefinedBehaviorException("* not defined for strings"),
            { StringValue("some") / StringValue("some") } shouldFailWith UndefinedBehaviorException("/ not defined for strings"),

            { StringValue("2") + NumberValue(2.0)} shouldSucceedWith StringValue("22"),
            { StringValue("some") - NumberValue(2.0) } shouldFailWith UndefinedBehaviorException("- not defined for strings"),
            { StringValue("2") * NumberValue(2.0) } shouldSucceedWith StringValue("22"),
            { StringValue("some") / NumberValue(2.0) } shouldFailWith UndefinedBehaviorException("/ not defined for strings"),

            { StringValue("Not ") + BooleanValue(true) } shouldSucceedWith StringValue("Not true"),
            { StringValue("Not ") + BooleanValue(false) } shouldSucceedWith StringValue("Not false"),
            { StringValue("some") - BooleanValue(false) } shouldFailWith UndefinedBehaviorException("- not defined for strings"),
            { StringValue("some") * BooleanValue(false) } shouldFailWith UndefinedBehaviorException("* not defined for strings"),
            { StringValue("some") / BooleanValue(false) } shouldFailWith UndefinedBehaviorException("/ not defined for strings"),

            { StringValue("Not ") + NullValue } shouldSucceedWith StringValue("Not null"),
            { StringValue("some") - NullValue } shouldFailWith UndefinedBehaviorException("- not defined for strings"),
            { StringValue("some") * NullValue } shouldFailWith UndefinedBehaviorException("* not defined for strings"),
            { StringValue("some") / NullValue } shouldFailWith UndefinedBehaviorException("/ not defined for strings"),

            { StringValue("Not ") + UndefinedValue.INSTANCE } shouldSucceedWith StringValue("Not mysterious"),
            { StringValue("some") - UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("- not defined for strings"),
            { StringValue("some") * UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("* not defined for strings"),
            { StringValue("some") / UndefinedValue.INSTANCE } shouldFailWith UndefinedBehaviorException("/ not defined for strings")
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(
            StringValue("true").withConversions("true", true),
            StringValue("false").withConversions("false", true),
            StringValue("some value").withConversions("some value", true),
            StringValue("3.14").withConversions("3.14", true, 3.14),
            StringValue("42").withConversions("42", true, 42.0)
        )
}