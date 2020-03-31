package org.rewedigital.konversation

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class KonversationApiTest {
    private lateinit var testProjectDir: TemporaryFolder
    private val outDir get() = testProjectDir.root

    @Before
    fun setup() {
        testProjectDir = TemporaryFolder()
        testProjectDir.create()
    }

    @After
    fun cleanup() {
        testProjectDir.delete()
    }

    @Test
    fun `Test processing of files`() {
        val sut = KonversationApi()
        sut.inputFiles += listOf(
            "${CliTest.resPath}/konversation/help.kvs",
            //"${CliTest.resPath}/konversation-alexa/help.kvs",
            //"${CliTest.resPath}/konversation-alexa-de/help.kvs",
            //"${CliTest.resPath}/konversation-en/help.kvs",
            "${CliTest.resPath}/konversation/colors.values")
            .map(::File)
        sut.invocationName = "test"
        val minifiedFile = File(outDir, "schema-minified.json")
        sut.exportAlexaSchema(minifiedFile)
        val prettyFile = File(outDir, "schema-pretty.json")
        sut.exportAlexaSchema(prettyFile, true)
        val outputs = dumpDir(outDir)

        assertEquals("Got unexpected count of output files", 2, outputs.size)
        assertTrue("Missing output file", outputs.contains(minifiedFile))
        assertTrue("Missing output file", outputs.contains(prettyFile))

        val expectedMinified = File("${CliTest.resPath}/help-expected-alexa-result-minified.json").readUnixText()
        val actualMinified = minifiedFile.readUnixText()
        val expectedPretty = File("${CliTest.resPath}/help-expected-alexa-result.json").readUnixText()
        val actualPretty = prettyFile.readUnixText()

        assertEquals("Minified output not as expected", actualMinified, expectedMinified)
        assertEquals("Pretty output not as expected", actualPretty, expectedPretty)
    }

    private fun File.readUnixText(): String = readText().replace("\r", "")

    private fun dumpDir(root: File): List<File> {
        val files = mutableListOf<File>()
        root.listFiles { dir, name ->
            val entry = File(dir, name)
            when {
                name == ".gradle" -> Unit
                entry.isDirectory -> files += dumpDir(entry)
                else -> files += entry
            }
            true
        }
        return files
    }

    @Test
    fun `Verify kson export`() {
        val sut = KonversationApi()
        sut.invocationName = "test"
        sut.inputFiles += File("${CliTest.resPath}/help.kvs")
        sut.exportKson(outDir)
        assertEquals("Found unxexpected output files", listOf(File("$outDir/test/Help.kson").absoluteFile), dumpDir(outDir))
    }

    @Test
    fun latinize() {
        val input = "Ţȟĭś ÏŠ ä Ⱦèßẗ"
        val expected = "This IS a Test"
        assertEquals("$input should be latinized to $expected", expected, input.latinize())
    }

    @Test
    fun camelCaseIt() {
        val input = "Yet another TEST"
        val expected = "YetAnotherTest"
        assertEquals("$input should be latinized to $expected", expected, input.camelCaseIt())
    }
}