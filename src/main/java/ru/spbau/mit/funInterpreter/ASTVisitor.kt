package ru.spbau.mit.funInterpreter

interface ASTVisitor<out T> {
    fun visitFile(file: File): T

    fun visitBlock(block: Block): T

    fun visitFunction(function: Function): T

    fun visitVariable(variable: Variable): T

    fun visitParameterNames(parameterNames: ParameterNames): T

    fun visitWhileLoop(whileLoop: WhileLoop): T

    fun visitIfOperator(ifOperator: IfOperator): T

    fun visitAssignment(assignment: Assignment): T

    fun visitReturnStatement(returnStatement: ReturnStatement): T

    fun visitFunctionCall(functionCall: FunctionCall): T

    fun visitPrintlnCall(printlnCall: PrintlnCall): T

    fun visitBinaryExpression(binaryExpression: BinaryExpression): T

    fun visitArguments(arguments: Arguments): T

    fun visitIdentifier(name: Identifier): T

    fun visitLiteral(literal: Literal): T
}