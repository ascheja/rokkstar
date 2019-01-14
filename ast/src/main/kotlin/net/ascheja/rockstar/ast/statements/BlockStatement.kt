package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement

data class BlockStatement(val statements: List<Statement>): Statement, Iterable<Statement> by statements