package net.ascheja.rockstar.interpreter

import net.ascheja.rockstar.ast.*
import net.ascheja.rockstar.ast.expressions.*
import net.ascheja.rockstar.ast.statements.*
import net.ascheja.rockstar.typesystem.values.*

class Interpreter(private val context: Context): Visitor<Action> {

    override fun visitProgram(program: Program): Action {
        return visitBlockStatement(program.root)
    }

    override fun visitStatement(statement: Statement): Action = when (statement) {
        is AssignmentStatement -> visitAssignmentStatement(statement)
        is BlockStatement -> visitBlockStatement(statement)
        is BreakStatement -> visitBreakStatement(statement)
        is ContinueStatement -> visitContinueStatement(statement)
        is FunctionDeclaration -> visitFunctionDeclaration(statement)
        is IfStatement -> visitIfStatement(statement)
        is IncrementStatement -> visitIncrementStatement(statement)
        is DecrementStatement -> visitDecrementStatement(statement)
        is LoopStatement -> visitLoopStatement(statement)
        is PrintLineStatement -> visitPrintLineStatement(statement)
        is ReadLineStatement -> visitReadLineStatement(statement)
        is ReturnStatement -> visitReturnStatement(statement)
        is Expression -> {
            visitExpression(statement)
            Action.Proceed()
        }
        else -> throw IllegalArgumentException("Unknown type of statement: ${statement.javaClass}")
    }

    override fun visitAssignmentStatement(assignmentStatement: AssignmentStatement): Action {
        context[assignmentStatement.identifier] = visitExpression(assignmentStatement.expression).value
        return Action.Proceed()
    }

    override fun visitBlockStatement(blockStatement: BlockStatement): Action {
        for (statement in blockStatement) {
            val action = visitStatement(statement)
            if (action !is Action.Proceed) {
                return action
            }
        }
        return Action.Proceed()
    }

    override fun visitBreakStatement(breakStatement: BreakStatement): Action {
        return Action.Break()
    }

    override fun visitContinueStatement(continueStatement: ContinueStatement): Action {
        return Action.Continue()
    }

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration): Action {
        context[functionDeclaration.identifier] = functionDeclaration
        return Action.Proceed()
    }

    override fun visitIfStatement(ifStatement: IfStatement): Action {
        if (visitExpression(ifStatement.condition).value.toBoolean()) {
            return visitBlockStatement(ifStatement.thenBlock)
        }
        val elseBlock = ifStatement.elseBlock
        if (elseBlock != null) {
            return visitBlockStatement(elseBlock)
        }
        return Action.Proceed()
    }

    override fun visitIncrementStatement(incrementStatement: IncrementStatement): Action {
        var toAdd = incrementStatement.amount
        while (toAdd > 0) {
            context[incrementStatement.identifier] = (context.getValue(incrementStatement.identifier)).inc()
            toAdd--
        }
        return Action.Proceed()
    }

    override fun visitDecrementStatement(decrementStatement: DecrementStatement): Action {
        var toSubtract = decrementStatement.amount
        while (toSubtract > 0) {
            context[decrementStatement.identifier] = (context.getValue(decrementStatement.identifier)).dec()
            toSubtract--
        }
        return Action.Proceed()
    }

    override fun visitLoopStatement(loopStatement: LoopStatement): Action {
        loop@ while (visitExpression(loopStatement.condition).value.toBoolean()) {
            when (val action = visitBlockStatement(loopStatement.body)) {
                is Action.Break -> break@loop
                is Action.Continue -> continue@loop
                is Action.Return -> return action
            }
        }
        return Action.Proceed()
    }

    override fun visitPrintLineStatement(printLineStatement: PrintLineStatement): Action {
        context.println(visitExpression(printLineStatement.expression).value)
        return Action.Proceed()
    }

    override fun visitReadLineStatement(readLineStatement: ReadLineStatement): Action {
        context[readLineStatement.identifier] = StringValue(context.readLine())
        return Action.Proceed()
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement): Action {
        return visitExpression(returnStatement.expression)
    }

    override fun visitExpression(expression: Expression): Action.Return =
        super.visitExpression(expression) as Action.Return

    override fun visitBinaryOperatorExpression(binaryOperatorExpression: BinaryOperatorExpression): Action.Return {
        val left = binaryOperatorExpression.left
        val right = binaryOperatorExpression.right
        val result = when (binaryOperatorExpression.operator) {
            BinaryOperatorExpression.Operator.MULTIPLY -> visitExpression(left).value * visitExpression(right).value
            BinaryOperatorExpression.Operator.DIVIDE -> visitExpression(left).value / visitExpression(right).value
            BinaryOperatorExpression.Operator.ADD -> visitExpression(left).value + visitExpression(right).value
            BinaryOperatorExpression.Operator.SUBTRACT -> visitExpression(left).value - visitExpression(right).value
            BinaryOperatorExpression.Operator.EQUALS -> BooleanValue(visitExpression(left).value == visitExpression(right).value)
            BinaryOperatorExpression.Operator.NOT_EQUALS -> BooleanValue(
                visitExpression(left).value != visitExpression(right).value
            )
            BinaryOperatorExpression.Operator.GREATER -> BooleanValue(visitExpression(left).value > visitExpression(right).value)
            BinaryOperatorExpression.Operator.LESS -> BooleanValue(visitExpression(left).value < visitExpression(right).value)
            BinaryOperatorExpression.Operator.GREATER_EQUALS -> BooleanValue(
                visitExpression(left).value >= visitExpression(right).value
            )
            BinaryOperatorExpression.Operator.LESS_EQUALS -> BooleanValue(
                visitExpression(left).value <= visitExpression(right).value
            )
            BinaryOperatorExpression.Operator.AND -> BooleanValue(
                visitExpression(left).value.toBoolean() && visitExpression(right).value.toBoolean()
            )
            BinaryOperatorExpression.Operator.OR -> BooleanValue(
                visitExpression(left).value.toBoolean() || visitExpression(right).value.toBoolean()
            )
            BinaryOperatorExpression.Operator.NOR -> BooleanValue(
                !visitExpression(left).value.toBoolean() && !visitExpression(right).value.toBoolean()
            )
        }
        return Action.Return(result)
    }

    override fun visitFunctionCallExpression(functionCallExpression: FunctionCallExpression): Action.Return {
        val declaration = context.getFunction(functionCallExpression.identifier)
        val functionCallContext = context.fork()
        val arguments = functionCallExpression.arguments.map { visitExpression(it).value }
        for ((index, name) in declaration.parameters.withIndex()) {
            functionCallContext[name] = if (index < arguments.size) arguments[index] else UndefinedValue.INSTANCE
        }
        val child = Interpreter(functionCallContext)
        val result = when(val bodyAction = child.visitBlockStatement(declaration.body)) {
            is Action.Return -> bodyAction.value
            else -> UndefinedValue.INSTANCE
        }

        return Action.Return(result)
    }

    override fun visitUnaryOperatorExpression(unaryOperatorExpression: UnaryOperatorExpression): Action.Return {
        val subject = unaryOperatorExpression.expression
        val result = when (unaryOperatorExpression.operator) {
            UnaryOperatorExpression.Operator.NOT -> !visitExpression(subject).value
        }
        return Action.Return(result)
    }

    override fun visitNumberLiteralExpression(numberLiteralExpression: NumberLiteralExpression): Action.Return {
        return Action.Return(NumberValue(numberLiteralExpression.value))
    }

    override fun visitStringLiteralExpression(stringLiteralExpression: StringLiteralExpression): Action.Return {
        return Action.Return(StringValue(stringLiteralExpression.value))
    }

    override fun visitBooleanLiteralExpression(booleanLiteralExpression: BooleanLiteralExpression): Action.Return {
        return Action.Return(BooleanValue(booleanLiteralExpression.value))
    }

    override fun visitNullLiteralExpression(nullLiteralExpression: NullLiteralExpression): Action.Return {
        return Action.Return(NullValue.INSTANCE)
    }

    override fun visitUndefinedLiteralExpression(undefinedLiteralExpression: UndefinedLiteralExpression): Action.Return {
        return Action.Return(UndefinedValue.INSTANCE)
    }

    override fun visitVariableExpression(variableExpression: VariableExpression): Action.Return {
        return Action.Return(context.getValue(variableExpression.identifier))
    }
}