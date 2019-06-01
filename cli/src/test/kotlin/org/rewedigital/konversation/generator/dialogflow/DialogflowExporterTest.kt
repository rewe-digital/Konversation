package org.rewedigital.konversation.generator.dialogflow

import org.junit.Test
import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.PartImpl
import org.rewedigital.konversation.PartType
import org.rewedigital.konversation.parser.Utterance
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import kotlin.test.assertEquals

class DialogflowExporterTest {
    @Test
    fun `Verify empty project`() {
        val output = ZipHelper()
        DialogflowExporter("test").minified(output, emptyList(), null)
        assertEquals(1, output.files.size, "Expect only one file")
        assertEquals("package.json", output.files.entries.first().key, "Should contain a package.json")
        assertEquals("""{"version":"1.0.0"}""", output.files.entries.first().value, "package.json has an unexpected content")
    }

    @Test
    fun `Verify intent without utterances`() {
        val output = ZipHelper()
        DialogflowExporter("test").minified(output, listOf(Intent("Foo")), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("""{"version":"1.0.0"}""", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedExtendedMinifiedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(output.files["intents/Foo_usersays_de.json"], "[]")
    }

    @Test
    fun `Verify intent with utterances`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("aaa", "aaa"),
                Utterance("bbb", "bbb")))
        DialogflowExporter("test").minified(output, listOf(intent), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("""{"version":"1.0.0"}""", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedExtendedMinifiedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedMinifiedUsersays, output.files["intents/Foo_usersays_de.json"])
    }

    @Test
    fun `Verify full example`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("aaa", "aaa"),
                Utterance("bbb", "bbb")),
            prompt = mutableListOf(
                PartImpl(mutableListOf("hi"), PartType.Text)
            ),
            suggestions = mutableListOf("Foo", "Bar"))
        DialogflowExporter("test").minified(output, listOf(intent), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("""{"version":"1.0.0"}""", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedMinifiedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedMinifiedUsersays, output.files["intents/Foo_usersays_de.json"])
    }

    @Test
    fun `Verify full example pretty printed`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("aaa", "aaa"),
                Utterance("bbb", "bbb")),
            prompt = mutableListOf(
                PartImpl(mutableListOf("hi"), PartType.Text)
            ),
            suggestions = mutableListOf("Foo", "Bar"))
        DialogflowExporter("test").prettyPrinted(output, listOf(intent), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("{\n  \"version\": \"1.0.0\"\n}", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedPrettyPrintedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedPrettyPrintedUsersays, output.files["intents/Foo_usersays_de.json"])
    }

    private class ZipHelper : ByteArrayOutputStream() {
        val files by lazy {
            val zipStream = ZipInputStream(ByteArrayInputStream(toByteArray()))
            var item = zipStream.nextEntry
            val buffer = ByteArray(1024)
            val content = StringBuilder()
            val files = mutableMapOf<String, String>()
            while (item != null) {
                do {
                    val len = zipStream.read(buffer)
                    if (len > 0) {
                        content.append(String(buffer, 0, len))
                    }
                } while (len > 0)
                files[item.name] = content.toString()
                content.clear()
                item = zipStream.nextEntry
            }
            zipStream.closeEntry()
            zipStream.closeEntry()
            files
        }
    }

    companion object {
        private val expectedPrettyPrintedUsersays = """
            [
              {
                "id": "398f06a5-2d31-31bd-87fc-f125c035d979",
                "data": [
                  {
                    "text": "aaa",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              },
              {
                "id": "aac098f8-9e9f-311f-ba5d-eafb90377e43",
                "data": [
                  {
                    "text": "bbb",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              }
            ]""".trimIndent()
        private val expectedMinifiedUsersays = expectedPrettyPrintedUsersays.replace("\": ", "\":").replace("\n *".toRegex(), "")
        private const val expectedExtendedMinifiedIntent =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":[]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":4711,"fallbackIntent":false,"events":[]}"""
        private val expectedPrettyPrintedIntent = """
            {
              "id": "1356c67d-7ad1-338d-816b-fb822dd2c25d",
              "name": "Foo",
              "auto": true,
              "contexts": [],
              "responses": [
                {
                  "resetContexts": false,
                  "action": "Foo",
                  "affectedContexts": [],
                  "parameters": [],
                  "messages": [
                    {
                      "type": 0,
                      "lang": "de",
                      "speech": [
                        "hi"
                      ]
                    },

                    {
                      "type": 2,
                      "lang": "de",
                      "replies": [
                        "Foo",
                        "Bar"
                      ]
                    }
                  ],
                  "defaultResponsePlatforms": {},
                  "speech": []
                }
              ],
              "priority": 500000,
              "webhookUsed": true,
              "webhookForSlotFilling": false,
              "lastUpdate": 4711,
              "fallbackIntent": false,
              "events": []
            }""".trimIndent()
        private val expectedMinifiedIntent = expectedPrettyPrintedIntent.replace("\": ", "\":").replace("\n *".toRegex(), "")

        private fun String?.replaceAttributeValue(attribute: String, value: String) = this?.replace("\"$attribute\":( )?\\d+".toRegex(), "\"$attribute\":$1$value")
        private fun String?.replaceTimestamp() = this.replaceAttributeValue("lastUpdate", "4711")
    }
}