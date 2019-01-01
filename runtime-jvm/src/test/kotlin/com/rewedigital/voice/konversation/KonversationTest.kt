package com.rewedigital.voice.konversation

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KonversationTest {
    @Test
    fun `Check display and ssml responses`() {
        setEnv(mapOf("USE_FIRST_OPTION" to "ON"))
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertEquals("Du kannst mit dieser App Rezepte und Angebote anhören.", sut.displayText)
        assertEquals("Du kannst mit dieser App Rezepte und Angebote anhören. Cool nicht wahr? Womit sollen wir weiter machen?", sut.ssml)
    }

    @Test
    fun `Check varity of responses`() {
        setEnv(mapOf("USE_FIRST_OPTION" to "OFF"))
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
        setEnv(mapOf("USE_FIRST_OPTION" to "ON"))
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertEquals(listOf("Angebote", "Rezept"), sut.suggestions)
    }

    @Test
    fun `Check reprompts`() {
        setEnv(mapOf("USE_FIRST_OPTION" to "ON"))
        val sut = Konversation("help", Environment("google", "de-DE", false)).createOutput()
        assertNull(sut.reprompts[0])
        assertEquals("Kann ich dir noch helfen?", sut.reprompts[1])
        assertEquals("Womit sollen wir weitermachen?", sut.reprompts[2])
        assertNull(sut.reprompts[3])
    }

    // This is a hack to set environment variables for testing.
    // Feel free to patch this away. The environment variable USE_FIRST_OPTION is used within the JVM's Random implementation to use only the first option for tests in this file.
    private fun setEnv(newenv: Map<String, String>) {
        try {
            val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
            val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
            theEnvironmentField.isAccessible = true
            val env = theEnvironmentField.get(null) as MutableMap<String, String>
            env.putAll(newenv)
            val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
            theCaseInsensitiveEnvironmentField.isAccessible = true
            val cienv = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
            cienv.putAll(newenv)
        } catch (e: NoSuchFieldException) {
            val classes = Collections::class.java.declaredClasses
            val env = System.getenv()
            for (cl in classes) {
                if ("java.util.Collections\$UnmodifiableMap" == cl.getName()) {
                    val field = cl.getDeclaredField("m")
                    field.isAccessible = true
                    val obj = field.get(env)
                    val map = obj as MutableMap<String, String>
                    map.clear()
                    map.putAll(newenv)
                }
            }
        }
    }
}