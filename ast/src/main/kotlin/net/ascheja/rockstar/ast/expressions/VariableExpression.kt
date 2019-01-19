package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Identifier

data class VariableExpression(val identifier: Identifier): Expression