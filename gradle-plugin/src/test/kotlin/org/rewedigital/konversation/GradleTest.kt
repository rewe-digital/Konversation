package org.rewedigital.konversation

import groovy.util.GroovyTestCase.assertEquals
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.File

class GradleTest {
    private lateinit var buildFile: File
    private lateinit var testProjectDir: TemporaryFolder

    @Before
    fun setup() {
        testProjectDir = TemporaryFolder()
        testProjectDir.create()

        testProjectDir.newFile("settings.gradle").writeText("""
            buildscript {
                repositories {
                    jcenter()
                }
                dependencies {
                    classpath "org.jetbrains.dokka:dokka-gradle-plugin:0.9.17"
                    classpath 'com.novoda:bintray-release:0.9'
                    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61"
                    classpath "org.jetbrains.kotlin:kotlin-serialization:1.3.61"
                }
            }
        """.trimIndent())
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @After
    fun cleanup() {
        testProjectDir.delete()
    }

    @Test
    fun `exportKson on empty project`() {
        buildFile.writeText("""
            $defaultPlugins
            konversation {
            }
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("exportKson")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportKson")?.outcome)
    }

    @Test
    fun `exportKson on simple project`() {
        buildFile.writeText("""
            $defaultPlugins
            konversation {
                projects{
                    Example {
                        invocationNames["de"] = "Test"
                        inputFiles = ["*.kvs"]
                    }
                }
            }
        """.trimIndent())
        File(testProjectDir.root, "src/main/konversation").mkdirs()
        testProjectDir.newFile("src/main/konversation/Foo.kvs").writeText("""
            HalloIntent:
            !hallo
            - Nice to meet you!
            - Wohoo you are here!
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("exportKson")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportKson")?.outcome)

        val helloKsonFile = File(testProjectDir.root, "build/konversation/res/main/HalloIntent.kson")
        assertFileExists("Output file missing", helloKsonFile)
        val helloKsonContent = helloKsonFile.readText()
        val expectedOutput = """
            {
              "parts": [
                {
                  "type": "Text",
                  "variants": [
                    "Nice to meet you!",
                    "Wohoo you are here!"
                  ]
                }
              ],
              "suggestions": [],
              "reprompts": {}
            }
        """.trimIndent()
        assertEquals("The output file has an unexpected output", expectedOutput, helloKsonContent)
    }

    @Test
    fun `validate that second run does nothing`() {
        `exportKson on simple project`()
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("exportKson")
            .build()

        assertEquals(TaskOutcome.UP_TO_DATE, result.task(":exportKson")?.outcome)

        val helloKsonFile = File(testProjectDir.root, "build/konversation/res/main/HalloIntent.kson")
        assertFileExists("Output vanished in second run", helloKsonFile)
    }

    @Test
    fun `exportKonversationEnum should export all konversations with prompts`() {
        buildFile.writeText("""
            $defaultPlugins
            konversation {
                enumPackageName = "org.rewedigital.test"
                projects{
                    Example {
                        invocationNames["de"] = "Test"
                        inputFiles = ["*.kvs"]
                    }
                }
            }
        """.trimIndent())
        File(testProjectDir.root, "src/main/konversation").mkdirs()
        testProjectDir.newFile("src/main/konversation/Foo.kvs").writeText("""
            HalloIntent:
            !hallo
            - Nice to meet you!
            - Wohoo you are here!
            
            ByeIntent:
            !Bye
            
            Error:
            - Just a random error message
        """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("exportKonversationEnum")
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":exportKonversationEnum")?.outcome)

        val enumFile = File(testProjectDir.root, "build/konversation/gen/main/org/rewedigital/test/Konversations.kt")
        assertFileExists("Output file $enumFile missing", enumFile)
        val enumContent = enumFile.readText()
        val expectedOutput = """
            // Code generated by Konversation. Do not edit.

            package org.rewedigital.test
            
            import org.rewedigital.dialog.utils.KonversationEnum
            
            enum class Konversations(override val alias: String? = null): KonversationEnum {
                Error,
                HalloIntent
            }
        """.trimIndent()
        assertEquals("The output file has an unexpected output", expectedOutput, enumContent.replace("\r", ""))
    }

    private fun assertFileExists(description: String, file: File) {
        if (!file.exists()) {
            dumpDir(testProjectDir.root)
            fail(description)
        }
    }

    private fun dumpDir(root: File) {
        root.listFiles { dir, name ->
            val entry = File(dir, name)
            when {
                name == ".gradle" -> Unit
                entry.isDirectory -> dumpDir(entry)
                else -> println(entry.path)
            }
            true
        }
    }

    companion object {
        private const val defaultPlugins =
            """plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'kotlin'
                id 'org.rewedigital.konversation' version '2.0.0-beta3'
            }
"""
    }
}