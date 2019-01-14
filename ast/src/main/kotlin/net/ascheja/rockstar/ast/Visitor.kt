package net.ascheja.rockstar.ast

import net.ascheja.rockstar.ast.expressions.*
import net.ascheja.rockstar.ast.statements.*

interface Visitor<T> {
    fun visit(program: Program): T

    fun visitStatement(statement: Statement): T

    fun visitAssignmentStatement(assignmentStatement: AssignmentStatement): T

    fun visitBlockStatement(blockStatement: BlockStatement): T

    fun visitBreakStatement(breakStatement: BreakStatement): T

    fun visitContinueStatement(continueStatement: ContinueStatement): T

    fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration): T

    fun visitIfStatement(ifStatement: IfStatement): T

    fun visitIncrementStatement(incrementStatement: IncrementStatement): T

    fun visitDecrementStatement(decrementStatement: DecrementStatement): T

    fun visitLoopStatement(loopStatement: LoopStatement): T

    fun visitPrintLineStatement(printLineStatement: PrintLineStatement): T

    fun visitReadLineStatement(readLineStatement: ReadLineStatement): T

    fun visitReturnStatement(returnStatement: ReturnStatement): T

    fun visitExpression(expression: Expression): T

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