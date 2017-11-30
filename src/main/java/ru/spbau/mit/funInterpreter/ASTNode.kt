package ru.spbau.mit.funInterpreter

interface Node {
    fun <T> accept(visitor: ASTVisitor<T>): T
}

interface Statement : Node

interface Expression : Statement

data class File(val block: Block) : Node {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitFile(this)
}

data class Block(val statements: List<Statement>) : Node {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitBlock(this)
}

data class Function(val name: Identifier,
                    val parameterNames: ParameterNames,
                    val body: Block
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitFunction(this)
}

data class Variable(val name: Identifier, val value: Expression?) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitVariable(this)
}

data class ParameterNames(val names: List<Identifier>) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitParameterNames(this)
}

data class WhileLoop(val condition: Expression, val body: Block) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitWhileLoop(this)
}

data class IfOperator(val condition: Expression,
                      val body: Block,
                      val elseBody: Block?
) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitIfOperator(this)
}

data class Assignment(val name: Identifier, val value: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitAssignment(this)
}

data class ReturnStatement(val value: Expression) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitReturnStatement(this)
}

data class PrintlnCall(val arguments: Arguments) : Statement {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitPrintlnCall(this)
}

data class BinaryExpression(val lhs: Expression,
                            val operator: Operator,
                            val rhs: Expression
) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitBinaryExpression(this)
}

data class FunctionCall(val name: Identifier, val arguments: Arguments) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitFunctionCall(this)
}

data class Arguments(val args: List<Expression>) : Node {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitArguments(this)
}

data class Identifier(val name: String) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitIdentifier(this)
}

data class Literal(val stringValue: String) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitLiteral(this)
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
