package ru.spbau.mit

import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class Visitor: FunBaseVisitor<Node>() {
    override fun visitFile(ctx: FunParser.FileContext): File {
        return File(visit(ctx.block()) as Block)
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Block {
        return Block(ctx.statement().map { visit(it) as Statement }.toList())
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): Block {
        return visitBlock(ctx.block())
    }

    override fun visitStatement(ctx: FunParser.StatementContext): Statement {
        return visit(ctx.assignment()
                ?: ctx.expression()
                ?: ctx.function()
                ?: ctx.ifOperator()
                ?: ctx.returnStatement()
                ?: ctx.variable()
                ?: ctx.whileLoop()
        ) as Statement
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): Function {
        val name = Identifier(ctx.IDENTIFIER()!!.text)
        val parameterNames = visit(ctx.parameterNames())
        val body = visit(ctx.blockWithBraces())
        return Function(name, parameterNames as ParameterNames, body as Block)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): Variable {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = if (ctx.expression() != null) visit(ctx.expression()) else null
        return Variable(name, value as Expression)
    }

    override fun visitParameterNames(ctx: FunParser.ParameterNamesContext): ParameterNames {
        return ParameterNames(ctx.IDENTIFIER()?.map { Identifier(it.text) }?.toList() ?: listOf())
    }

    override fun visitWhileLoop(ctx: FunParser.WhileLoopContext): WhileLoop {
        val condition = visit(ctx.expression())
        val body = visit(ctx.blockWithBraces())
        return WhileLoop(condition as Expression, body as Block)
    }

    override fun visitIfOperator(ctx: FunParser.IfOperatorContext): IfOperator {
        val condition = visit(ctx.expression())
        val blocks = ctx.blockWithBraces().map { visit(it) }
        return IfOperator(condition as Expression, blocks[0] as Block,
                blocks.getOrNull(1) as Block?)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): Assignment {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = visit(ctx.expression())
        return Assignment(name, value as Expression)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): ReturnStatement {
        return ReturnStatement(visit(ctx.expression()) as Expression)
    }

    override fun visitExpression(ctx: FunParser.ExpressionContext): Expression {
        return visit(ctx.atomicExpression() ?: ctx.binaryExpression()) as Expression
    }

    override fun visitAtomicExpression(ctx: FunParser.AtomicExpressionContext): Expression {
        val name = ctx.IDENTIFIER()
        if (name != null) {
            return Identifier(name.text)
        }
        val literalStringValue = ctx.LITERAL()
        if (literalStringValue != null) {
            return Literal(literalStringValue.text)
        }
        return visit(ctx.printlnCall() ?: ctx.functionCall() ?: ctx.expression()) as Expression
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext): FunctionCall {
        val name = Identifier(ctx.IDENTIFIER().text)
        val arguments = visit(ctx.arguments())
        return FunctionCall(name, arguments as Arguments)
    }

    override fun visitPrintlnCall(ctx: FunParser.PrintlnCallContext): PrintlnCall {
        return PrintlnCall(visit(ctx.arguments()) as Arguments)
    }

    override fun visitArguments(ctx: FunParser.ArgumentsContext): Arguments {
        val args = ctx.expression()?.map { visit(it) as Expression }?.toList() ?: listOf()
        return Arguments(args)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): BinaryExpression {
        val lhs = visit(ctx.atomicExpression())
        val operator = Operator.operatorByStringValue(ctx.operator.text)!!
        val rhs = visit(ctx.expression())
        return BinaryExpression(lhs as Expression, operator, rhs as Expression)
    }

}
