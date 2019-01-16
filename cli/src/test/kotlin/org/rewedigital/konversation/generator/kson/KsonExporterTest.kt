package org.rewedigital.konversation.generator.kson

import org.rewedigital.konversation.parser.Parser
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class KsonExporterTest {
    @Test
    fun prettyKvsResult() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEquals(expectedResult, sb.toString())
    }

    //@Test
    fun minifiedKvsResult() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.minified({ line -> sb.append(line) }, help)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    //@Test
    fun prettyGrammarResult() {
        val help = Parser("cli/src/test/resources/help.grammar").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEquals(expectedResult, sb.toString())
    }

    //@Test
    fun minifiedGrammarResult() {
        val help = Parser("cli/src/test/resources/help.grammar").intents
        val sb = StringBuilder()
        val exporter = KsonExporter("Help")
        exporter.minified({ line -> sb.append(line) }, help)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    companion object {
        val expectedResult = File("cli/src/test/resources/help-expected-kson-result.json").readText()
        val expectedMinifiedResult = File("cli/src/test/resources/help-expected-alexa-result-minified.json").readText()
    }
}