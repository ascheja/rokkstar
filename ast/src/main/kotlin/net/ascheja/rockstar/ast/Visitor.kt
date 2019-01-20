package net.ascheja.rockstar.ast

import net.ascheja.rockstar.ast.expressions.*
import net.ascheja.rockstar.ast.statements.*

interface Visitor<T> {
    fun visitProgram(program: Program): T = visitBlockStatement(program.root)

    fun visitStatement(statement: Statement): T = when (statement) {
        is AssignmentStatement -> visitAssignmentStatement(statement)
        is BlockStatement -> visitBlockStatement(statement)
        is BreakStatement -> visitBreakStatement(statement)
        is ContinueStatement -> visitContinueStatement(statement)
        is FunctionDeclaration -> visitFunctionDeclaration(statement)
        is IfStatement -> visitIfStatement(statement)
        is IncrementStatement -> visitIncrementStatement(statement)
        is DecrementStatement -> visitDecrementStatement(statement)
        is WhileLoopStatement -> visitWhileLoopStatement(statement)
        is UntilLoopStatement -> visitUntilLoopStatement(statement)
        is PrintLineStatement -> visitPrintLineStatement(statement)
        is ReadLineStatement -> visitReadLineStatement(statement)
        is ReturnStatement -> visitReturnStatement(statement)
        is Expression -> visitExpression(statement)
        else -> throw IllegalArgumentException("Unknown type of statement: ${statement.javaClass}")
    }

    fun visitAssignmentStatement(assignmentStatement: AssignmentStatement): T

    fun visitBlockStatement(blockStatement: BlockStatement): T

    fun visitBreakStatement(breakStatement: BreakStatement): T

    fun visitContinueStatement(continueStatement: ContinueStatement): T

    fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration): T

    fun visitIfStatement(ifStatement: IfStatement): T

    fun visitIncrementStatement(incrementStatement: IncrementStatement): T

    fun visitDecrementStatement(decrementStatement: DecrementStatement): T

    fun visitWhileLoopStatement(whileLoopStatement: WhileLoopStatement): T

    fun visitUntilLoopStatement(untilLoopStatement: UntilLoopStatement): T

    fun visitPrintLineStatement(printLineStatement: PrintLineStatement): T

    fun visitReadLineStatement(readLineStatement: ReadLineStatement): T

    fun visitReturnStatement(returnStatement: ReturnStatement): T

    fun visitExpression(expression: Expression): T = when (expression) {
        is BinaryOperatorExpression -> visitBinaryOperatorExpression(expression)
        is UnaryOperatorExpression -> visitUnaryOperatorExpression(expression)
        is FunctionCallExpression -> visitFunctionCallExpression(expression)
        is NumberLiteralExpression -> visitNumberLiteralExpression(expression)
        is StringLiteralExpression -> visitStringLiteralExpression(expression)
        is BooleanLiteralExpression -> visitBooleanLiteralExpression(expression)
        is NullLiteralExpression -> visitNullLiteralExpression(expression)
        is UndefinedLiteralExpression -> visitUndefinedLiteralExpression(expression)
        is VariableExpression -> visitVariableExpression(expression)
        else -> throw IllegalArgumentException("Unknown type of expression: ${expression.javaClass}")
    }

    fun visitBinaryOperatorExpression(binaryOperatorExpression: BinaryOperatorExpression): T

    fun visitFunctionCallExpression(functionCallExpression: FunctionCallExpression): T

    fun visitUnaryOperatorExpression(unaryOperatorExpression: UnaryOperatorExpression): T

    fun visitNumberLiteralExpression(numberLiteralExpression: NumberLiteralExpression): T

    fun visitStringLiteralExpression(stringLiteralExpression: StringLiteralExpression): T

    fun visitBooleanLiteralExpression(booleanLiteralExpression: BooleanLiteralExpression): T

    fun visitNullLiteralExpression(nullLiteralExpression: NullLiteralExpression): T

    fun visitUndefinedLiteralExpression(undefinedLiteralExpression: UndefinedLiteralExpression): T

    fun visitVariableExpression(variableExpression: VariableExpression): T
}