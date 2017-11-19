package ru.spbau.mit
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TestSource {
    @Test
    fun testSample1() {
        val solver = Solver(3, "a?c".toCharArray())
        assertTrue(solver.removePairedQuestionMarks())
        assertFalse(solver.removeSingleQuestionMarks())
        Assert.assertArrayEquals(solver.solve(), "IMPOSSIBLE".toCharArray())
    }

    @Test
    fun testSample2() {
        val solver = Solver(2, "a??a".toCharArray())
        assertTrue(solver.removePairedQuestionMarks())
        Assert.assertArrayEquals("abba".toCharArray(), solver.bookTitle)
        assertTrue(solver.removeSingleQuestionMarks())
        Assert.assertArrayEquals(solver.solve(), "abba".toCharArray())
    }


    @Test
    fun testSample3() {
        val solver = Solver(2, "?b?a".toCharArray())
        assertTrue(solver.removePairedQuestionMarks())
        Assert.assertArrayEquals("?b?a".toCharArray(), solver.bookTitle)
        assertTrue(solver.removeSingleQuestionMarks())
        Assert.assertArrayEquals(solver.solve(), "abba".toCharArray())
    }

}