package ru.spbau.mit.funInterpreter

import ru.spbau.mit.funInterpreter.Operator.*
import java.io.PrintStream

class Interpreter(private val context: Context = Context(),
                  private val printStream: PrintStream = DEFAULT_PRINT_STREAM): ASTVisitor<Int?> {
    companion object {
        val DEFAULT_PRINT_STREAM: PrintStream = System.out
    }

    override fun visitFile(file: File): Int? = file.block.accept(this)

    override fun visitBlock(block: Block): Int? {
         var result: Int? = null
         context.enterScope()
         for (statement in block.statements) {
             result = statement.accept(this)
             if (result != null) {
                 break
             }
         }
         context.leaveScope()
         return result
    }

    override fun visitFunction(function: Function): Int? {
        context.addFunction(function)
        return null
    }

    override fun visitParameterNames(parameterNames: ParameterNames): Int? = null

    override fun visitVariable(variable: Variable): Int? {
        var value: Int? = null
        if (variable.value != null) {
            value = variable.value.accept(this)
        }
        context.addVariable(variable.name, value)
        return null
    }

    override fun visitWhileLoop(whileLoop: WhileLoop): Int? {
        var result: Int? = null
        while (whileLoop.condition.accept(this) != 0) {
            result = whileLoop.body.accept(this)
            if (result != null) {
                break
            }
        }
        return result
    }

    override fun visitIfOperator(ifOperator: IfOperator): Int? {
        var result: Int? = null
        if (ifOperator.condition.accept(this) != 0) {
            result = ifOperator.body.accept(this)
        } else if (ifOperator.elseBody != null) {
            result = ifOperator.elseBody.accept(this)
        }
        return result
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

    override fun visitArguments(arguments: Arguments): Int? = null

    override fun visitIdentifier(name: Identifier): Int? = context.getVariableValue(name)

    override fun visitLiteral(literal: Literal): Int = literal.stringValue.toInt()

    private val Int.bool get() = this != 0

    private val Boolean.int get() = if (this) 1 else 0
}
