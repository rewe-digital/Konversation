package org.rewedigital.konversation.generator.kson

import org.junit.Test
import org.rewedigital.konversation.assertEqualsIgnoringLineBreaks
import org.rewedigital.konversation.parser.Parser
import java.io.File
import kotlin.test.assertEquals

class KsonExporterTest {
    @Test
    fun prettyKvsResult() {
        val help = Parser("$pathPrefix/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.prettyPrinted({ line -> sb.append(line) }, help, null)
        assertEqualsIgnoringLineBreaks(expectedResult, sb.toString())
    }

    @Test
    fun minifiedKvsResult() {
        val help = Parser("$pathPrefix/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.minified({ line -> sb.append(line) }, help, null)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    companion object {
        val pathPrefix = (if (File("").absolutePath.endsWith("cli")) "" else "cli/") + "src/test/resources"
        val expectedResult = File("$pathPrefix/help-expected-kson-result.json").absoluteFile.readText()
        val expectedMinifiedResult = File("$pathPrefix/help-expected-kson-result-minified.json").absoluteFile.readText()
    }
}