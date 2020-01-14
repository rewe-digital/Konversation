package org.rewedigital.konversation

import org.junit.Assert
import org.junit.Test

class CharTest {

    @Test
    fun latinize() {
        val input = "Ţȟĭś ÏŠ ä Ⱦèßẗ"
        val expected = "This IS a Test"
        Assert.assertEquals("$input should be latinized to $expected", expected, input.latinize())
    }

    @Test
    fun camelCaseIt() {
        val input = "Yet another TEST"
        val expected = "YetAnotherTest"
        Assert.assertEquals("$input should be latinized to $expected", expected, input.camelCaseIt())
        Assert.assertEquals("$input should be latinized to $expected", "ReweRdkWelcomeNewUser", "REWE.rdk.welcome-new-user".camelCaseIt().latinize())
    }
}