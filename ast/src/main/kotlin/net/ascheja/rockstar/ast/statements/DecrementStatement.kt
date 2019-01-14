package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.VariableName

data class DecrementStatement(val name: VariableName, val amount: Int): Statement