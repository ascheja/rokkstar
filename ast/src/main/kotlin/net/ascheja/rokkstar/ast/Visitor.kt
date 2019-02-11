package net.ascheja.rokkstar.ast

import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*

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
        is NumberConstant -> visitNumberConstant(expression)
        is StringConstant -> visitStringConstant(expression)
        is BooleanConstant -> visitBooleanConstant(expression)
        is NullConstant -> visitNullConstant(expression)
        is UndefinedConstant -> visitUndefinedConstant(expression)
        is VariableLookup -> visitVariableLookup(expression)
        else -> throw IllegalArgumentException("Unknown type of expression: ${expression.javaClass}")
    }

    fun visitBinaryOperatorExpression(binaryOperatorExpression: BinaryOperatorExpression): T

    fun visitFunctionCallExpression(functionCallExpression: FunctionCallExpression): T

    fun visitUnaryOperatorExpression(unaryOperatorExpression: UnaryOperatorExpression): T

    fun visitNumberConstant(numberConstant: NumberConstant): T

    fun visitStringConstant(stringConstant: StringConstant): T

    fun visitBooleanConstant(booleanConstant: BooleanConstant): T

    fun visitNullConstant(nullConstant: NullConstant): T

    fun visitUndefinedConstant(undefinedConstant: UndefinedConstant): T

    fun visitVariableLookup(variableLookup: VariableLookup): T
}