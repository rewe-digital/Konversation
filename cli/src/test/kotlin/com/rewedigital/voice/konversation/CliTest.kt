package com.rewedigital.voice.konversation

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CliTest {
    @Test
    fun `No args will show help`() {
        val sut = CliTestHelper.getOutput()
        assertEquals(sut.output, "Missing arguments! Please specify at least the kvs or grammar file you want to process.\n\n$helpOutput\n\n")
        assertEquals(-1, sut.exitCode)
    }

    @Test
    fun `Help is working`() {
        val sut = CliTestHelper.getOutput("help")
        assertEquals(sut.output, "$helpOutput\n\n")
        assertNull(sut.exitCode)
    }

    @Test
    fun `Check processing of grammar files`() {
        val testOutputFile = "test.out"
        val expectedOutputFile = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val outputFile = File(testOutputFile)
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "--export-alexa", testOutputFile, "-invocation", "test")
            assertEquals(sut.output, "Parsing finished. Found 1 intents.\n")
            assertNull(sut.exitCode)
            assertTrue(outputFile.exists())
            assertTrue(File(expectedOutputFile).exists())
            assertEquals(outputFile.readText(), File(expectedOutputFile).readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Check handling of missing invocation name`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "--export-alexa", "test.out")
        assertEquals(sut.output, "Parsing finished. Found 1 intents.\n" +
                "Invocation name is missing! Please specify the invocation name with the parameter -invocation <name>.\n")
        assertEquals(-1, sut.exitCode)
    }

    @Test
    fun `Check processing of konversation files`() {
        val testOutputFile = "test.out"
        val expectedOutputFile = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val outputFile = File(testOutputFile)
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.kvs", "--export-alexa", testOutputFile, "-invocation", "test")
            assertEquals(sut.output, "Parsing finished. Found 1 intents.\n")
            assertNull(sut.exitCode)
            assertTrue(outputFile.exists())
            assertTrue(File(expectedOutputFile).exists())
            assertEquals(outputFile.readText(), File(expectedOutputFile).readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Test dump option`() {
        val testOutputFile = "test.out"
        val expectedOutputFile = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val outputFile = File(testOutputFile)
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.kvs", "-dump")
            assertEquals(sut.output, "Parsing finished. Found 1 intents.\nDumping Help...\n" +
                    "Response of Help\n" +
                    "---\n")
            assertNull(sut.exitCode)
            assertTrue(outputFile.exists())
            assertTrue(File(expectedOutputFile).exists())
            assertEquals(outputFile.readText(), File(expectedOutputFile).readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Test non existing file`() {
        val sut = CliTestHelper.getOutput("404.kvs", "-dump")
        assertEquals(sut.output, "Unknown argument \"404.kvs\".\nInput file not found!\n")
        assertEquals(-1, sut.exitCode)
    }

    @Test
    fun `Test big grammar file`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/huge.grammar", "-stats", "-count")
        assertEquals(sut.output, "Parsing finished. Found 1 intents.\n" +
                "Test has 1 utterances which have in total 1.000 permutations\n" +
                "That are in total 1.000 permutations!\n" +
                "Test has now 1000 sample utterances\n" +
                "Generated in total 1000 Utterances")
        assertNull(sut.exitCode)
    }

    @Test
    fun `Check the debug options`() {
        val testOutputFile = "test.out"
        val expectedOutputFile = "cli/src/test/resources/huge-expected-alexa-result-limited.json"
        val outputFile = File(testOutputFile)
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/huge.grammar", "--export-alexa", testOutputFile, "-invocation", "huge", "-prettyprint", "-limit", "20")
            assertEquals(sut.output, "Parsing finished. Found 2 intents.\n")
            assertNull(sut.exitCode)
            assertTrue(outputFile.exists())
            assertTrue(File(expectedOutputFile).exists())
            assertEquals(outputFile.readText(), File(expectedOutputFile).readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }
    @Test
    fun `Test dir processing`() {
        val sut = ParseTestCli("cli/src/test/resources/")
        assertEquals(4, sut.files.count())
        assertEquals("cli/src/test/resources/konversation/help.kvs", sut.files[0].replace('\\', '/'))
        assertEquals("cli/src/test/resources/konversation-alexa/help.kvs", sut.files[1].replace('\\', '/'))
        assertEquals("cli/src/test/resources/konversation-alexa-de/help.kvs", sut.files[2].replace('\\', '/'))
        assertEquals("cli/src/test/resources/konversation-en/help.kvs", sut.files[3].replace('\\', '/'))
    }

    @Test
    fun `Konversation directory processing`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/", "--export-kson", "build/out/kson")
        println(sut.output)
        assertTrue(File("build/out/kson/konversation/help.kson").isFile)
        assertTrue(File("build/out/kson/konversation-alexa/help.kson").isFile)
        assertTrue(File("build/out/kson/konversation-alexa-de/help.kson").isFile)
        assertTrue(File("build/out/kson/konversation-en/help.kson").isFile)
    }

    private class CliTestHelper : Cli() {
        private var exitCode: Int? = null

        override fun exit(status: Int) {
            exitCode = status
        }

        companion object {
            fun getOutput(vararg args: String): TestResult {
                val outputStream = ByteArrayOutputStream(4096)
                val out = System.out
                System.setOut(PrintStream(outputStream))
                val helper = CliTestHelper()
                helper.parseArgs(args.toList().toTypedArray())
                System.out.flush()
                System.setOut(out)
                return TestResult(outputStream.toString().replace("\r", ""), helper.exitCode)
            }
        }
    }

    data class TestResult(val output: String, val exitCode: Int?)

    private class ParseTestCli(val path: String) : Cli() {
        val files = mutableListOf<String>()

        init {
            parseArgs(arrayOf(path, "-dump"))
        }

        override fun parseFiles(input: String) {
            files.add(input)
        }
    }

    companion object {
        val helpOutput = """Arguments for konversation:
[-help]                     Print this help
[-count]                    Count the permutations and print this to the console
[-stats]                    Print out some statistics while generation
[-cache]                    Cache everything even if an utterance has just a single permutation
[--export-alexa <OUTFILE>]  Write the resulting json to OUTFILE instead of result.json
[-invocation <NAME>]        Define the invocation name for the Alexa export
[-limit <COUNT>]            While pretty printing the json to the output file limit the utterances count per intent
[--export-kson <OUTDIR>]    Compiles the kvs file to kson resource files which are required for the runtime
[-dump]                     Dump out all intents to its own txt file
[-prettyprint]              Generate a well formatted json for easier debugging
<FILE>                      The grammar or kvs file to parse"""
    }
}