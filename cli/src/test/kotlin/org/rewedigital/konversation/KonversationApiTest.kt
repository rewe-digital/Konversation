package org.rewedigital.konversation

import org.junit.Assert.assertEquals
import org.junit.Test

class KonversationApiTest {
    @Test
    fun latinize() {
        val input = "Ţȟĭś ÏŠ ä Ⱦèßẗ"
        val expected = "This IS a Test"
        assertEquals("$input should be latinized to $expected", expected, input.latinize())
    }

    @Test
    fun camelCaseIt() {
        val input = "Yet another TEST"
        val expected = "YetAnotherTest"
        assertEquals("$input should be latinized to $expected", expected, input.camelCaseIt())
    }
}