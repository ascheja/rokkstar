package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.Identifier

data class ReadLineStatement(val identifier: Identifier): Statement