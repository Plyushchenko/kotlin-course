package ru.spbau.mit.funInterpreter

import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class Visitor: FunBaseVisitor<Node>() {
    override fun visitFile(ctx: FunParser.FileContext) : File {
        val block = visit(ctx.block()) as Block
        return File(block)
    }

    override fun visitBlock(ctx: FunParser.BlockContext) : Block {
        val statements = ctx.statement().map {
            visit(it) as Statement
        }.toList()
        return Block(statements)
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext) : Block =
            visitBlock(ctx.block())

    override fun visitStatement(ctx: FunParser.StatementContext) : Statement {
        return visit(ctx.assignment()
                ?: ctx.expression()
                ?: ctx.function()
                ?: ctx.ifOperator()
                ?: ctx.returnStatement()
                ?: ctx.variable()
                ?: ctx.whileLoop()
                ?: ctx.printlnCall()
        ) as Statement
    }

    override fun visitFunction(ctx: FunParser.FunctionContext) : Function {
        val name = Identifier(ctx.IDENTIFIER().text)
        val parameterNames = visit(ctx.parameterNames()) as ParameterNames
        val body = visit(ctx.blockWithBraces()) as Block
        return Function(name, parameterNames, body)
    }

    override fun visitVariable(ctx: FunParser.VariableContext) : Variable {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = ctx.expression() ?: return Variable(name, null)
        return Variable(name, visit(value) as Expression)
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext) : ParameterNames {
        val parameterNamesAsContext = ctx.IDENTIFIER() ?: return ParameterNames(emptyList())
        val parameterNames = parameterNamesAsContext.map {
            Identifier(it.text)
        }.toList()
        return ParameterNames(parameterNames)
    }

    override fun visitWhileLoop(ctx: FunParser.WhileLoopContext) : WhileLoop {
        val condition = visit(ctx.expression()) as Expression
        val body = visit(ctx.blockWithBraces()) as Block
        return WhileLoop(condition, body)
    }

    override fun visitIfOperator(ctx: FunParser.IfOperatorContext) : IfOperator {
        val condition = visit(ctx.expression()) as Expression
        val blocks = ctx.blockWithBraces().map { visit(it) as Block }
        val ifBlock = blocks[0]
        val elseBlock = blocks.getOrNull(1)
        return IfOperator(condition, ifBlock, elseBlock)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext) : Assignment {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = visit(ctx.expression()) as Expression
        return Assignment(name, value)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext) : ReturnStatement {
        val value = visit(ctx.expression()) as Expression
        return ReturnStatement(value)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext) : Expression {
        val expressionToVisit = ctx.binaryExpression() ?: ctx.atomicExpression()
        return visit(expressionToVisit) as Expression
    }

    override fun visitAtomicExpression(ctx: FunParser.AtomicExpressionContext) : Expression {
        val name = ctx.IDENTIFIER()
        if (name != null) {
            return Identifier(name.text)
        }
        val literalStringValue = ctx.LITERAL()
        if (literalStringValue != null) {
            return Literal(literalStringValue.text)
        }
        val expressionToVisit = ctx.functionCall() ?: ctx.expression()
        return visit(expressionToVisit) as Expression
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext) : FunctionCall {
        val name = Identifier(ctx.IDENTIFIER().text)
        val arguments = visit(ctx.arguments()) as Arguments
        return FunctionCall(name, arguments)
    }

    override fun visitPrintlnCall(ctx: FunParser.PrintlnCallContext) : PrintlnCall {
        val arguments = visit(ctx.arguments()) as Arguments
        return PrintlnCall(arguments)
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext) : Arguments {
        val argsAsContext = ctx.expression() ?: return Arguments(emptyList())
        val args = argsAsContext.map {
            visit(it) as Expression
        }.toList()
        return Arguments(args)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext) : BinaryExpression {
        val lhs = visit(ctx.atomicExpression()) as Expression
        val operator = Operator.operatorByStringValue(ctx.operator.text)!!
        val rhs = visit(ctx.expression()) as Expression
        return BinaryExpression(lhs, operator, rhs)
    }
}
