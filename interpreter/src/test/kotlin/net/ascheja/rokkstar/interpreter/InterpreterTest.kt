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
            AssignmentStatement(variableName, StringConstant("42")).accept(visitor)
            assertEquals("42", getValue(variableName).toString())
        }
    }

    @Test
    fun visitBlockStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(BreakStatement())
            assertTrue(block.accept(visitor) is Action.Break)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(ContinueStatement())
            assertTrue(block.accept(visitor) is Action.Continue)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement(ReturnStatement(NumberConstant(42.0)))
            val action = block.accept(visitor)
            assertTrue(action is Action.Return)
            assertEquals(42.0, (action as Action.Return).value.toNumber(), 0.0)
        }
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val block = BlockStatement()
            assertTrue(block.accept(visitor) is Action.Proceed)
        }
    }

    @Test
    fun visitBreakStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(BreakStatement().accept(visitor) is Action.Break)
        }
    }

    @Test
    fun visitContinueStatement() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(ContinueStatement().accept(visitor) is Action.Continue)
        }
    }

    @Test
    fun visitFunctionDeclaration() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            val functionName = Identifier("my function")
            assertFalse(getFunction(functionName).identifier == functionName)
            FunctionDeclaration(
                functionName,
                emptyList(),
                BlockStatement()
            ).accept(visitor)
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
            IfStatement(truthyValue, thenBlock, null).accept(visitor)
        }

        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            IfStatement(falsyValue, thenBlock, null).accept(visitor)
        }

        withContext("", createOutput { yield("true") }) {
            val visitor = Interpreter(this)
            IfStatement(truthyValue, thenBlock, elseBlock).accept(visitor)
        }

        withContext("", createOutput { yield("false") }) {
            val visitor = Interpreter(this)
            IfStatement(falsyValue, thenBlock, elseBlock).accept(visitor)
        }
    }

    @Test
    fun visitIncrementStatement() {
        withContext("", createOutput {}) {
            val variableName = Identifier("my variable")
            this[variableName] = NumberValue(1.0)
            val visitor = Interpreter(this)
            IncrementStatement(variableName, 1).accept(visitor)
            assertEquals(2.0, getValue(variableName).toNumber(), 0.0)
        }
    }

    @Test
    fun visitDecrementStatement() {
        withContext("", createOutput {}) {
            val variableName = Identifier("my variable")
            this[variableName] = NumberValue(2.0)
            val visitor = Interpreter(this)
            DecrementStatement(variableName, 1).accept(visitor)
            assertEquals(1.0, getValue(variableName).toNumber(), 0.0)
        }
    }

    @Test
    fun visitWhileLoopStatement() {
        val printBla = PrintLineStatement(StringConstant("bla"))
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            WhileLoopStatement(
                UndefinedConstant(),
                BlockStatement(printBla)
            ).accept(visitor)
        }

        withContext("", createOutput { yield("bla"); yield("bla") }) {
            val visitor = Interpreter(this)
            val variableName = Identifier("my var")
            this[variableName] = NumberValue(2.0)
            WhileLoopStatement(
                VariableLookup(variableName),
                BlockStatement(DecrementStatement(variableName, 1), printBla)
            ).accept(visitor)
        }
    }

    @Test
    fun visitUntilLoopStatement() {
        val variable = Identifier("my var")
        withContext("", createOutput { yield("bla"); yield("bla"); }) {
            this[variable] = NumberValue(0.0)
            val visitor = Interpreter(this)
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
            ).accept(visitor)
        }
    }

    @Test
    fun visitPrintLineStatement() {
        withContext("", createOutput { yield("Hello World!") }) {
            val visitor = Interpreter(this)
            PrintLineStatement(
                StringConstant("Hello World!")
            ).accept(visitor)
        }
    }

    @Test
    fun visitReadLineStatement() {
        withContext("Hello World!", createOutput {}) {
            val visitor = Interpreter(this)
            val varName = Identifier("my var")
            assertSame(UndefinedValue, getValue(varName))
            ReadLineStatement(
                varName
            ).accept(visitor)
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
                (FunctionCallExpression(functionName, listOf(StringConstant("some value"))).accept(visitor) as Action.Return)
                    .value.toString()
            )
            assertSame(UndefinedValue, getValue(functionParameterName))
        }
    }

    @Test
    fun visitUnaryOperatorExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertTrue(
                (
                    UnaryOperatorExpression(
                        UnaryOperatorExpression.Operator.NOT,
                        BooleanConstant(false)
                    ).accept(visitor) as Action.Return
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
                (NumberConstant(1.0).accept(visitor) as Action.Return).value.toNumber(),
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
                (StringConstant("bla").accept(visitor) as Action.Return).value.toString()
            )
        }
    }

    @Test
    fun visitBooleanLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertEquals(
                true,
                (BooleanConstant(true).accept(visitor) as Action.Return).value.toBoolean()
            )
        }
    }

    @Test
    fun visitNullLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertSame(NullValue, (NullConstant().accept(visitor) as Action.Return).value)
        }
    }

    @Test
    fun visitUndefinedLiteralExpression() {
        withContext("", createOutput {}) {
            val visitor = Interpreter(this)
            assertSame(UndefinedValue, (UndefinedConstant().accept(visitor) as Action.Return).value)
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