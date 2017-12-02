package ru.spbau.mit.funInterpreter

import java.io.PrintStream

class Interpreter(private val printStream: PrintStream = DEFAULT_PRINT_STREAM): ASTVisitor<Int?> {
    companion object {
        val DEFAULT_PRINT_STREAM: PrintStream = System.out
    }

    private val context: Context = Context()

    override fun visitFile(file: File): Int? = file.block.accept(this)

    override fun visitBlock(block: Block): Int? {
         context.enterScope()
         for (statement in block.statements) {
             val result = statement.accept(this)
             if (result != null) {
                 context.leaveScope()
                 return result
             }
         }
         context.leaveScope()
         return null
    }

    override fun visitFunction(function: Function): Int? {
        context.addFunction(function)
        return null
    }

    override fun visitVariable(variable: Variable): Int? {
        val value = variable.value?.accept(this)
        context.addVariable(variable.name, value)
        return null
    }

    override fun visitWhileLoop(whileLoop: WhileLoop): Int? {
        while (whileLoop.condition.accept(this) != 0) {
            val result = whileLoop.body.accept(this)
            if (result != null) {
                return result
            }
        }
        return null
    }

    override fun visitIfOperator(ifOperator: IfOperator): Int? {
        if (ifOperator.condition.accept(this) != 0) {
            return ifOperator.body.accept(this)
        }
        return ifOperator.elseBody?.accept(this)
    }

    override fun visitAssignment(assignment: Assignment): Int? {
        val intValue = assignment.value.accept(this)!!
        context.assignVariable(assignment.name, intValue)
        return null
    }

    override fun visitReturnStatement(returnStatement: ReturnStatement): Int =
            returnStatement.value.accept(this)!!

    override fun visitFunctionCall(functionCall: FunctionCall): Int {
        val result: Int?
        val function = context.getFunction(functionCall.name)
        val args = functionCall.arguments.args.map { it.accept(this) }
        val parameterNames = function.parameterNames.names
        if (args.size != parameterNames.size) {
            throw WrongArgumentsNumberException(functionCall.name.name, args.size,
                    parameterNames.size)
        }
        context.enterScope()
        for (i in args.indices) {
            context.addVariable(parameterNames[i], args[i])
        }
        result = function.body.accept(this)
        context.leaveScope()
        return result ?: 0
    }

    override fun visitPrintlnCall(printlnCall: PrintlnCall): Int? {
        val args = printlnCall.arguments.args.map { it.accept(this) }
        printStream.println(args.joinToString(" ") { it.toString() })
        return null
    }

    override fun visitBinaryExpression(binaryExpression: BinaryExpression): Int {
        val lhs = binaryExpression.lhs.accept(this)!!
        val operator = binaryExpression.operator
        val rhs = binaryExpression.rhs.accept(this)!!
        return operator(lhs, rhs)
    }

    override fun visitIdentifier(name: Identifier): Int? = context.getVariableValue(name)

    override fun visitLiteral(literal: Literal): Int = literal.stringValue.toInt()
}
