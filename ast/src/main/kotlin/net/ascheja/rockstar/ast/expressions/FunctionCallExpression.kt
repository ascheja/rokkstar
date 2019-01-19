package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Identifier

data class FunctionCallExpression(val identifier: Identifier, val arguments: List<Expression>): Expression