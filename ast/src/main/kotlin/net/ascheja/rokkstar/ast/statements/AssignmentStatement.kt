package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier

data class AssignmentStatement(val identifier: Identifier, val expression: Expression): Statement