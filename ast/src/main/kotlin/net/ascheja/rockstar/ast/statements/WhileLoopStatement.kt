package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Expression
import net.ascheja.rockstar.ast.Statement

data class WhileLoopStatement(val condition: Expression, val body: BlockStatement): Statement