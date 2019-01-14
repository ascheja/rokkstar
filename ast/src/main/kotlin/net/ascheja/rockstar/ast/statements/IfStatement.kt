package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Statement

data class IfStatement(val condition: Expression, val thenBlock: BlockStatement, val elseBlock: BlockStatement?): Statement
