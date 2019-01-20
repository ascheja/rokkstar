package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier

data class IncrementStatement(val identifier: Identifier, val amount: Int): Statement