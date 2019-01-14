package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.FunctionName

data class FunctionCallExpression(val name: FunctionName, val arguments: List<Expression>): Expression