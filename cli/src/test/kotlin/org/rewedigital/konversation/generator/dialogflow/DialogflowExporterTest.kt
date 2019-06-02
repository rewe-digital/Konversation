package org.rewedigital.konversation.generator.dialogflow

import org.junit.Test
import org.rewedigital.konversation.Entities
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
        assertEquals(expectedExtendedIntentMinified, output.files["intents/Foo.json"].replaceTimestamp())
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
        assertEquals(expectedExtendedIntentMinified, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersays.minified(), output.files["intents/Foo_usersays_de.json"])
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
        assertEquals(expectedIntent.minified(), output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersays.minified(), output.files["intents/Foo_usersays_de.json"])
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
        assertEquals(expectedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersays, output.files["intents/Foo_usersays_de.json"])
    }

    @Test
    fun `Verify export of entities`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("{{a:TypeA}|{b:TypeB}} {{c:TypeC}}", "{a} {b} {c}")),
            prompt = mutableListOf(
                PartImpl(mutableListOf("hi"), PartType.Text)
            ),
            suggestions = mutableListOf("Foo", "Bar"))
        val typeA = Entities("TypeA", listOf(org.rewedigital.konversation.Entity("master1", null, emptyList())))
        val typeB = Entities("TypeB", listOf(org.rewedigital.konversation.Entity("master2", "key", listOf("foobar"))))
        val typeC = Entities("TypeC", listOf(org.rewedigital.konversation.Entity("master3", null, listOf("foo", "bar")), org.rewedigital.konversation.Entity("master4", null, emptyList())))
        val typeD = Entities("TypeD", listOf())
        DialogflowExporter("test").prettyPrinted(output, listOf(intent), listOf(typeA, typeB, typeC, typeD))
        assertEquals(9, output.files.size, "Expect expect 9 files")
        assertEquals("{\n  \"version\": \"1.0.0\"\n}", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedIntentWithEntities, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersaysWithEntities, output.files["intents/Foo_usersays_de.json"])
        assertEquals(expectedTypeA, output.files["entities/TypeA.json"])
        assertEquals(expectedTypeAEntries, output.files["entities/TypeA_entries_de.json"])
        assertEquals(expectedTypeB, output.files["entities/TypeB.json"])
        assertEquals(expectedTypeBEntries, output.files["entities/TypeB_entries_de.json"])
        assertEquals(expectedTypeC, output.files["entities/TypeC.json"])
        assertEquals(expectedTypeCEntries, output.files["entities/TypeC_entries_de.json"])
    }

    @Test
    fun `Verify minified export of entities`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("{{a:TypeA}|{b:TypeB}} {{c:TypeC}}", "{a} {b} {c}")),
            prompt = mutableListOf(
                PartImpl(mutableListOf("hi"), PartType.Text)
            ),
            suggestions = mutableListOf("Foo", "Bar"))
        val typeA = Entities("TypeA", listOf(org.rewedigital.konversation.Entity("master1", null, emptyList())))
        val typeB = Entities("TypeB", listOf(org.rewedigital.konversation.Entity("master2", "key", listOf("foobar"))))
        val typeC = Entities("TypeC", listOf(org.rewedigital.konversation.Entity("master3", null, listOf("foo", "bar")), org.rewedigital.konversation.Entity("master4", null, emptyList())))
        val typeD = Entities("TypeD", listOf())
        DialogflowExporter("test").minified(output, listOf(intent), listOf(typeA, typeB, typeC, typeD))
        assertEquals(9, output.files.size, "Expect expect 9 files")
        assertEquals("""{"version":"1.0.0"}""", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedIntentWithEntities.minified(), output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersaysWithEntities.minified(), output.files["intents/Foo_usersays_de.json"]) // FIXME
        assertEquals(expectedTypeA.minified(), output.files["entities/TypeA.json"])
        assertEquals(expectedTypeAEntries.minified(), output.files["entities/TypeA_entries_de.json"])
        assertEquals(expectedTypeB.minified(), output.files["entities/TypeB.json"])
        assertEquals(expectedTypeBEntries.minified(), output.files["entities/TypeB_entries_de.json"])
        assertEquals(expectedTypeC.minified(), output.files["entities/TypeC.json"])
        assertEquals(expectedTypeCEntries.minified(), output.files["entities/TypeC_entries_de.json"]) // FIXME
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
        private val expectedUsersays = """
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
        private const val expectedExtendedIntentMinified =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":[]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":4711,"fallbackIntent":false,"events":[]}"""
        private val expectedIntent = """
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
        private val expectedIntentWithEntities = """
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
                  "parameters": [
                    {
                      "id": "9b59bc08-202e-319b-8f2a-61638dfaf1ce",
                      "dataType": "@TypeB",
                      "name": "b",
                      "value": "${'$'}b",
                      "isList": false
                    },
                    {
                      "id": "20ae3e4a-f01b-318e-9d4e-3d5bf69ce0f7",
                      "dataType": "@TypeC",
                      "name": "c",
                      "value": "${'$'}c",
                      "isList": false
                    },
                    {
                      "id": "820def1c-8a30-3de8-b9ed-9df3ba5c88f9",
                      "dataType": "@TypeA",
                      "name": "a",
                      "value": "${'$'}a",
                      "isList": false
                    }
                  ],
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
            }
        """.trimIndent()
        private val expectedUsersaysWithEntities = """
            [
              {
                "id": "76708b37-55f9-31c9-83d3-aeb5ade29341",
                "data": [
                  {
                    "text": "TypeA",
                    "alias": "a",
                    "meta": "@TypeA",
                    "userDefined": false
                  },
                  {
                    "text": " ",
                    "userDefined": false
                  },
                  {
                    "text": "foo",
                    "alias": "c",
                    "meta": "@TypeC",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              },
              {
                "id": "7a85140f-d4a2-30fc-9dea-9f26650f39c2",
                "data": [
                  {
                    "text": "foobar",
                    "alias": "b",
                    "meta": "@TypeB",
                    "userDefined": false
                  },
                  {
                    "text": " ",
                    "userDefined": false
                  },
                  {
                    "text": "foo",
                    "alias": "c",
                    "meta": "@TypeC",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              }
            ]""".trimIndent()
        private val expectedTypeA = """
            {
              "id": "4c3e9e75-856f-3b24-83cb-b624751fbd6d",
              "name": "TypeA",
              "isOverridable": false,
              "isEnum": false,
              "automatedExpansion": false
            }""".trimIndent()
        private val expectedTypeAEntries = """
            [
              {
                "value": "master1",
                "synonyms": [
                  "master1"
                ]
              }
            ]""".trimIndent()
        private val expectedTypeB = """
            {
              "id": "6701be2b-c608-3df3-802a-ad3215b90851",
              "name": "TypeB",
              "isOverridable": false,
              "isEnum": false,
              "automatedExpansion": false
            }""".trimIndent()
        private val expectedTypeBEntries = """
            [
              {
                "value": "master2",
                "synonyms": [
                  "master2",
                  "foobar"
                ]
              }
            ]""".trimIndent()
        private val expectedTypeC = """
            {
              "id": "dd40a595-c890-3dce-a5e9-c065c544af05",
              "name": "TypeC",
              "isOverridable": false,
              "isEnum": false,
              "automatedExpansion": false
            }""".trimIndent()
        private val expectedTypeCEntries = """
            [
              {
                "value": "master3",
                "synonyms": [
                  "master3",
                  "foo",
                  "bar"
                ]
              },
              {
                "value": "master4",
                "synonyms": [
                  "master4"
                ]
              }
            ]""".trimIndent()

        private fun String?.replaceAttributeValue(attribute: String, value: String) = this?.replace("\"$attribute\":( )?\\d+".toRegex(), "\"$attribute\":$1$value")
        private fun String?.replaceTimestamp() = this.replaceAttributeValue("lastUpdate", "4711")
        private fun String.minified() = replace("\": ", "\":").replace("\n *".toRegex(), "")
    }
}