package org.rewedigital.konversation.generator.kson

import org.junit.Test
import org.rewedigital.konversation.assertEqualsIgnoringLineBreaks
import org.rewedigital.konversation.parser.Parser
import java.io.File
import kotlin.test.assertEquals

class KsonExporterTest {
    @Test
    fun prettyKvsResult() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEqualsIgnoringLineBreaks(expectedResult, sb.toString())
    }

    @Test
    fun minifiedKvsResult() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.minified({ line -> sb.append(line) }, help)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    companion object {
        init {
            val dir = File("")
            if (dir.absolutePath.endsWith("cli")) {
                System.setProperty("user.dir", dir.absoluteFile.parentFile.absolutePath)
            }
        }
        val expectedResult = File("cli/src/test/resources/help-expected-kson-result.json").absoluteFile.readText()
        val expectedMinifiedResult = File("cli/src/test/resources/help-expected-kson-result-minified.json").absoluteFile.readText()
    }
}