package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.VariableName

data class IncrementStatement(val variableName: VariableName, val amount: Int): Statement