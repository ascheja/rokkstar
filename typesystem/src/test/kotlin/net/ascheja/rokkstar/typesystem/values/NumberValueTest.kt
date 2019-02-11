package net.ascheja.rokkstar.typesystem.values

import net.ascheja.rokkstar.typesystem.ValueTest

class NumberValueTest: ValueTest() {
    override val expressionExamples: Set<ExpressionExample>
        get() = setOf(
            { NumberValue(1.0) + NumberValue(2.0) } shouldSucceedWith NumberValue(3.0),
            { NumberValue(3.0) - NumberValue(2.0) } shouldSucceedWith NumberValue(1.0),
            { NumberValue(2.0) * NumberValue(5.0) } shouldSucceedWith NumberValue(10.0),
            { NumberValue(10.0) / NumberValue(2.0) } shouldSucceedWith NumberValue(5.0),
            { NumberValue(10.0) / NumberValue(0.0) } shouldSucceedWith UndefinedValue.INSTANCE,

            { NumberValue(1.0) + StringValue("2") } shouldSucceedWith StringValue("12"),
            { NumberValue(2.0) * StringValue("3") } shouldSucceedWith StringValue("33"),

            { NumberValue(1.0) + NullValue } shouldSucceedWith NumberValue(1.0),
            { NumberValue(3.0) - NullValue } shouldSucceedWith NumberValue(3.0),
            { NumberValue(2.0) * NullValue } shouldSucceedWith NumberValue(0.0),
            { NumberValue(10.0) / NullValue } shouldSucceedWith UndefinedValue.INSTANCE,

            { NumberValue(1.0).inc() } shouldSucceedWith NumberValue(2.0),
            { NumberValue(1.0).dec() } shouldSucceedWith NumberValue(0.0)
        )
    override val conversionExamples: Set<ConversionExample>
        get() = setOf(
            NumberValue(0.0).withConversions("0", false, 0.0),
            NumberValue(1.0).withConversions("1", true, 1.0)
        )
}