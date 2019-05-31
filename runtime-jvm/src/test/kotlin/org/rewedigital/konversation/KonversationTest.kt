package org.rewedigital.konversation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KonversationTest {
    @Test
    fun `Check display and ssml responses`() {
        Random.forcedValue = 0
        val sut = Konversation("help", environment).createOutput()
        assertEquals("Du kannst mit dieser App Rezepte und Angebote anhören.", sut.displayText)
        assertEquals("<speak>Du kannst mit dieser App Rezepte und Angebote anhören. Cool nicht wahr? Womit sollen wir weiter machen?</speak>", sut.ssml)
    }

    @Test
    fun `Check varity of responses`() {
        Random.forcedValue = null
        val sut = Konversation("help", environment)
        val displayTexts = mutableListOf<String>()
        val ssml = mutableListOf<String>()
        for (i in 0..50) {
            val output = sut.createOutput()
            displayTexts += output.displayText
            ssml += output.ssml
        }
        assertTrue(displayTexts.distinct().size > 1)
        assertTrue(ssml.distinct().size > 1)
    }

    @Test
    fun `Check suggestions`() {
        Random.forcedValue = 0
        val sut = Konversation("help", environment).createOutput()
        assertEquals(listOf("Angebote", "Rezept"), sut.suggestions)
    }

    @Test
    fun `Check reprompts`() {
        Random.forcedValue = 0
        val sut = Konversation("help", environment).createOutput()
        assertNull(sut.reprompts[0])
        assertEquals("Kann ich dir noch helfen?", sut.reprompts[1])
        assertEquals("Womit sollen wir weitermachen?", sut.reprompts[2])
        assertNull(sut.reprompts[3])
    }

    @Test
    fun `Check if applying variables work`() {
        val data = mapOf("planet" to "Welt", "user.name" to "René", "age" to 12.34)

        val result = Konversation("ApplyVariableTest", Environment("google", "de-DE")).createOutput(data)

        val expectedText = "Hallo Welt. You are René!"
        val expectedSsml = "<speak>Hallo Welt. You are René! And I know this age: 12,34</speak>"
        assertEquals(expectedText, result.displayText)
        assertEquals(expectedSsml, result.ssml)
    }

    @Test
    fun `Check if different locale work`() {
        val data = mapOf("planet" to "Welt", "user.name" to "René", "age" to 12.34)

        val result = Konversation("ApplyVariableTest", Environment("google", "en-US")).createOutput(data)

        val expectedText = "Hallo Welt. You are René!"
        val expectedSsml = "<speak>Hallo Welt. You are René! And I know this age: 12.34</speak>"
        assertEquals(expectedText, result.displayText)
        assertEquals(expectedSsml, result.ssml)
    }

    companion object {
        val environment = Environment("google", "de-DE")
    }
}