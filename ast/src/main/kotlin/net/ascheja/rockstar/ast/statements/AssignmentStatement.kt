package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.Identifier

data class AssignmentStatement(val identifier: Identifier, val expression: Expression): Statement