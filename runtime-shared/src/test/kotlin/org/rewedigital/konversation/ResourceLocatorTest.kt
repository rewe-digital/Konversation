package org.rewedigital.konversation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

open class ResourceLocatorTest {
    @Test
    fun localeTest() {
        val sut = ResourceLocatorMock("konversation/test.kson",
                                      "konversation-de_de/test.kson",
                                      "konversation-de_at/test.kson",
                                      "konversation-fr/test.kson",
                                      "konversation-en/test.kson",
                                      "konversation-en_us/test.kson",
                                      "konversation-en_gb/test.kson",
                                      "konversation-alexa/test.kson",
                                      "konversation-alexa-de_de/test.kson",
                                      "konversation-alexa-de_at/test.kson",
                                      "konversation-alexa-en/test.kson",
                                      "konversation-alexa-en_us/test.kson",
                                      "konversation-alexa-en_gb/test.kson",
                                      "konversation-google/test.kson",
                                      "konversation-google-de_de/test.kson",
                                      "konversation-google-de_at/test.kson",
                                      "konversation-google-en/test.kson",
                                      "konversation-google-en_us/test.kson",
                                      "konversation-google-en_gb/test.kson")
        assertEquals("konversation/test.kson", sut.locate("test", Environment("magenta", "es")), "Should fallback to generic konversation.")
        assertEquals("konversation-de_de/test.kson", sut.locate("test", Environment("magenta", "de_DE")), "Should provide correct locale.")
        assertEquals("konversation-de_de/test.kson", sut.locate("test", Environment("magenta", "De_dE")), "Should provide correct locale with nonsense cases.")
        assertEquals("konversation-google-de_de/test.kson", sut.locate("test", Environment("google", "de_de")), "Should provide correct locale and platform.")
        assertEquals("konversation-fr/test.kson", sut.locate("test", Environment("magenta", "fr_FR")), "Should fallback to generic locale.")
        assertEquals("konversation-alexa/test.kson", sut.locate("test", Environment("alexa", "es")), "Should fallback to generic platform.")
        assertFailsWith(RuntimeException::class, message = "Should throw exception when the conversation is not found.") {sut.locate("404", Environment("alexa", "es"))}
    }
}

private class ResourceLocatorMock(private vararg val validPath: String) : ResourceLocator() {
    override fun checkPath(path: String) = validPath.contains(path)
}