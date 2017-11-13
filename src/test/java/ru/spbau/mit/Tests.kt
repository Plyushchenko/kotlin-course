package ru.spbau.mit

import org.antlr.v4.runtime.misc.ParseCancellationException
import org.junit.Test
import ru.spbau.mit.funInterpreter.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.test.assertEquals

class Tests {
    @Test
    fun integrationTests() {
        val pathTemplateAsString = "src/test/resources/example"
        for (i in 1..6) {
            val expected = Files.readAllBytes(Paths.get(
                    pathTemplateAsString + i.toString() + ".ans")
            )
            val byteArrayOutputStream = ByteArrayOutputStream()
            val printStream = PrintStream(byteArrayOutputStream, true)
            interpretFile(Paths.get(pathTemplateAsString + i.toString()), Context(), printStream)
            assert(Arrays.equals(expected, byteArrayOutputStream.toByteArray()))
        }
    }

    @Test
    fun correctASTTests() {
        assertEquals(File(Block(listOf(
                Variable(Identifier("i"), BinaryExpression(
                        Literal("1"), Operator.PLUS, BinaryExpression(
                        Literal("2"), Operator.PLUS, BinaryExpression(
                        Literal("3"), Operator.PLUS, Literal("4"))))),
                PrintlnCall(Arguments(listOf(Identifier("i"))))))),
                    buildAST("""
                        |var i = 1 + 2 + 3 + 4
                        |println(i)
                        |""".trimMargin()))
        assertEquals(File(Block(listOf(
                Variable(Identifier("x"), Literal("5")),
                Function(Identifier("f"),
                        ParameterNames(listOf(Identifier("x"))),
                        Block(listOf(ReturnStatement(Identifier("x"))))),
                PrintlnCall(Arguments(emptyList())),
                Assignment(Identifier("x"), BinaryExpression(
                        Identifier("x"), Operator.PLUS, FunctionCall(
                            Identifier("f"), Arguments(listOf(Identifier("x"))))))))),
                    buildAST("""
                        |var x = 5
                        |fun f(x) {
                        |    return x
                        |}
                        |println()
                        |x = x + f(x)
                        |""".trimMargin()))
    }

    @Test(expected = ParseCancellationException::class)
    fun lexerExceptionsTest() {
        try {
            buildAST("var x = 5 ^ 6")
        } catch (e: Exception) {
            assertEquals("Syntax error! Line 1:10, token recognition error at: '^'", e.message)
            throw e
        }
    }

    @Test(expected = ParseCancellationException::class)
    fun parserExceptionTest() {
        try {
            buildAST("var x = var")
        } catch (e: Exception) {
            assertEquals("Syntax error! Line 1:8, mismatched input 'var' expecting {'(', " +
                    "IDENTIFIER, LITERAL}", e.message)
            throw e
        }
    }

    @Test(expected = NotFoundException::class)
    fun unknownVariableTest() {
        interpretAST(buildAST("y = 6"))
    }

    @Test(expected = NotFoundException::class)
    fun unknownFunctionTest() {
        interpretAST(buildAST("val y = f(6)"))
    }

    @Test(expected = RedefinitionException::class)
    fun redefinitionVariableTest() {
        interpretAST(buildAST("""
            |var x = 5
            |var x = 6
            |""".trimMargin()))
    }

    @Test(expected = RedefinitionException::class)
    fun redefinitionFunctionTest() {
        interpretAST(buildAST("""
            |fun f(x) {}
            |fun f(x, y) {}
            |""".trimMargin()))
    }


    @Test(expected = WrongArgumentsNumberException::class)
    fun arityTest() {
        interpretAST(buildAST("""
            |fun f(x) {
            | return x + 95
            | }
            |println(f(105, 905))
            |""".trimMargin()))
    }
}