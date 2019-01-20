package net.ascheja.rokkstar.ast.expressions

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Identifier

data class FunctionCallExpression(val identifier: Identifier, val arguments: List<Expression>): Expression