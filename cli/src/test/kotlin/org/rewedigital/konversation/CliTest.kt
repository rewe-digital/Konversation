package org.rewedigital.konversation

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test
import org.rewedigital.konversation.config.AlexaProject
import org.rewedigital.konversation.config.DialogflowProject
import org.rewedigital.konversation.config.KonversationConfig
import org.rewedigital.konversation.config.KonversationProject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.text.DecimalFormat
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CliTest {

    @Test
    fun `No args will show help`() {
        val sut = CliTestHelper().getOutput()
        assertNoException(sut)
        assertEquals("Missing arguments! Please specify at least the kvs or grammar file you want to process.\n\n$helpOutput\n\n", sut.output)
    }

    @Test
    fun `Help is working`() {
        val sut = CliTestHelper().getOutput("-help")
        assertNoException(sut)
        assertEquals("$helpOutput\n\n", sut.output)
    }

    @Test
    fun `Unknown args is working`() {
        val sut = CliTestHelper().getOutput("foobar")
        assertEquals("Unknown argument \"foobar\".", sut.caughtException?.message, "Unexpected error message")
    }

    @Test
    fun `Check invocation of exportAlexa`() {
        val sut = CliTestHelper().getOutput("$resPath/help.grammar", "--export-alexa", "-p", "alexaOnly")
        assertNoException(sut)
        assertEquals(listOf(File("$resPath/help.grammar").absoluteFile), sut.api.inputFiles, "Expected input file was missing")
        verify { sut.api setProperty "invocationName" value "alexa" }
        verify { sut.api invoke "exportAlexaSchema" withArguments listOf(File("alexaOnly.json").absoluteFile, false) }
    }

    @Test
    fun `Check invocation of exportDialogflow`() {
        val sut = CliTestHelper().getOutput("$resPath/help.grammar", "--export-dialogflow", "-p", "dialogflowOnly")
        assertNoException(sut)
        assertEquals(listOf(File("$resPath/help.grammar").absoluteFile), sut.api.inputFiles, "Expected input file was missing")
        verify { sut.api setProperty "invocationName" value "dialogflow" }
        verify { sut.api invoke "exportDialogflow" withArguments listOf(File("dialogflowOnly.zip").absoluteFile, false) }
    }

    @Test
    fun `Check invocation of updateAlexa`() {
        val sut = CliTestHelper().getOutput("$resPath/help.grammar", "--update-alexa", "-p", "test")
        assertEquals(listOf(File("$resPath/help.grammar").absoluteFile), sut.api.inputFiles, "Expected input file was missing")
        verify { sut.api.invocationName = "test" }
        verify { sut.api.updateAlexaSchema("skill-id") }
    }

    @Test
    fun `Check invocation of updateDialogflow`() {
        val sut = CliTestHelper().getOutput("$resPath/help.grammar", "--update-dialogflow", "-p", "test")
        assertNoException(sut)
        verify { sut.api.invocationName = "test" }
        assertTrue(sut.api.inputFiles.contains(File("$resPath/help.grammar").absoluteFile))
        verify { sut.api.updateDialogflowProject("project-id") }
    }

    @Test
    fun `Check handling of missing profile`() {
        val sut = CliTestHelper {}.getOutput("$resPath/help.grammar", "--export-alexa")
        assertNotNull(sut.caughtException, "Exception expected")
        assertEquals("No project defined", sut.caughtException.message)
    }

    @Test
    fun `Check default project handling`() {
        val sut = CliTestHelper {
            projects["SomeDefault"] = KonversationProject("foo", "bar", "de" to "Foo Bar")
        }.getOutput("$resPath/help.grammar", "--export-alexa")
        assertNoException(sut)
        verify { sut.api setProperty "invocationName" value "Foo Bar" }
        assertTrue(sut.output.isNotBlank())
    }

    @Test
    fun `Test dump option`() {
        val sut = CliTestHelper().getOutput("$resPath/help.kvs", "-dump")
        assertNoException(sut)
        verify { sut.api.exportPlain(File(".").absoluteFile.parentFile) }
    }

    @Test
    fun `Check project dumping`() {
        val sut = CliTestHelper().getOutput("--show-projects")
        assertNoException(sut)
        assertEquals(expectedProjects, sut.output, "Expect error message")
    }

    @Test
    fun `Use multiple input files`() {
        val sut = CliTestHelper().getOutput("$kvsPath/help.kvs",
            "$resPath/foo/ExampleIntent.kvs",
            "-p", "test",
            "--export-alexa",
            "-prettyprint")
        assertNoException(sut)
        assertTrue(sut.api.inputFiles.contains(File("$kvsPath/help.kvs").absoluteFile), "Expected input file was missing")
        assertTrue(sut.api.inputFiles.contains(File("$resPath/foo/ExampleIntent.kvs").absoluteFile), "Expected input file was missing")
        verify { sut.api setProperty "invocationName" value "test" }
        verify { sut.api invoke "exportAlexaSchema" withArguments listOf(File("test.json").absoluteFile, true) }
    }

    private fun assertNoException(sut: TestResult) {
        if (sut.caughtException != null) {
            throw AssertionError("Execution should be successful", sut.caughtException)
        }
    }

    private class CliTestHelper(
        val settings: KonversationConfig,
        public val api: KonversationApi) : Cli(settings, api) {
        constructor() : this(mockConfig(), mockApi())
        constructor(apply: KonversationConfig.() -> Unit) : this(KonversationConfig().apply(apply), mockApi())

        fun getOutput(vararg args: String): TestResult {
            var caughtException: IllegalArgumentException? = null
            val outputStream = ByteArrayOutputStream(4096)
            val out = System.out
            val err = System.err
            val log = PrintStream(outputStream)
            System.setOut(log)
            System.setErr(log)
            val cli = spyk(this, recordPrivateCalls = true)
            try {
                cli.parseArgs(args.toList().toTypedArray())
            } catch (e: IllegalArgumentException) {
                L.error(e.message.orEmpty())
                caughtException = e
            }
            System.out.flush()
            System.err.flush()
            System.setOut(out)
            System.setErr(err)
            return TestResult(outputStream.toString().replace("\r", ""), api, caughtException, settings, cli)
        }

        companion object {
            private fun mockApi(): KonversationApi {
                val mock = mockk<KonversationApi>(relaxed = true)
                val inputFiles = spyk(mutableListOf<File>())
                every { mock.inputFiles } returns inputFiles
                every { mock.intentDb } returns spyk(mutableMapOf())
                return mock
            }

            private fun mockConfig() = KonversationConfig().apply {
                projects["test"] = KonversationProject(
                    alexa = AlexaProject("skill-id"),
                    dialogflow = DialogflowProject("project-id"),
                    invocations = mutableMapOf("de" to "test")
                )
                projects["alexaOnly"] = KonversationProject(
                    alexa = AlexaProject("skill-id"),
                    invocations = mutableMapOf("de" to "alexa")
                )
                projects["dialogflowOnly"] = KonversationProject(
                    dialogflow = DialogflowProject("project-id"),
                    invocations = mutableMapOf("de" to "dialogflow")
                )
                projects["configError"] = KonversationProject(
                    invocations = mutableMapOf()
                )
            }
        }
    }

    data class TestResult(
        val output: String,
        val api: KonversationApi,
        val caughtException: IllegalArgumentException? = null,
        val settings: KonversationConfig,
        val cli: Cli)

    companion object {
        private val format = DecimalFormat.getInstance() as DecimalFormat
        val separator = format.decimalFormatSymbols.groupingSeparator
        val rootPath = if (File("").absolutePath.endsWith("cli")) "" else "cli/"
        val resPath = "${rootPath}src/test/resources"
        val kvsPath = "${rootPath}src/test/konversation"
        const val helpOutput = """Arguments for konversation:
[-help]                   Print this help
[-version]                Print the version of this build
[-count]                  Count the permutations and print this to the console
[-stats]                  Print out some statistics while generation
[--export-alexa]          Write the resulting json to OUTFILE instead of result.json
[--export-dialogflow]     Write the dialogflow zip file to the OUTDIR
[--export-kson]           Compiles the kvs file to kson resource files which are required for the runtime
[--update-alexa]          tba
[--update-dialogflow]     tba
[-p|--project] <project>  tba
[--create-project]        tba
[--show-projects]         tba
[--show-alexa-token]      tba
[--show-dialogflow-token] tba
[-dump]                   Dump out all intents to its own txt file
[-prettyprint]            Generate a well formatted json for easier debugging
<FILE>                    The grammar, kvs or values files to parse"""
        private const val expectedProjects = """test
  Alexa:
    Invocation (de): test
    SkillID: skill-id
  Dialogflow:
    Invocation (de): test
    ProjectID: project-id
alexaOnly
  Alexa:
    Invocation (de): alexa
    SkillID: skill-id
  Dialogflow:
    Invocation: (no invocation set)
    ProjectID: null
dialogflowOnly
  Alexa:
    Invocation: (no invocation set)
    SkillID: null
  Dialogflow:
    Invocation (de): dialogflow
    ProjectID: project-id
configError
  Alexa:
    Invocation: (no invocation set)
    SkillID: null
  Dialogflow:
    Invocation: (no invocation set)
    ProjectID: null
"""
    }
}