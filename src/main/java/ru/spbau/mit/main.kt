package ru.spbau.mit

import java.io.InputStream
import java.util.*

class Solver(k: Int, internal val bookTitle: CharArray) {
    companion object {
        fun read(inputStream: InputStream) : Solver {
            val input = Scanner(inputStream)
            val k = input.nextInt()
            val bookTitle = input.next().toCharArray()
            return Solver(k, bookTitle)
        }

        val IMPOSSIBLE = "IMPOSSIBLE".toCharArray()
    }

    private val unusedLetters  = (('a' until 'a' + k) - bookTitle.toSortedSet()).toSortedSet()
    private val halfOfBookTitleSize = bookTitle.size / 2

    fun solve(): CharArray {
        if (!removePairedQuestionMarks())
            return IMPOSSIBLE
        if (!removeSingleQuestionMarks())
            return IMPOSSIBLE
        return bookTitle
    }

    internal fun removeSingleQuestionMarks(): Boolean {
        var i = 0
        while (i < halfOfBookTitleSize) {
            val correspondingIndex = getCorrespondingIndex(i)
            if (bookTitle[i] == '?' || bookTitle[correspondingIndex] == '?') {
                if (bookTitle[i] == '?') {
                    bookTitle[i] = bookTitle[correspondingIndex]
                } else {
                    bookTitle[correspondingIndex] = bookTitle[i]
                }
            } else {
                if (bookTitle[i] != bookTitle[correspondingIndex]) {
                    return false
                }
            }
            i++
        }
        return true
    }

    internal fun removePairedQuestionMarks(): Boolean {
        var i = halfOfBookTitleSize
        while (i < bookTitle.size) {
            val correspondingIndex = getCorrespondingIndex(i)
            if (bookTitle[i] == '?' && bookTitle[correspondingIndex] == '?') {
                bookTitle[i] = getAndExtractMaxUnusedLetterOrA()
                bookTitle[correspondingIndex] = bookTitle[i]
            }
            i++
        }
        return unusedLetters.isEmpty()
    }

    private fun getAndExtractMaxUnusedLetterOrA(): Char {
        if (unusedLetters.isEmpty())
            return 'a'
        val lastFromUnusedLetters = unusedLetters.last()
        unusedLetters.remove(lastFromUnusedLetters)
        return lastFromUnusedLetters
    }

    private fun getCorrespondingIndex(i: Int): Int = bookTitle.size - i - 1
}

fun main(args: Array<String>) {
    print(Solver.read(System.`in`).solve())
}
