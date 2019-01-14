package net.ascheja.rockstar.ast.expressions

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.VariableName

data class VariableExpression(val variableName: VariableName): Expression