package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement

data class BlockStatement(val statements: List<Statement>): Statement, Iterable<Statement> by statements