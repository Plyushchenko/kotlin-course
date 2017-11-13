package ru.spbau.mit.funInterpreter

import ru.spbau.mit.funInterpreter.Operator.*
import java.io.PrintStream

class Interpreter(private val context: Context = Context(),
                  private val printStream: PrintStream = DEFAULT_PRINT_STREAM) {
    companion object {
        val DEFAULT_PRINT_STREAM: PrintStream = System.out
    }

    fun <T: Node> visit(node: T): Int? {
        return node.accept(this)
    }

    fun visitFile(file: File): Int? {
        return visitBlock(file.block)
    }

    fun visitBlock(block: Block): Int? {
         var result: Int? = null
         context.enterScope()
         for (statement in block.statements) {
             result = visit(statement)
             if (result != null) {
                 break
             }
         }
         context.leaveScope()
         return result
    }

    fun visitFunction(function: Function): Int? {
        context.addFunction(function)
        return null
    }

    fun visitVariable(variable: Variable): Int? {
        var value: Int? = null
        if (variable.value != null) {
            value = visit(variable.value)
        }
        context.addVariable(variable.name, value)
        return null
    }

    fun visitWhileLoop(whileLoop: WhileLoop): Int? {
        var result: Int? = null
        while (visit(whileLoop.condition)!! != 0) {
            result = visit(whileLoop.body)
            if (result != null) {
                break
            }
        }
        return result
    }

    fun visitIfOperator(ifOperator: IfOperator): Int? {
        var result: Int? = null
        if (visit(ifOperator.condition)!! != 0) {
            result = visit(ifOperator.body)
        } else {
            if (ifOperator.elseBody != null) {
                result = visit(ifOperator.elseBody)
            }
        }
        return result
    }

    fun visitAssignment(assignment: Assignment): Int? {
        val intValue = visit(assignment.value)!!
        context.assignVariable(assignment.name, intValue)
        return null
    }

    fun visitReturnStatement(returnStatement: ReturnStatement): Int {
        return visit(returnStatement.value)!!
    }

    fun visitFunctionCall(functionCall: FunctionCall): Int {
        val result: Int?
        val function = context.getFunction(functionCall.name)
        val args = functionCall.arguments.args.map { visit(it) }
        val parameterNames = function.parameterNames.names
        if (args.size != parameterNames.size) {
            throw WrongArgumentsNumberException(functionCall.name.name, args.size,
                    parameterNames.size)
        }
        context.enterScope()
        for (i in args.indices) {
            context.addVariable(parameterNames[i], args[i])
        }
        result = visit(function.body)
        context.leaveScope()
        return result ?: 0
    }

    fun visitPrintlnCall(printlnCall: PrintlnCall): Int? {
        val args = printlnCall.arguments.args.map { visit(it) }
        printStream.println(args.joinToString(" ") {it.toString()})
        return null
    }

    fun visitBinaryExpression(binaryExpression: BinaryExpression): Int {
        val lhs = visit(binaryExpression.lhs)!!
        val operator = binaryExpression.operator
        val rhs = visit(binaryExpression.rhs)!!
        return when(operator) {
            AND -> (lhs.bool && rhs.bool).int
            DIV -> lhs / rhs
            EQ -> (lhs == rhs).int
            GEQ -> (lhs >= rhs).int
            GT -> (lhs > rhs).int
            LEQ -> (lhs <= rhs).int
            LT -> (lhs < rhs).int
            MINUS -> lhs - rhs
            MOD -> lhs % rhs
            MUL -> lhs * rhs
            NEQ -> (lhs != rhs).int
            OR -> (lhs.bool || rhs.bool).int
            PLUS -> lhs + rhs
        }
    }

    fun visitIdentifier(name: Identifier): Int? {
        return context.getVariableValue(name)
    }

    fun visitLiteral(literal: Literal): Int {
        return literal.stringValue.toInt()
    }
}

val Int.bool get() = this != 0
val Boolean.int get() = if (this) 1 else 0
