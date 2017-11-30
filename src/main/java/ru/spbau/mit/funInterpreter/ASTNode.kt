package ru.spbau.mit.funInterpreter

interface Node {
    fun accept(visitor: Interpreter): Int?
}

interface Statement : Node

interface Expression : Statement

data class File(val block: Block) : Node {
    override fun accept(visitor: Interpreter): Int? = visitor.visitFile(this)
}

data class Block(val statements: List<Statement>) : Node {
    override fun accept(visitor: Interpreter): Int? = visitor.visitBlock(this)
}

data class Function(val name: Identifier,
                    val parameterNames: ParameterNames,
                    val body: Block
) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitFunction(this)
}

data class Variable(val name: Identifier, val value: Expression?) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitVariable(this)
}

data class ParameterNames(val names: List<Identifier>) : Statement {
    override fun accept(visitor: Interpreter): Int? = null
}

data class WhileLoop(val condition: Expression, val body: Block) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitWhileLoop(this)
}

data class IfOperator(val condition: Expression,
                      val body: Block,
                      val elseBody: Block?
) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitIfOperator(this)
}

data class Assignment(val name: Identifier, val value: Expression) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitAssignment(this)
}

data class ReturnStatement(val value: Expression) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitReturnStatement(this)
}

data class PrintlnCall(val arguments: Arguments) : Statement {
    override fun accept(visitor: Interpreter): Int? = visitor.visitPrintlnCall(this)
}

data class BinaryExpression(val lhs: Expression,
                            val operator: Operator,
                            val rhs: Expression
) : Expression {
    override fun accept(visitor: Interpreter): Int? = visitor.visitBinaryExpression(this)
}

data class FunctionCall(val name: Identifier, val arguments: Arguments) : Expression {
    override fun accept(visitor: Interpreter): Int? = visitor.visitFunctionCall(this)
}

data class Arguments(val args: List<Expression>) : Node {
    override fun accept(visitor: Interpreter): Int? = null
}

data class Identifier(val name: String) : Expression {
    override fun accept(visitor: Interpreter): Int? = visitor.visitIdentifier(this)
}

data class Literal(val stringValue: String) : Expression {
    override fun accept(visitor: Interpreter): Int? = visitor.visitLiteral(this)
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
        fun operatorByStringValue(stringValue: String): Operator? = values().firstOrNull {
            it.stringValue == stringValue
        }
    }
}

