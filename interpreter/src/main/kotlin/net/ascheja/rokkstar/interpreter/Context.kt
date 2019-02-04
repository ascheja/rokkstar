package net.ascheja.rokkstar.interpreter

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.expressions.UndefinedConstant
import net.ascheja.rokkstar.ast.statements.BlockStatement
import net.ascheja.rokkstar.ast.statements.FunctionDeclaration
import net.ascheja.rokkstar.ast.statements.ReturnStatement
import net.ascheja.rokkstar.typesystem.Value
import net.ascheja.rokkstar.typesystem.values.UndefinedValue

class Context private constructor(
    private val input: () -> String,
    private val output: (Value) -> Unit,
    private val parent: Context?
) {

    private val functionDeclarations: MutableMap<Identifier, FunctionDeclaration> = mutableMapOf()
    private val variables: MutableMap<Identifier, Value> = mutableMapOf()

    companion object {
        private val UNDEFINED_FUNCTION = FunctionDeclaration(
            Identifier(""),
            listOf(),
            BlockStatement(ReturnStatement(UndefinedConstant()))
        )

        fun create(input: () -> String, output: (Value) -> Unit): Context =
            Context(input, output, null)
    }

    operator fun set(name: Identifier, declaration: FunctionDeclaration) {
        functionDeclarations[name] = declaration
        variables.remove(Identifier(name.value))
    }

    fun getFunction(name: Identifier): FunctionDeclaration =
        functionDeclarations[name] ?: parent?.getFunction(name) ?: UNDEFINED_FUNCTION

    operator fun set(name: Identifier, value: Value) {
        variables[name] = value
        functionDeclarations.remove(name)
    }

    fun getValue(name: Identifier): Value =
        variables[name] ?: parent?.getValue(name) ?: UndefinedValue.INSTANCE

    fun println(value: Value) = output(value)

    fun readLine(): String = input()

    fun fork(): Context =
        Context(input, output, this)
}