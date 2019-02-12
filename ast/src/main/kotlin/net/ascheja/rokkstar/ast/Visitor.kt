package net.ascheja.rokkstar.ast

import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*

interface Visitor<T> {
    fun visitProgram(program: Program): T = visitBlockStatement(program.root)

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