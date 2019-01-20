package net.ascheja.rockstar.parser

import net.ascheja.rockstar.ast.Identifier
import net.ascheja.rockstar.ast.expressions.*
import net.ascheja.rockstar.ast.statements.*
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
    fun `Poetic string literal parsed correctly`() {
        assertEquals(
            AssignmentStatement(Identifier("Your heart"), StringLiteralExpression(";!bla bla???<>")),
            createParser("Your heart says ;!bla bla???<>").parseStatement()
        )
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

    private fun createParser(text: String): StatementParser = StatementParser(Lexer(text).tokens
    )
}