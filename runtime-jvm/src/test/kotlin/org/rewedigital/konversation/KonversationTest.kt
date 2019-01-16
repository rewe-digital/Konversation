package org.rewedigital.konversation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KonversationTest {
    @Test
    fun `Check display and ssml responses`() {
        Random.forcedValue = 0
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertEquals("Du kannst mit dieser App Rezepte und Angebote anhören.", sut.displayText)
        assertEquals("Du kannst mit dieser App Rezepte und Angebote anhören. Cool nicht wahr? Womit sollen wir weiter machen?", sut.ssml)
    }

    @Test
    fun `Check varity of responses`() {
        Random.forcedValue = null
        val sut = Konversation("help", Environment("google", "de-DE", false))
        val displayTexts = mutableListOf<String>()
        val ssml = mutableListOf<String>()
        for (i in 0 .. 50) {
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
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertEquals(listOf("Angebote", "Rezept"), sut.suggestions)
    }

    @Test
    fun `Check reprompts`() {
        Random.forcedValue = 0
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertNull(sut.reprompts[0])
        assertEquals("Kann ich dir noch helfen?", sut.reprompts[1])
        assertEquals("Womit sollen wir weitermachen?", sut.reprompts[2])
        assertNull(sut.reprompts[3])
    }
}