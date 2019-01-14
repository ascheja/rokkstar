package net.ascheja.rockstar.ast.statements

import net.ascheja.rockstar.ast.FunctionName
import net.ascheja.rockstar.ast.Statement
import net.ascheja.rockstar.ast.VariableName

data class FunctionDeclaration(val functionName: FunctionName, val parameters: List<VariableName>, val body: BlockStatement): Statement