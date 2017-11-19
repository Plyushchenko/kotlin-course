package ru.spbau.mit
import kotlin.test.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun mathTest() {
        val actual = document {
            documentClass("MathTest")
            math("2^4 = 4^2")
            + "MAYBE"
            math("2^5 = 5^2")
            + "NOPE!"
        }.toString()
        val expected = """
            |\documentclass{MathTest}
            |\begin{document}
            |    $2^4 = 4^2$
            |    MAYBE
            |    $2^5 = 5^2$
            |    NOPE!
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun listTest() {
        val actual = document {
            documentClass("article")
            usePackage("enumitem")
            enumerate(listOf("label=(\\roman*)")) {
                item {
                    + "Wicked or weakness?"
                }
                item {
                    + "You gotta see this"
                }
                item {
                    itemize {
                        item(listOf("W")) {
                            +"Inner"
                        }
                        item(listOf("O")) {
                            +"List"
                        }
                        item(listOf("W")) {
                            +"Is"
                        }
                        item {
                            +"Here"
                        }
                    }
                }
            }
        }.toString()
        val expected = """
            |\documentclass{article}
            |\usepackage{enumitem}
            |\begin{document}
            |    \begin{enumerate}[label=(\roman*)]
            |        \item
            |            Wicked or weakness?
            |        \item
            |            You gotta see this
            |        \item
            |            \begin{itemize}
            |                \item[W]
            |                    Inner
            |                \item[O]
            |                    List
            |                \item[W]
            |                    Is
            |                \item
            |                    Here
            |            \end{itemize}
            |    \end{enumerate}
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun preambleTest() {
        val actual = document {
            documentClass("article", listOf("12pt"))
            usePackage("framed")
            usePackage("inputenc", listOf("cp1251"))
            usePackage("babel", listOf("russian, english"))
            + "BOOM"
        }.toString()
        val expected = """
            |\documentclass[12pt]{article}
            |\usepackage{framed}
            |\usepackage[cp1251]{inputenc}
            |\usepackage[russian, english]{babel}
            |\begin{document}
            |    BOOM
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun alignmentAndFramingTest() {
        val actual = document {
            documentClass("article", listOf("12pt"))
            usePackage("framed")
            framed {
                flushLeft {
                    + "LOOK TO THE RIGHT"
                }
                center {
                    + "<><>"
                }
                flushRight {
                    + "LOOK TO THE LEFT"
                }
            }
        }.toString()
        val expected = """
            |\documentclass[12pt]{article}
            |\usepackage{framed}
            |\begin{document}
            |    \begin{framed}
            |        \begin{flushleft}
            |            LOOK TO THE RIGHT
            |        \end{flushleft}
            |        \begin{center}
            |            <><>
            |        \end{center}
            |        \begin{flushright}
            |            LOOK TO THE LEFT
            |        \end{flushright}
            |    \end{framed}
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, actual)
    }

    @Test
    fun testCustomTags() {
        val actual = document {
            customMulti("Huge", listOf()) {
                +"This text is huge"
            }
            customSingle("frac", listOf("1", "2"))
        }.toString()
        val expected = """
            |\begin{document}
            |    \begin{Huge}
            |        This text is huge
            |    \end{Huge}
            |    \frac{1}{2}
            |\end{document}
            |""".trimMargin()
        assertEquals(expected, actual)
    }
}
