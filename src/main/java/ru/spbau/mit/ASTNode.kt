package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.nio.file.Path

interface Node {
    fun accept(visitor: Interpreter): Int?
}
interface Statement: Node
interface Expression: Statement

data class File(val block: Block): Node {
    companion object {
        fun buildAST(path: Path): File {
            val lexer = FunLexer(CharStreams.fromPath(path))
            val parser = FunParser(BufferedTokenStream(lexer))
            return Visitor().visitFile(parser.file())
        }
    }
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitFile(this)
    }
}

data class Block(val statements: List<Statement>): Node {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitBlock(this)
    }
}

data class Function(val name: Identifier, val parameterNames: ParameterNames, val body: Block):
        Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitFunction(this)
    }
}

data class Variable(val name: Identifier, val value: Expression?): Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitVariable(this)
    }
}

data class ParameterNames(val names: List<Identifier>): Statement {
    override fun accept(visitor: Interpreter): Int? = null
}

data class WhileLoop(val condition: Expression, val body: Block): Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitWhileLoop(this)
    }
}

data class IfOperator(val condition: Expression, val body: Block, val elseBody: Block?): Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitIfOperator(this)
    }
}

data class Assignment(val name: Identifier, val value: Expression): Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitAssignment(this)
    }
}
data class ReturnStatement(val value: Expression): Statement {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitReturnStatement(this)
    }
}

data class BinaryExpression(val lhs: Expression, val operator: Operator, val rhs: Expression):
        Expression {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitBinaryExpression(this)
    }
}

data class FunctionCall(val name: Identifier,  val arguments: Arguments): Expression {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitFunctionCall(this)
    }
}

data class PrintlnCall(val arguments: Arguments) : Expression {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitPrintlnCall(this)
    }
}

data class Arguments(val args: List<Expression>): Node {
    override fun accept(visitor: Interpreter): Int? = null
}

data class Identifier(val name: String): Expression {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitIdentifier(this)
    }
}

data class Literal(val stringValue: String): Expression {
    override fun accept(visitor: Interpreter): Int? {
        return visitor.visitLiteral(this)
    }
}

enum class Operator(val stringValue: String) {
    AND("&&"),
    DIV("/"),
    EQ("=="),
    GEQ(">="),
    GT(">"),
    LEQ("<="),
    LT("<"),
    MINUS("-"),
    MOD("%"),
    MUL("*"),
    NEQ("!="),
    OR("||"),
    PLUS("+");
    companion object {
        fun operatorByStringValue(stringValue: String): Operator?
                = values().firstOrNull { it.stringValue == stringValue}
    }
}

