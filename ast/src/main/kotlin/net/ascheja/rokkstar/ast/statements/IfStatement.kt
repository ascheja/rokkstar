package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Expression
import net.ascheja.rokkstar.ast.Statement

data class IfStatement(val condition: Expression, val thenBlock: BlockStatement, val elseBlock: BlockStatement? = null): Statement
