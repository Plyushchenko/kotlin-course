package ru.spbau.mit

import java.util.*

class Solver(k: Int, internal val bookTitle: CharArray) {
    companion object {
        private val IMPOSSIBLE = "IMPOSSIBLE".toCharArray()
    }
    private val unusedLetters: ArrayDeque<Char>
    init {
        val firstKLetters = 'a' until 'a' + k
        val usedLetters = bookTitle.toSet()
        unusedLetters = ArrayDeque(firstKLetters - usedLetters)
    }

    fun solve(): CharArray {
        val ok = removePairedQuestionMarks() && removeSingleQuestionMarks()
        return if (ok) bookTitle else IMPOSSIBLE
    }

    internal fun removeSingleQuestionMarks(): Boolean {
        for (i in 0 until bookTitle.size / 2) {
            val currentSymbol = bookTitle[i]
            val correspondingIndex = i.getCorrespondingIndex
            val correspondingSymbol = bookTitle[correspondingIndex]
            when {
                currentSymbol == '?' -> bookTitle[i] = correspondingSymbol
                correspondingSymbol == '?' -> bookTitle[correspondingIndex] = currentSymbol
                currentSymbol != correspondingSymbol -> return false
            }
        }
        return true
    }

    internal fun removePairedQuestionMarks(): Boolean {
        for (i in bookTitle.size / 2 until bookTitle.size) {
            val currentSymbol = bookTitle[i]
            val correspondingIndex = i.getCorrespondingIndex
            val correspondingSymbol = bookTitle[correspondingIndex]
            if (currentSymbol == '?' && correspondingSymbol == '?') {
                bookTitle[i] = getAndExtractMaxUnusedLetterOrA()
                bookTitle[correspondingIndex] = bookTitle[i]
            }
        }
        return unusedLetters.isEmpty()
    }

    private fun getAndExtractMaxUnusedLetterOrA(): Char {
        return if (unusedLetters.isEmpty()) 'a' else unusedLetters.removeLast()
    }

    private val Int.getCorrespondingIndex:Int get() = bookTitle.size - this - 1
}

fun main(args: Array<String>) {
    val input = Scanner(System.`in`)
    val k = input.nextInt()
    val bookTitle = input.next().toCharArray()
    print(Solver(k, bookTitle).solve())
}
