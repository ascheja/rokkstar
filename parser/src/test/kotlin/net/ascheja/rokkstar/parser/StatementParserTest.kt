package net.ascheja.rokkstar.parser

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*
import org.junit.Assert.assertEquals
import org.junit.Test

class StatementParserTest {

    @Test
    fun `say,whisper,scream,shout statements parsed correctly`() {
        val expected = PrintLineStatement(VariableExpression(Identifier("my identifier")))
        assertEquals(expected, createParser("Say my identifier").parseStatement())
        assertEquals(expected, createParser("Whisper my identifier").parseStatement())
        assertEquals(expected, createParser("Scream my identifier").parseStatement())
        assertEquals(expected, createParser("Shout my identifier").parseStatement())
    }

    @Test
    fun `Listen to parsed correctly`() {
        assertEquals(
            ReadLineStatement(Identifier("the music")),
            createParser("Listen to the music").parseStatement()
        )
    }

    @Test
    fun `Put into parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("your heart"), VariableExpression(Identifier("Everything"))),
            createParser("Put Everything into your heart").parseStatement()
        )
    }

    @Test
    fun `Poetic string literal assignment parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("Your heart"), StringLiteralExpression(";!bla bla???<>")),
            createParser("Your heart says ;!bla bla???<>").parseStatement()
        )
    }

    @Test
    fun `Poetic number literal assignment parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("Tommy"), NumberLiteralExpression(14487.0)),
            createParser("Tommy was a lean, mean wrecking machine.").parseStatement()
        )
    }

    @Test
    fun `mysterious assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            UndefinedLiteralExpression()
        )
        assertEquals(expected, createParser("variable is mysterious").parseStatement())
    }

    @Test
    fun `true assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            BooleanLiteralExpression(true)
        )
        for (keyword in setOf("true", "right", "yes", "ok")) {
            assertEquals(expected, createParser("variable is $keyword").parseStatement())
        }
    }

    @Test
    fun `false assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            BooleanLiteralExpression(false)
        )
        for (keyword in setOf("false", "wrong", "no", "lies")) {
            assertEquals(expected, createParser("variable is $keyword").parseStatement())
        }
    }

    @Test
    fun `null assignment parsed correctly`() {
        val expected = AssignmentStatement(
            Identifier("variable"),
            NullLiteralExpression()
        )
        for (keyword in setOf("null", "nothing", "nowhere", "nobody", "empty", "gone")) {
            assertEquals(expected, createParser("variable is $keyword").parseStatement())
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
            createParser("variable is \"some text rockstar doesn't give a shit about\"").parseStatement()
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
                createParser("variable is $input").parseStatement()
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
                BlockStatement(listOf(ReadLineStatement(identifier))),
                null
            ),
            createParser("If my name is nothing\nListen to my name").parseStatement()
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
                BlockStatement(listOf(ReadLineStatement(identifier))),
                BlockStatement(listOf(PrintLineStatement(VariableExpression(identifier))))
            ),
            createParser("If my name is nothing\nListen to my name\nElse\nShout my name").parseStatement()
        )
    }

    @Test
    fun `While statements parsed correctly`() {
        val expected = WhileLoopStatement(
            BooleanLiteralExpression(true),
            BlockStatement(listOf(PrintLineStatement(NullLiteralExpression())))
        )
        assertEquals(expected, createParser("While true\nSay nothing").parseStatement())
    }

    @Test
    fun `Until statements parsed correctly`() {
        val expected = UntilLoopStatement(
            BooleanLiteralExpression(true),
            BlockStatement(listOf(PrintLineStatement(NullLiteralExpression())))
        )
        assertEquals(expected, createParser("Until true\nSay nothing").parseStatement())
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
        assertEquals(expected, createParser("Put Multiply taking 3, 5, and 9 into Large").parseStatement())
    }

    @Test
    fun `function declaration is parsed correctly`() {
        val expected = FunctionDeclaration(
            Identifier("Search"),
            listOf(Identifier("Needle"), Identifier("Haystack")),
            BlockStatement(listOf(ReturnStatement(VariableExpression(Identifier("Needle")))))
        )
        assertEquals(expected, createParser("Search takes Needle and Haystack\nGive back Needle").parseStatement())
    }

    private fun createParser(text: String): StatementParser = StatementParser(Lexer(text).tokens)
}