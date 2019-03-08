package org.rewedigital.konversation.generator.alexa

import org.junit.Test
import org.rewedigital.konversation.assertEqualsIgnoringLineBreaks
import org.rewedigital.konversation.parser.Parser
import java.io.File

class AlexaExporterTest {
    @Test
    fun `Validate pretty printed kvs file result`() {
        val help = Parser("$pathPrefix/help.kvs").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parentFile, Long.MAX_VALUE)
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEqualsIgnoringLineBreaks(expectedResult, sb.toString())
    }

    @Test
    fun `Validate minified kvs file result`() {
        val help = Parser("$pathPrefix/help.kvs").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parentFile, Long.MAX_VALUE)
        exporter.minified({ line -> sb.append(line) }, help)
        assertEqualsIgnoringLineBreaks(expectedMinifiedResult, sb.toString())
    }

    @Test
    fun `Validate pretty printed grammar file result`() {
        val help = Parser("$pathPrefix/help.grammar").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parentFile, Long.MAX_VALUE)
        exporter.prettyPrinted({ line -> sb.append(line) }, help)
        assertEqualsIgnoringLineBreaks(expectedResult, sb.toString())
    }

    @Test
    fun `Validate minified grammar file result`() {
        val help = Parser("$pathPrefix/help.grammar").intents
        val sb = StringBuilder()
        val exporter = AlexaExporter("test", File(".").absoluteFile.parentFile, Long.MAX_VALUE)
        exporter.minified({ line -> sb.append(line) }, help)
        assertEqualsIgnoringLineBreaks(expectedMinifiedResult, sb.toString())
    }

    companion object {
        val pathPrefix = (if (File("").absolutePath.endsWith("cli")) "" else "cli/") + "src/test/resources"
        val expectedResult = File("$pathPrefix/help-expected-alexa-result.json").absoluteFile.readText()
        val expectedMinifiedResult = File("$pathPrefix/help-expected-alexa-result-minified.json").absoluteFile.readText()
    }
}