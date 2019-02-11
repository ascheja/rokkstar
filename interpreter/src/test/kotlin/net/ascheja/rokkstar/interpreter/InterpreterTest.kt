package net.ascheja.rokkstar.interpreter

import net.ascheja.rokkstar.ast.*
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*
import net.ascheja.rokkstar.typesystem.Value
import net.ascheja.rokkstar.typesystem.values.NullValue
import net.ascheja.rokkstar.typesystem.values.NumberValue
import net.ascheja.rokkstar.typesystem.values.UndefinedValue
import org.junit.Assert.*
import org.junit.Test
import java.io.*

class InterpreterTest {

    @Test
    fun visitAssignmentStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val variableName = Identifier("some var")
            assertSame(UndefinedValue, getValue(variableName))
            visitor.visitStatement(AssignmentStatement(variableName, StringConstant("42")))
            assertEquals("42", getValue(variableName).toString())
        }
    }

    @Test
    fun visitBlockStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(BreakStatement())
            assertTrue(visitor.visitStatement(block) is Action.Break)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(ContinueStatement())
            assertTrue(visitor.visitStatement(block) is Action.Continue)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(ReturnStatement(NumberConstant(42.0)))
            val action = visitor.visitStatement(block)
            assertTrue(action is Action.Return)
            assertEquals(42.0, (action as Action.Return).value.toNumber(), 0.0)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement()
            assertTrue(visitor.visitStatement(block) is Action.Proceed)
        }
    }

    @Test
    fun visitBreakStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(visitor.visitStatement(BreakStatement()) is Action.Break)
        }
    }

    @Test
    fun visitContinueStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(visitor.visitStatement(ContinueStatement()) is Action.Continue)
        }
    }

    @Test
    fun visitFunctionDeclaration() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val functionName = Identifier("my function")
            assertFalse(getFunction(functionName).identifier == functionName)
            visitor.visitStatement(
                FunctionDeclaration(
                    functionName,
                    emptyList(),
                    BlockStatement()
                )
            )
            assertTrue(getFunction(functionName).identifier == functionName)
        }
    }

    @Test
    fun visitIfStatement() {
        val truthyValue = StringConstant("123")
        val falsyValue = UndefinedConstant()

        val thenBlock = BlockStatement(PrintLineStatement(StringConstant("true")))
        val elseBlock = BlockStatement(PrintLineStatement(StringConstant("false")))

        withContext("", createOutput { yield("true") }) {
            val visitor = Interpreter(this)
            visitor.visitStatement(IfStatement(truthyValue, thenBlock, null))
        }

        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            visitor.visitStatement(IfStatement(falsyValue, thenBlock, null))
        }

        withContext("", createOutput { yield("true") }) {
            val visitor = Interpreter(this)
            visitor.visitStatement(IfStatement(truthyValue, thenBlock, elseBlock))
        }

        withContext("", createOutput { yield("false") }) {
            val visitor = Interpreter(this)
            visitor.visitStatement(IfStatement(falsyValue, thenBlock, elseBlock))
        }
    }

    @Test
    fun visitIncrementStatement() {
        withContext("", createOutput {}) {
            val variableName = Identifier("my variable")
            this[variableName] = NumberValue(1.0)
            val visitor = Interpreter(this)
            visitor.visitStatement(IncrementStatement(variableName, 1))
            assertEquals(2.0, getValue(variableName).toNumber(), 0.0)
        }
    }

    @Test
    fun visitDecrementStatement() {
        withContext("", createOutput {}) {
            val variableName = Identifier("my variable")
            this[variableName] = NumberValue(2.0)
            val visitor = Interpreter(this)
            visitor.visitStatement(DecrementStatement(variableName, 1))
            assertEquals(1.0, getValue(variableName).toNumber(), 0.0)
        }
    }

    @Test
    fun visitWhileLoopStatement() {
        val printBla = PrintLineStatement(StringConstant("bla"))
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            visitor.visitStatement(
                WhileLoopStatement(
                    UndefinedConstant(),
                    BlockStatement(printBla)
                )
            )
        }

        withContext("", createOutput { yield("bla"); yield("bla") }) {
            val visitor = Interpreter(this)
            val variableName = Identifier("my var")
            this[variableName] = NumberValue(2.0)
            visitor.visitStatement(
                WhileLoopStatement(
                    VariableLookup(variableName),
                    BlockStatement(DecrementStatement(variableName, 1), printBla)
                )
            )
        }
    }

    @Test
    fun visitUntilLoopStatement() {
        val variable = Identifier("my var")
        withContext("", createOutput { yield("bla"); yield("bla"); }) {
            this[variable] = NumberValue(0.0)
            val visitor = Interpreter(this)
            visitor.visitStatement(
                UntilLoopStatement(
                    BinaryOperatorExpression(
                        BinaryOperatorExpression.Operator.EQUALS,
                        VariableLookup(variable),
                        NumberConstant(2.0)
                    ),
                    BlockStatement(
                        IncrementStatement(variable, 1),
                        PrintLineStatement(StringConstant("bla"))
                    )
                )
            )
        }
    }

    @Test
    fun visitPrintLineStatement() {
        withContext("", createOutput { yield("Hello World!") }) {
            val visitor = Interpreter(this)
            visitor.visitStatement(
                PrintLineStatement(
                    StringConstant("Hello World!")
                )
            )
        }
    }

    @Test
    fun visitReadLineStatement() {
        withContext("Hello World!", createOutput {}) {
            val visitor = Interpreter(this)
            val varName = Identifier("my var")
            assertSame(UndefinedValue, getValue(varName))
            visitor.visitStatement(
                ReadLineStatement(
                    varName
                )
            )
            assertEquals("Hello World!", getValue(varName).toString())
        }
    }

    @Test
    fun visitReturnStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val result = visitor.visitReturnStatement(ReturnStatement(StringConstant("test")))
            assertTrue("result of return should be of type Action.Return", result is Action.Return)
            assertEquals("test", (result as Action.Return).value.toString())
        }
    }

    @Test
    fun visitBinaryOperatorExpression() {
        withContext("", createOutput {}) {

        }
    }

    @Test
    fun visitFunctionCallExpression() {
        withContext("", createOutput {}) {
            val functionName = Identifier("my function")
            val functionParameterName = Identifier("my var")
            this[functionName] = FunctionDeclaration(
                functionName,
                listOf(functionParameterName),
                BlockStatement(ReturnStatement(VariableLookup(functionParameterName)))
            )
            val visitor = Interpreter(this)
            assertEquals(
                "some value",
                visitor.visitExpression(
                    FunctionCallExpression(
                        functionName,
                        listOf(StringConstant("some value"))
                    )
                ).value.toString()
            )
            assertSame(UndefinedValue, getValue(functionParameterName))
        }
    }

    @Test
    fun visitUnaryOperatorExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(
                visitor.visitExpression(
                    UnaryOperatorExpression(
                        UnaryOperatorExpression.Operator.NOT,
                        BooleanConstant(false)
                    )
                ).value.toBoolean()
            )
        }
    }

    @Test
    fun visitNumberLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertEquals(
                1.0,
                visitor.visitExpression(NumberConstant(1.0)).value.toNumber(),
                0.0
            )
        }
    }

    @Test
    fun visitStringLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertEquals(
                "bla",
                visitor.visitExpression(StringConstant("bla")).value.toString()
            )
        }
    }

    @Test
    fun visitBooleanLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertEquals(
                true,
                visitor.visitExpression(BooleanConstant(true)).value.toBoolean()
            )
        }
    }

    @Test
    fun visitNullLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertSame(NullValue, visitor.visitExpression(NullConstant()).value)
        }
    }

    @Test
    fun visitUndefinedLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertSame(UndefinedValue, visitor.visitExpression(UndefinedConstant()).value)
        }
    }

    private fun withContext(
        content: String,
        outputFunctions: Pair<(Value) -> Unit, () -> Unit>,
        block: Context.() -> Unit
    ) {
        val reader = BufferedReader(StringReader(content))
        val (input, inputDone) = { reader.readLine() ?: "" } to { reader.close() }
        val (output, outputDone) = outputFunctions
        try {
            val context = Context.create(input, output)
            context.block()
        } finally {
            inputDone()
            outputDone()
        }
    }

    private fun createOutput(block: suspend SequenceScope<String>.() -> Unit): Pair<(Value) -> Unit, () -> Unit> {
        val seq = sequence(block).iterator()
        var line = 1
        return Pair(
            { value: Value ->
                assertTrue("more output than expected: $value", seq.hasNext())
                val expected = seq.next()
                val actual = value.toString()
                assertEquals("$line: $expected != $actual", expected, actual)
                line += 1
            },
            { assertFalse("less output then expected", seq.hasNext()) }
        )
    }
}