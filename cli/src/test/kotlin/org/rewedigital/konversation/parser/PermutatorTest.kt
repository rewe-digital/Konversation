package org.rewedigital.konversation.parser

import org.junit.Test
import kotlin.test.assertEquals

class PermutatorTest {
    @Test
    fun `verify generation of 1 permutations`() {
        verifyPermutations("{a}", "a")
    }

    @Test
    fun `verify generation of 2 permutations`() {
        verifyPermutations("{a|b}", "a", "b")
    }

    @Test
    fun `verify generation of 3 permutations`() {
        verifyPermutations("{a|b|c}", "a", "b", "c")
    }

    @Test
    fun `verify that masking works`() {
        verifyPermutations("\\{a|b\\}", "\\{a|b\\}")
    }

    @Test
    fun `verify processing of variables`() {
        verifyPermutations("\${a.b}", "\${a.b}")
    }

    @Test
    fun `verify mixed input`() {
        verifyPermutations("\${a.b} {c|d} \\{e|f\\} {g}", "\${a.b} c \\{e|f\\} g", "\${a.b} d \\{e|f\\} g")
    }

    private fun verifyPermutations(input: String, vararg expected: String) {
        val actual = Permutator.generate(input)
        assertEquals(expected.size, actual.size, "Unexpected count of permutations")
        for (i in 0 until expected.size) {
            assertEquals(expected[i], actual[i], "${i.asOrdinal()} permutation has unexpected value")
        }
    }

    private fun Int.asOrdinal(): String = when (this) {
        0 -> "First"
        1 -> "Second"
        2 -> "Third"
        3 -> "Fourth"
        else -> "${this + 1}."
    }
}