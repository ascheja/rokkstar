package net.ascheja.rokkstar.ast.statements

import net.ascheja.rokkstar.ast.Statement
import net.ascheja.rokkstar.ast.Identifier

data class FunctionDeclaration(val identifier: Identifier, val parameters: List<Identifier>, val body: BlockStatement): Statement