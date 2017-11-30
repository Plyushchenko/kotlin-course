package ru.spbau.mit

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.misc.ParseCancellationException
import ru.spbau.mit.funInterpreter.*
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.PrintStream
import java.nio.file.Path
import java.nio.file.Paths

private class ThrowingListener : BaseErrorListener() {
    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int,
                             charPositionInLine: Int, msg: String?, e: RecognitionException?) {
        throw ParseCancellationException("Syntax error! Line $line:$charPositionInLine, $msg")
    }
}

fun buildAST(path: Path): File = buildAST(FunLexer(CharStreams.fromPath(path)))

internal fun buildAST(s: String): File = buildAST(FunLexer(CharStreams.fromString(s)))

private fun buildAST(lexer: FunLexer): File {
    val throwingListener = ThrowingListener()
    lexer.removeErrorListeners()
    lexer.addErrorListener(throwingListener)
    val parser = FunParser(BufferedTokenStream(lexer))
    parser.removeErrorListeners()
    parser.addErrorListener(throwingListener)
    return Visitor().visitFile(parser.file())
}

internal fun interpretAST(ast: File) {
    Interpreter().visit(ast)
}

internal fun interpretAST(ast: File, context: Context, printStream: PrintStream) {
    Interpreter(context, printStream).visit(ast)
}

fun interpretFile(path: Path) {
    val ast = buildAST(path)
    interpretAST(ast)
}

internal fun interpretFile(path: Path, context: Context, printStream: PrintStream) {
    val ast = buildAST(path)
    interpretAST(ast, context, printStream)
}

fun main(args: Array<String>) {
    try {
        if (args.isEmpty()) {
            println("First argument should be path to file which will be interpreted!")
            return
        }
        interpretFile(Paths.get(args[0]))
    } catch (e: Exception) {
        println(e.message)
    }
}