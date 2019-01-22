package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.expressions.BinaryOperatorExpression.Operator.*
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ExpressionParserTest {

    @Test
    fun `mysterious assignment parsed correctly`() {
        assertEquals(
            UndefinedLiteralExpression(),
            createParser("mysterious").parseExpression()
        )
    }

    @Test
    fun `true assignment parsed correctly`() {
        for (keyword in setOf("true", "right", "yes", "ok")) {
            assertEquals(
                BooleanLiteralExpression(true),
                createParser(keyword).parseExpression()
            )
        }
    }

    @Test
    fun `false assignment parsed correctly`() {
        for (keyword in setOf("false", "wrong", "no", "lies")) {
            assertEquals(
                BooleanLiteralExpression(false),
                createParser(keyword).parseExpression()
            )
        }
    }

    @Test
    fun `null assignment parsed correctly`() {
        for (keyword in setOf("null", "nothing", "nowhere", "nobody", "empty", "gone")) {
            assertEquals(NullLiteralExpression(), createParser(keyword).parseExpression())
        }
    }

    @Test
    fun `string assignment parsed correctly`() {
        assertEquals(
            StringLiteralExpression("some text rockstar doesn't give a shit about"),
            createParser("\"some text rockstar doesn't give a shit about\"").parseExpression()
        )
    }

    @Test
    fun `number assignment parsed correctly`() {
        for ((input, expectedValue) in listOf("2" to 2.0, "0" to 0.0, "0.273" to 0.273, "10.9" to 10.9)) {
            assertEquals(
                NumberLiteralExpression(expectedValue),
                createParser(input).parseExpression()
            )
        }
    }

    @Test
    fun `and parsed correctly`() {
        val expected = BinaryOperatorExpression(
            AND,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("true and true").parseExpression()
        )
    }

    @Test
    fun `or parsed correctly`() {
        val expected = BinaryOperatorExpression(
            OR,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("true or true").parseExpression()
        )
    }

    @Test
    fun `nor parsed correctly`() {
        val expected = BinaryOperatorExpression(
            NOR,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("true nor true").parseExpression()
        )
    }

    @Test
    fun `equal parsed correctly`() {
        val expected = BinaryOperatorExpression(
            EQUALS,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("true is true").parseExpression()
        )
    }

    @Test
    fun `not equal parsed correctly`() {
        val expected = BinaryOperatorExpression(
            NOT_EQUALS,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("ain't", "isn't")) {
            assertEquals(
                expected,
                createParser("true $keyword true").parseExpression()
            )
        }
    }

    @Test
    fun `greater parsed correctly`() {
        val expected = BinaryOperatorExpression(
            GREATER,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("higher", "bigger", "greater", "stronger")) {
            assertEquals(
                expected,
                createParser("true is $keyword than true").parseExpression()
            )
        }
    }

    @Test
    fun `greater equals parsed correctly`() {
        val expected = BinaryOperatorExpression(
            GREATER_EQUALS,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("high", "big", "great", "strong")) {
            assertEquals(
                expected,
                createParser("true is as $keyword as true").parseExpression()
            )
        }
    }

    @Test
    fun `less parsed correctly`() {
        val expected = BinaryOperatorExpression(
            LESS,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("less", "lower", "smaller", "weaker")) {
            assertEquals(
                expected,
                createParser("true is $keyword than true").parseExpression()
            )
        }
    }

    @Test
    fun `less equals parsed correctly`() {
        val expected = BinaryOperatorExpression(
            LESS_EQUALS,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("low", "little", "small", "weak")) {
            assertEquals(
                expected,
                createParser("true is as $keyword as true").parseExpression()
            )
        }
    }

    @Test
    fun `plus,with parsed correctly`() {
        val expected = BinaryOperatorExpression(
            ADD,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("plus", "with")) {
            assertEquals(
                expected,
                createParser("true $keyword true").parseExpression()
            )
        }
    }

    @Test
    fun `minus,without parsed correctly`() {
        val expected = BinaryOperatorExpression(
            SUBTRACT,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("minus", "without")) {
            assertEquals(
                expected,
                createParser("true $keyword true").parseExpression()
            )
        }
    }

    @Test
    fun `times,of parsed correctly`() {
        val expected = BinaryOperatorExpression(
            MULTIPLY,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("times", "of")) {
            assertEquals(
                expected,
                createParser("true $keyword true").parseExpression()
            )
        }
    }

    @Test
    fun `over parsed correctly`() {
        val expected = BinaryOperatorExpression(
            DIVIDE,
            BooleanLiteralExpression(true),
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("true over true").parseExpression()
        )
    }

    @Test
    fun `not parsed correctly`() {
        val expected = UnaryOperatorExpression(
            UnaryOperatorExpression.Operator.NOT,
            BooleanLiteralExpression(true)
        )
        assertEquals(
            expected,
            createParser("not true").parseExpression()
        )
    }

    @Test
    fun `variable parsed correctly`() {
        assertEquals(
            VariableExpression(Identifier("my variable")),
            createParser("my variable").parseExpression()
        )
    }

    @Test
    fun `function call parsed correctly`() {
        val expected = FunctionCallExpression(
            Identifier("my function"),
            listOf(
                VariableExpression(Identifier("my variable")),
                VariableExpression(Identifier("Rock")),
                VariableExpression(Identifier("Roll")),
                NumberLiteralExpression(666.0),
                StringLiteralExpression("Joy"),
                VariableExpression(Identifier("Happiness"))
            )
        )
        assertEquals(
            expected,
            createParser("my function taking my variable, Rock 'n' Roll, 666 & \"Joy\", and Happiness")
                .parseExpression()
        )
    }

    @Test
    fun `function call with dangling separator is an error`() {
        try {
            createParser("my function taking my variable, Rock 'n' Roll, 666 & \"Joy\", and")
                .parseExpression()
            fail()
        } catch (e: ParserException) {
            assertEquals("Dangling separator", e.message)
        }
    }

    @Test
    fun `dangling operand is an error`() {
        try {
            createParser("my variable plus").parseExpression()
            fail()
        } catch (e: ParserException) {
            assertEquals("expected token to be of type WORD, got EOF", e.message)
        }
    }

    private fun createParser(text: String): ExpressionParser = ExpressionParser(Lexer(text).tokens)
}