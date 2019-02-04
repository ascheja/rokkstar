package net.ascheja.rokkstar.runner

import net.ascheja.rokkstar.ast.Identifier
import net.ascheja.rokkstar.ast.Visitor
import net.ascheja.rokkstar.ast.expressions.*
import net.ascheja.rokkstar.ast.statements.*

class JavascriptTranspiler: Visitor<String> {
    private var depth = -1

    override fun visitVariableExpression(variableExpression: VariableExpression): String {
        return transformIdentifier(variableExpression.identifier)
    }

    override fun visitAssignmentStatement(assignmentStatement: AssignmentStatement): String {
        return transformIdentifier(assignmentStatement.identifier) + " = " + visitExpression(assignmentStatement.expression) + ";"
    }

    override fun visitBlockStatement(blockStatement: BlockStatement): String {
        depth++
        var out = ""
        for (statement in blockStatement) {
            out += indent(visitStatement(statement)) + "\n"
        }
        depth--
        return out
    }

    override fun visitBreakStatement(breakStatement: BreakStatement): String = "break;"

    override fun visitContinueStatement(continueStatement: ContinueStatement): String = "continue;"

    override fun visitFunctionDeclaration(functionDeclaration: FunctionDeclaration): String {
        val parameterList = functionDeclaration.parameters.joinToString(", ") { transformIdentifier(it) }
        val header = "var ${transformIdentifier(functionDeclaration.identifier)} = function ($parameterList) {\n"
        val body = visitBlockStatement(functionDeclaration.body)
        return header + body + indent("}")
    }

    override fun visitIfStatement(ifStatement: IfStatement): String {
        val header = "if (" + visitExpression(ifStatement.condition) + ") {\n"
        val thenPart = visitBlockStatement(ifStatement.thenBlock) + indent("}")
        val elsePart = if (ifStatement.elseBlock != null) {
            "else {\n" + visitBlockStatement(ifStatement.elseBlock!!) + indent("}")
        } else {
            ""
        }
        return header + thenPart + elsePart
    }

    override fun visitIncrementStatement(incrementStatement: IncrementStatement): String {
        return "${transformIdentifier(incrementStatement.identifier)} += ${incrementStatement.amount};"
    }

    override fun visitDecrementStatement(decrementStatement: DecrementStatement): String {
        return "${transformIdentifier(decrementStatement.identifier)} -= ${decrementStatement.amount};"
    }

    override fun visitWhileLoopStatement(whileLoopStatement: WhileLoopStatement): String {
        val header = "while (" + visitExpression(whileLoopStatement.condition) + ") {\n"
        val body = visitBlockStatement(whileLoopStatement.body)
        return header + body + indent("}")
    }

    override fun visitUntilLoopStatement(untilLoopStatement: UntilLoopStatement): String {
        val header = "while (true) {\n"
        depth++
        val conditionCheck = indent(visitIfStatement(
            IfStatement(
                untilLoopStatement.condition,
                BlockStatement(BreakStatement())
            )
        ) + "\n")
        depth--
        val body = visitBlockStatement(untilLoopStatement.body)
        return header + conditionCheck + body + indent("}")
    }

    override fun visitPrintLineStatement(printLineStatement: PrintLineStatement): String {
        return "console.log(" + visitExpression(printLineStatement.expression) + ");"
    }

    override fun visitReadLineStatement(readLineStatement: ReadLineStatement): String {
        return transformIdentifier(readLineStatement.identifier) + " = __getInput()" + ";"
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement): String {
        return "return " + visitExpression(returnStatement.expression) + ";"
    }

    override fun visitBinaryOperatorExpression(binaryOperatorExpression: BinaryOperatorExpression): String =
        when(binaryOperatorExpression.operator) {
            BinaryOperatorExpression.Operator.MULTIPLY -> visitExpression(binaryOperatorExpression.left) + " * " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.DIVIDE -> visitExpression(binaryOperatorExpression.left) + " / " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.ADD -> visitExpression(binaryOperatorExpression.left) + " + " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.SUBTRACT -> visitExpression(binaryOperatorExpression.left) + " - " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.EQUALS -> visitExpression(binaryOperatorExpression.left) + " == " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.NOT_EQUALS -> visitExpression(binaryOperatorExpression.left) + " != " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.LESS -> visitExpression(binaryOperatorExpression.left) + " < " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.LESS_EQUALS -> visitExpression(binaryOperatorExpression.left) + " <= " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.GREATER -> visitExpression(binaryOperatorExpression.left) + " > " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.GREATER_EQUALS -> visitExpression(binaryOperatorExpression.left) + " >= " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.AND -> visitExpression(binaryOperatorExpression.left) + " && " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.OR -> visitExpression(binaryOperatorExpression.left) + " || " + visitExpression(binaryOperatorExpression.right)
            BinaryOperatorExpression.Operator.NOR -> "!(" + visitExpression(binaryOperatorExpression.left) + " || " + visitExpression(binaryOperatorExpression.right) + ")"
        }

    override fun visitFunctionCallExpression(functionCallExpression: FunctionCallExpression): String {
        val arguments = functionCallExpression.arguments.joinToString(", ") { visitExpression(it) }
        return "${transformIdentifier(functionCallExpression.identifier)}($arguments)"
    }

    override fun visitUnaryOperatorExpression(unaryOperatorExpression: UnaryOperatorExpression): String {
        return "!" + visitExpression(unaryOperatorExpression.expression)
    }

    override fun visitNumberConstant(numberConstant: NumberConstant): String {
        return numberConstant.value.toString()
    }

    override fun visitStringConstant(stringConstant: StringConstant): String {
        return "\"${stringConstant.value}\""
    }

    override fun visitBooleanConstant(booleanConstant: BooleanConstant): String {
        return booleanConstant.value.toString()
    }

    override fun visitNullConstant(nullConstant: NullConstant): String {
        return "null"
    }

    override fun visitUndefinedConstant(undefinedConstant: UndefinedConstant): String {
        return "undefined"
    }

    private fun transformIdentifier(identifier: Identifier): String {
        return identifier.value.toLowerCase().replace(' ', '_')
    }

    private fun indent(what: Any): String {
        return " ".repeat(4 * depth) + what
    }
}