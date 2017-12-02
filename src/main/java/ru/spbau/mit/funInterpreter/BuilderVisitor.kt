package ru.spbau.mit.funInterpreter

import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser

class BuilderVisitor : FunBaseVisitor<Node>() {
    override fun visitFile(ctx: FunParser.FileContext): File {
        val block = ctx.block().accept(this) as Block
        return File(block)
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Block {
        val statements = ctx.statement().map { it.accept(this) as Statement }
        return Block(statements)
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): Block =
            ctx.block().accept(this) as Block

    override fun visitFunction(ctx: FunParser.FunctionContext): Function {
        val name = Identifier(ctx.IDENTIFIER().text)
        val parameterNames = ParameterNames(ctx.parameterNames().IDENTIFIER().map {
            Identifier(it.text)
        })
        val body = ctx.blockWithBraces().accept(this) as Block
        return Function(name, parameterNames, body)
    }

    override fun visitVariable(ctx: FunParser.VariableContext): Variable {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = ctx.expression() ?: return Variable(name, null)
        return Variable(name, value.accept(this) as Expression)
    }

    override fun visitWhileLoop(ctx: FunParser.WhileLoopContext): WhileLoop {
        val condition = ctx.expression().accept(this) as Expression
        val body = ctx.blockWithBraces().accept(this) as Block
        return WhileLoop(condition, body)
    }

    override fun visitIfOperator(ctx: FunParser.IfOperatorContext): IfOperator {
        val condition = ctx.expression().accept(this) as Expression
        val blocks = ctx.blockWithBraces().map { it.accept(this) as Block }
        val ifBlock = blocks[0]
        val elseBlock = blocks.getOrNull(1)
        return IfOperator(condition, ifBlock, elseBlock)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): Assignment {
        val name = Identifier(ctx.IDENTIFIER().text)
        val value = ctx.expression().accept(this) as Expression
        return Assignment(name, value)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): ReturnStatement {
        val value = ctx.expression().accept(this) as Expression
        return ReturnStatement(value)
    }

    override fun visitIdentifierExpression(ctx: FunParser.IdentifierExpressionContext): Identifier =
            Identifier(ctx.text)

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext): Literal =
            Literal(ctx.text)

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): BinaryExpression {
        val lhs = ctx.lhs.accept(this) as Expression
        val operatorStringValue = ctx.operator.text
        val operator = BinaryExpression.Companion.Operator.operatorByStringValue(
                operatorStringValue
        ) ?: throw NotFoundException(operatorStringValue, NotFoundException.Companion.Type.OPERATOR)
        val rhs = ctx.rhs.accept(this) as Expression
        return BinaryExpression(lhs, operator, rhs)
    }

    override fun visitExpressionInBrackets(ctx: FunParser.ExpressionInBracketsContext): Expression =
            ctx.expression().accept(this) as Expression

    override fun visitPrintlnCall(ctx: FunParser.PrintlnCallContext): PrintlnCall {
        val arguments = Arguments(ctx.arguments().expression().map {
            it.accept(this) as Expression
        })
        return PrintlnCall(arguments)
    }

    override fun visitFunctionCall(ctx: FunParser.FunctionCallContext): FunctionCall {
        val name = Identifier(ctx.IDENTIFIER().text)
        val arguments = Arguments(ctx.arguments().expression().map {
            it.accept(this) as Expression
        })
        return FunctionCall(name, arguments)
    }
}
