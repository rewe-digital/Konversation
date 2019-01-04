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
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "-out", testOutputFile, "-invocation", "test")
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
        val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "-out", "test.out")
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
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.kvs", "-out", testOutputFile, "-invocation", "test")
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

    companion object {
        val helpOutput = """Arguments for konversation:
[-help]             Print this help
[-count]            Count the permutations and print this to the console
[-stats]            Print out some statistics while generation
[-cache]            Cache everything even if an utterance has just a single permutation
[-out <OUTFILE>]    Write the resulting json to OUTFILE instead of result.json
[-limit <COUNT>]    While pretty printing the json to the output file limit the utterances count per intent
[-dump]             Dump out all intents to its own txt file
[-prettyprint]      Generate a well formatted json for easier debugging
-invocation <name>  Define the invocation name for Alexa
<FILE>              The grammar or kvs file to parse"""
    }
}