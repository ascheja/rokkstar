package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class StatementParserTest {

    @Test
    fun `say,whisper,scream,shout statements parsed correctly`() {
        val expected = PrintLineStatement(VariableExpression(Identifier("my identifier")))
        assertEquals(expected, createParser().parseStatement("Say my identifier"))
        assertEquals(expected, createParser().parseStatement("Whisper my identifier"))
        assertEquals(expected, createParser().parseStatement("Scream my identifier"))
        assertEquals(expected, createParser().parseStatement("Shout my identifier"))
    }

    @Test
    fun `Listen to parsed correctly`() {
        assertEquals(
            ReadLineStatement(Identifier("the music")),
            createParser().parseStatement("Listen to the music")
        )
    }

    @Test
    fun `Put into parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("your heart"), VariableExpression(Identifier("Everything"))),
            createParser().parseStatement("Put Everything into your heart")
        )
    }

    @Test
    fun `Poetic string literal assignment parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("Your heart"), StringLiteralExpression(";!bla bla???<>")),
            createParser().parseStatement("Your heart says ;!bla bla???<>")
        )
    }

    @Test
    fun `Poetic number literal assignment parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("Tommy"), NumberLiteralExpression(14487.0)),
            createParser().parseStatement("Tommy was a lean, mean wrecking machine.")
        )
    }

    @Test
    fun `mysterious assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            UndefinedLiteralExpression()
        )
        assertEquals(expected, createParser().parseStatement("variable is mysterious"))
    }

    @Test
    fun `true assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("true", "right", "yes", "ok")) {
            assertEquals(expected, createParser().parseStatement("variable is $keyword"))
        }
    }

    @Test
    fun `false assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            BooleanLiteralExpression(false)
        )
        for (keyword in setOf("false", "wrong", "no", "lies")) {
            assertEquals(expected, createParser().parseStatement("variable is $keyword"))
        }
    }

    @Test
    fun `null assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            NullLiteralExpression()
        )
        for (keyword in setOf("null", "nothing", "nowhere", "nobody", "empty", "gone")) {
            assertEquals(expected, createParser().parseStatement("variable is $keyword"))
        }
    }

    @Test
    fun `string assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            StringLiteralExpression("some text rockstar doesn't give a shit about")
        )
        assertEquals(
            expected,
            createParser().parseStatement("variable is \"some text rockstar doesn't give a shit about\"")
        )
    }

    @Test
    fun `number assignment parsed correctly`() {
        for ((input, expectedValue) in listOf("2" to 2.0, "0" to 0.0, "0.273" to 0.273, "10.9" to 10.9)) {
            val expected = AssignmentStatement(
                Identifier("variable"),
                NumberLiteralExpression(expectedValue)
            )
            assertEquals(
                expected,
                createParser().parseStatement("variable is $input")
            )
        }
    }

    @Test
    fun `If without else parsed correctly`() {
        val identifier = Identifier("my name")
        assertEquals(
            IfStatement(
                BinaryOperatorExpression(
                    BinaryOperatorExpression.Operator.EQUALS,
                    VariableExpression(identifier),
                    NullLiteralExpression()
                ),
                BlockStatement(ReadLineStatement(identifier))
            ),
            createParser().parseStatement("If my name is nothing\nListen to my name")
        )
    }

    @Test
    fun `If with else parsed correctly`() {
        val identifier = Identifier("my name")
        assertEquals(
            IfStatement(
                BinaryOperatorExpression(
                    BinaryOperatorExpression.Operator.EQUALS,
                    VariableExpression(identifier),
                    NullLiteralExpression()
                ),
                BlockStatement(ReadLineStatement(identifier)),
                BlockStatement(PrintLineStatement(VariableExpression(identifier)))
            ),
            createParser().parseStatement("If my name is nothing\nListen to my name\nElse\nShout my name")
        )
    }

    @Test
    fun `While statements parsed correctly`() {
        val expected = WhileLoopStatement(
            BooleanLiteralExpression(true),
            BlockStatement(PrintLineStatement(NullLiteralExpression()))
        )
        assertEquals(expected, createParser().parseStatement("While true\nSay nothing"))
    }

    @Test
    fun `Until statements parsed correctly`() {
        val expected = UntilLoopStatement(
            BooleanLiteralExpression(true),
            BlockStatement(PrintLineStatement(NullLiteralExpression()))
        )
        assertEquals(expected, createParser().parseStatement("Until true\nSay nothing"))
    }

    @Test
    fun `function call parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("Large"),
            FunctionCallExpression(
                Identifier("Multiply"),
                listOf(
                    NumberLiteralExpression(3.0),
                    NumberLiteralExpression(5.0),
                    NumberLiteralExpression(9.0)
                )
            )
        )
        assertEquals(expected, createParser().parseStatement("Put Multiply taking 3, 5, and 9 into Large"))
    }

    @Test
    fun `function declaration is parsed correctly`() {
        val expected = FunctionDeclaration(
            Identifier("Search"),
            listOf(Identifier("Needle"), Identifier("Haystack")),
            BlockStatement(listOf(ReturnStatement(VariableExpression(Identifier("Needle")))))
        )
        assertEquals(expected, createParser().parseStatement("Search takes Needle and Haystack\nGive back Needle"))
    }

    @Test
    fun `pronouns are substituted with last parsed variable name`() {
        val delegate = LastNameDelegate("your heart")
        val parser = StatementParser(delegate)
        val expected = AssignmentStatement(
            Identifier("my heart"),
            VariableExpression(Identifier("your heart"))
        )
        assertEquals(
            expected,
            parser.parseStatement("Put it into my heart")
        )
        assertEquals(
            "my heart",
            delegate.value
        )
    }

    private fun createParser(): StatementParser = StatementParser()

    private fun StatementParser.parseStatement(text: String): Statement {
        return parseStatement(Lexer(text).toTokenSource())
    }
}