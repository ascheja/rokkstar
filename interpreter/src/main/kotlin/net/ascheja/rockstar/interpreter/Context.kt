package net.ascheja.rockstar.interpreter

import net.ascheja.rockstar.ast.FunctionName
import net.ascheja.rockstar.ast.VariableName
import net.ascheja.rockstar.ast.expressions.UndefinedLiteralExpression
import net.ascheja.rockstar.ast.statements.BlockStatement
import net.ascheja.rockstar.ast.statements.FunctionDeclaration
import net.ascheja.rockstar.ast.statements.ReturnStatement
import net.ascheja.rockstar.typesystem.Value
import net.ascheja.rockstar.typesystem.values.UndefinedValue

class Context private constructor(
    private val input: () -> String,
    private val output: (Value) -> Unit,
    private val parent: Context?
) {

    private val functionDeclarations: MutableMap<FunctionName, FunctionDeclaration> = mutableMapOf()
    private val variables: MutableMap<VariableName, Value> = mutableMapOf()

    companion object {
        private val UNDEFINED_FUNCTION = FunctionDeclaration(
            FunctionName(""),
            listOf(),
            BlockStatement(listOf(ReturnStatement(UndefinedLiteralExpression())))
        )

        fun create(input: () -> String, output: (Value) -> Unit): Context =
            Context(input, output, null)
    }

    operator fun set(name: FunctionName, declaration: FunctionDeclaration) {
        functionDeclarations[name] = declaration
        variables.remove(VariableName(name.value))
    }

    operator fun get(name: FunctionName): FunctionDeclaration =
        functionDeclarations[name] ?: parent?.get(name) ?: UNDEFINED_FUNCTION

    operator fun set(name: VariableName, value: Value) {
        variables[name] = value
        functionDeclarations.remove(FunctionName(name.value))
    }

    operator fun get(name: VariableName): Value =
        variables[name] ?: parent?.get(name) ?: UndefinedValue.INSTANCE

    fun println(value: Value) = output(value)

    fun readLine(): String = input()

    fun fork(): Context =
        Context(input, output, this)
}