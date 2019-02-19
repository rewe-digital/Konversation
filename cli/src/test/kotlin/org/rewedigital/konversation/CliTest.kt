package org.rewedigital.konversation

import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CliTest {
    @Before
    fun fixPath() {
        val dir = File("")
        if (dir.absolutePath.endsWith("cli")) {
            System.setProperty("user.dir", dir.absoluteFile.parentFile.absolutePath)
        }
    }

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
        val expectedOutputPath = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val expectedOutputFile = File(expectedOutputPath).absoluteFile
        val outputFile = File(testOutputFile).absoluteFile
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "--export-alexa", testOutputFile, "-invocation", "test")
            assertEquals(sut.output, "Parsing of 1 file finished. Found 1 intent.\n")
            assertNull(sut.exitCode, message = "Execution should be successful")
            assertTrue(outputFile.exists(), message = "Output file should be created")
            assertTrue(expectedOutputFile.exists(), message = "The reference file must exists")
            assertEquals(outputFile.readText(), expectedOutputFile.readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Check handling of missing invocation name`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/help.grammar", "--export-alexa", "test.out")
        assertEquals("Parsing of 1 file finished. Found 1 intent.\n" +
                             "Invocation name is missing! Please specify the invocation name with the parameter -invocation <name>.\n", sut.output)
        assertEquals(-1, sut.exitCode)
    }

    @Test
    fun `Check processing of konversation files`() {
        val testOutputFile = "test.out"
        val expectedOutputPath = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val expectedOutputFile = File(expectedOutputPath).absoluteFile
        val outputFile = File(testOutputFile).absoluteFile
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.kvs", "--export-alexa", testOutputFile, "-invocation", "test")
            assertEquals(sut.output, "Parsing of 1 file finished. Found 1 intent.\n")
            assertNull(sut.exitCode, message = "Execution should be successful")
            assertTrue(outputFile.exists(), message = "Output file should be created")
            assertTrue(expectedOutputFile.exists(), message = "The reference file must exists")
            assertEquals(outputFile.readText(), expectedOutputFile.readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Test dump option`() {
        val testOutputFile = "test.out"
        val expectedOutputPath = "cli/src/test/resources/help-expected-alexa-result-minified.json"
        val expectedOutputFile = File(expectedOutputPath).absoluteFile
        val outputFile = File(testOutputFile).absoluteFile
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/help.kvs", "-dump")
            assertEquals(sut.output, "Parsing of 1 file finished. Found 1 intent.\nDumping Help...\n")
            assertNull(sut.exitCode, message = "Execution should be successful")
            assertTrue(outputFile.exists(), message = "Output file should be created")
            assertTrue(expectedOutputFile.exists(), message = "The reference file must exists")
            assertEquals(outputFile.readText(), expectedOutputFile.readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Test non existing file`() {
        val sut = CliTestHelper.getOutput("404.kvs")
        assertEquals(sut.output, "Input file \"404.kvs\" not found!\n")
        assertEquals(-1, sut.exitCode)
    }

    @Test
    fun `Test big grammar file`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/huge.grammar", "-stats", "-count")
        assertEquals(sut.output, "Parsing of 1 file finished. Found 2 intents.\n" +
                "Test has 1 utterances which have in total 1.000 permutations\n" +
                "Foo has 0 utterances which have in total 0 permutations\n" +
                "That are in total 1.000 permutations!\n" +
                "Test has now 1000 sample utterances\n" +
                "Foo has now 0 sample utterances\n" +
                "Generated in total 1000 Utterances\n")
        assertNull(sut.exitCode)
    }

    @Test
    fun `Check the debug options`() {
        val testOutputFile = "test.out"
        val expectedOutputPath = "cli/src/test/resources/huge-expected-alexa-result-limited.json"
        val expectedOutputFile = File(expectedOutputPath).absoluteFile
        val outputFile = File(testOutputFile).absoluteFile
        try {
            val sut = CliTestHelper.getOutput("cli/src/test/resources/huge.grammar", "--export-alexa", testOutputFile, "-invocation", "huge", "-prettyprint", "-limit", "20")
            assertEquals(sut.output, "Parsing of 1 file finished. Found 2 intents.\n")
            assertNull(sut.exitCode, message = "Execution should be successful")
            assertTrue(outputFile.exists(), message = "Output file should be created")
            assertTrue(expectedOutputFile.exists(), message = "The reference file must exists")
            assertEqualsIgnoringLineBreaks(expectedOutputFile.readText(), outputFile.readText())
        } finally {
            outputFile.apply {
                if (exists()) deleteOnExit()
            }
        }
    }

    @Test
    fun `Test dir processing`() {
        val sut = ParseTestCli("cli/src/test/resources/")
        assertEquals(4, sut.intentDb.size)
        assertEquals(File("cli/src/test/resources/konversation/help.kvs").absolutePath, sut.files[0])
        assertEquals(File("cli/src/test/resources/konversation-alexa/help.kvs").absolutePath, sut.files[1])
        assertEquals(File("cli/src/test/resources/konversation-alexa-de/help.kvs").absolutePath, sut.files[2])
        assertEquals(File("cli/src/test/resources/konversation-en/help.kvs").absolutePath, sut.files[3])
    }

    @Test
    fun `Konversation directory processing`() {
        val sut = CliTestHelper.getOutput("cli/src/test/resources/", "--export-kson", "build/out/kson")
        assertEquals(sut.output, "Parsing of 4 files finished. Found 1 intent.\n")
        assertNull(sut.exitCode)
        assertTrue(File("build/out/kson/konversation/help.kson").absoluteFile.isFile)
        assertTrue(File("build/out/kson/konversation-alexa/help.kson").absoluteFile.isFile)
        assertTrue(File("build/out/kson/konversation-alexa-de/help.kson").absoluteFile.isFile)
        assertTrue(File("build/out/kson/konversation-en/help.kson").absoluteFile.isFile)
    }

    @Test
    fun `Check for missing args`() {
        val sut1 = CliTestHelper.getOutput("./", "--export-alexa")
        assertEquals("Target is missing\n", sut1.output)
        assertEquals(-1, sut1.exitCode)
        val sut2 = CliTestHelper.getOutput("./", "--export-kson")
        assertEquals("Target directory is missing\n", sut2.output)
        assertEquals(-1, sut2.exitCode)
        val sut3 = CliTestHelper.getOutput("./", "-top")
        assertEquals("Count is missing!\n", sut3.output)
        assertEquals(-1, sut3.exitCode)
        val sut4 = CliTestHelper.getOutput("./", "-top", "x")
        assertEquals("\"x\" is no valid count of utterances.\n", sut4.output)
        assertEquals(-1, sut4.exitCode)
    }

    @Test
    fun `Use multiple input files`() {
        println(File("").absolutePath)
        println(System.getProperty("user.dir"))
        val sut = CliTestHelper.getOutput("cli/src/test/konversation/help.kvs",
                                          "cli/src/test/resources/foo/ExampleIntent.kvs",
                                          "--export-alexa",
                                          "cli/build/out/multiple-input.json",
                                          "-invocation",
                                          "multi",
                                          "-prettyprint")
        assertEquals("Parsing of 2 files finished. Found 2 intents.\n", sut.output)
        assertNull(sut.exitCode)
    }

    private class CliTestHelper : Cli() {
        private var exitCode: Int? = null

        override fun exit(status: Int) {
            exitCode = status
            throw ExitException()
        }

        private class ExitException : RuntimeException()

        companion object {
            fun getOutput(vararg args: String): TestResult {
                val outputStream = ByteArrayOutputStream(4096)
                val out = System.out
                val err = System.err
                val log = PrintStream(outputStream)
                System.setOut(log)
                System.setErr(log)
                val helper = CliTestHelper()
                try {
                    helper.parseArgs(args.toList().toTypedArray())
                } catch (e: ExitException) {
                }
                System.out.flush()
                System.err.flush()
                System.setOut(out)
                System.setErr(err)
                return TestResult(outputStream.toString().replace("\r", ""), helper.exitCode)
            }
        }
    }

    data class TestResult(val output: String, val exitCode: Int?)

    private class ParseTestCli(path: String) : Cli() {
        val files = mutableListOf<String>()

        init {
            parseArgs(arrayOf(path, "-dump"))
        }

        override fun parseFile(file: File): List<Intent> {
            files.add(file.path)
            return emptyList()
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