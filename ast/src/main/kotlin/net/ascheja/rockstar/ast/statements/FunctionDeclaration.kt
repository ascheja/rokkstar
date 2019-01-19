package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.Identifier

data class FunctionDeclaration(val identifier: Identifier, val parameters: List<Identifier>, val body: BlockStatement): Statement