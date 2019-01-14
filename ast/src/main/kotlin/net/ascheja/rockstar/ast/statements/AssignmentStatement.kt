package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.VariableName

data class AssignmentStatement(val variableName: VariableName, val expression: Expression): Statement