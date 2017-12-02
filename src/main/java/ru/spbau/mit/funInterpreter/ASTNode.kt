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

data class ParameterNames(val names: List<Identifier>)

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

    enum class Operator(val stringValue: String) {
        AND("&&") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs.bool && rhs.bool).int
        },
        DIV("/") {
            override fun invoke(lhs: Int, rhs: Int): Int = lhs / rhs
        },
        EQ("==") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs == rhs).int
        },
        GEQ(">=") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs >= rhs).int
        },
        GT(">") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs > rhs).int
        },
        LEQ("<=") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs <= rhs).int
        },
        LT("<") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs < rhs).int
        },
        MINUS("-") {
            override fun invoke(lhs: Int, rhs: Int): Int = lhs - rhs
        },
        MOD("%") {
            override fun invoke(lhs: Int, rhs: Int): Int = lhs % rhs
        },
        MUL("*") {
            override fun invoke(lhs: Int, rhs: Int): Int = lhs * rhs
        },
        NEQ("!=") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs != rhs).int
        },
        OR("||") {
            override fun invoke(lhs: Int, rhs: Int): Int = (lhs.bool || rhs.bool).int
        },
        PLUS("+") {
            override fun invoke(lhs: Int, rhs: Int): Int = lhs + rhs
        };

        abstract operator fun invoke(lhs: Int, rhs: Int): Int

        companion object {
            fun operatorByStringValue(stringValue: String): Operator? = values().firstOrNull {
                it.stringValue == stringValue
            }

            private val Int.bool get() = this != 0

            private val Boolean.int get() = if (this) 1 else 0
        }
    }
}

data class FunctionCall(val name: Identifier, val arguments: Arguments) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitFunctionCall(this)
}

data class Arguments(val args: List<Expression>)

data class Identifier(val name: String) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitIdentifier(this)
}

data class Literal(val stringValue: String) : Expression {
    override fun <T> accept(visitor: ASTVisitor<T>): T = visitor.visitLiteral(this)
}
