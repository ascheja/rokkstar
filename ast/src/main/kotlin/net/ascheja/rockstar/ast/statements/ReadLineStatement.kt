package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.VariableName

data class ReadLineStatement(val variableName: VariableName): Statement