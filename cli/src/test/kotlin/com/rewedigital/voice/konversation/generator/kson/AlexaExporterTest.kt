package com.rewedigital.voice.konversation.generator.kson

import com.rewedigital.voice.konversation.generator.alexa.AlexaExporter
import com.rewedigital.voice.konversation.parser.Parser
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class AlexaExporterTest {
    @Test
    fun `Validate pretty printed kvs file result`() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parent, Long.MAX_VALUE)
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEquals(expectedResult, sb.toString())
    }

    @Test
    fun `Validate minified kvs file result`() {
        val help = Parser("cli/src/test/resources/help.kvs").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parent, Long.MAX_VALUE)
        exporter.minified({ line -> sb.append(line) }, help)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    @Test
    fun `Validate pretty printed grammar file result`() {
        val help = Parser("cli/src/test/resources/help.grammar").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parent, Long.MAX_VALUE)
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEquals(expectedResult, sb.toString())
    }

    @Test
    fun `Validate minified grammar file result`() {
        val help = Parser("cli/src/test/resources/help.grammar").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parent, Long.MAX_VALUE)
        exporter.minified({ line -> sb.append(line) }, help)
        assertEquals(expectedMinifiedResult, sb.toString())
    }

    companion object {
        val expectedResult = File("cli/src/test/resources/help-expected-alexa-result.json").readText().replace("\r", "")
        val expectedMinifiedResult = File("cli/src/test/resources/help-expected-alexa-result-minified.json").readText().replace("\r", "")
    }
}