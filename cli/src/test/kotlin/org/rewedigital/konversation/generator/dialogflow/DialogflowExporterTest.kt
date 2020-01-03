package org.rewedigital.konversation.generator.dialogflow

import org.junit.Test
import org.rewedigital.konversation.*
import org.rewedigital.konversation.Entity
import org.rewedigital.konversation.parser.Utterance
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
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
        DialogflowExporter("test").minified(output, listOf(Intent("Foo", sourceFile = File("Foo"))), null)
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
                Utterance("bbb", "bbb")),
            sourceFile = File("Foo"))
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
            suggestions = mutableListOf("Foo", "Bar"),
            sourceFile = File("Foo"))
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
            suggestions = mutableListOf("Foo", "Bar"),
            sourceFile = File("Foo"))
        DialogflowExporter("test").prettyPrinted(output, listOf(intent), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("{\n  \"version\": \"1.0.0\"\n}", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedIntent, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersays, output.files["intents/Foo_usersays_de.json"])
    }

    @Test
    fun `Verify sample utterances for system types`() {
        val output = ZipHelper()
        val intent = Intent(name = "Foo",
            utterances = mutableListOf(
                Utterance("Ich bin {{age:number}} und kommt aus {{city:de-city}}", "Ich bin {age} und kommt aus {city}")),
            prompt = mutableListOf(
                PartImpl(mutableListOf("hi"), PartType.Text)
            ),
            suggestions = mutableListOf("Foo", "Bar"),
            sourceFile = File("Foo"))
        DialogflowExporter("test").prettyPrinted(output, listOf(intent), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("{\n  \"version\": \"1.0.0\"\n}", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedIntentWithSystemEntities, output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersaysWithSystemEntities, output.files["intents/Foo_usersays_de.json"])
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
            suggestions = mutableListOf("Foo", "Bar"),
            sourceFile = File("Foo"))
        val typeA = Entities("TypeA", listOf(Entity("master1", null, emptyList())))
        val typeB = Entities("TypeB", listOf(Entity("master2", "key", listOf("foobar"))))
        val typeC = Entities("TypeC", listOf(Entity("master3", null, listOf("foo", "bar")), Entity("master4", null, emptyList())))
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
            suggestions = mutableListOf("Foo", "Bar"),
            sourceFile = File("Foo"))
        val typeA = Entities("TypeA", listOf(Entity("master1", null, emptyList())))
        val typeB = Entities("TypeB", listOf(Entity("master2", "key", listOf("foobar"))))
        val typeC = Entities("TypeC", listOf(Entity("master3", null, listOf("foo", "bar")), Entity("master4", null, emptyList())))
        val typeD = Entities("TypeD", listOf())
        DialogflowExporter("test").minified(output, listOf(intent), listOf(typeA, typeB, typeC, typeD))
        assertEquals(9, output.files.size, "Expect expect 9 files")
        assertEquals("""{"version":"1.0.0"}""", output.files["package.json"], "package.json has an unexpected content")
        assertEquals(expectedIntentWithEntities.minified(), output.files["intents/Foo.json"].replaceTimestamp())
        assertEquals(expectedUsersaysWithEntities.minified(), output.files["intents/Foo_usersays_de.json"])
        assertEquals(expectedTypeA.minified(), output.files["entities/TypeA.json"])
        assertEquals(expectedTypeAEntries.minified(), output.files["entities/TypeA_entries_de.json"])
        assertEquals(expectedTypeB.minified(), output.files["entities/TypeB.json"])
        assertEquals(expectedTypeBEntries.minified(), output.files["entities/TypeB_entries_de.json"])
        assertEquals(expectedTypeC.minified(), output.files["entities/TypeC.json"])
        assertEquals(expectedTypeCEntries.minified(), output.files["entities/TypeC_entries_de.json"])
    }

    @Test
    fun `Verify that annotations work as expected`() {
        val minified = ZipHelper()
        val prettyPrinted = ZipHelper()
        val fallbackTestWithEvent = Intent(
            name = "FallbackTestWithEvent",
            utterances = mutableListOf(Utterance("Test", "Test")),
            annotations = mutableMapOf("Fallback" to emptyList(), "Events" to listOf("Works")),
            sourceFile = File("FallbackTestWithEvent"))
        val listElementsWithMultipleEvents = Intent(
            name = "ListElementsWithMultipleEvents",
            utterances = mutableListOf(Utterance("I like {{colors:color}}", "I like {color}")),
            annotations = mutableMapOf("ListParameters" to listOf("colors"), "Events" to listOf("Works", "Fine")),
            sourceFile = File("ListElementsWithMultipleEvents"))
        DialogflowExporter("test").apply {
            prettyPrinted(prettyPrinted, listOf(fallbackTestWithEvent, listElementsWithMultipleEvents), emptyList())
            minified(minified, listOf(fallbackTestWithEvent, listElementsWithMultipleEvents), emptyList())
        }
        assertEquals(5, prettyPrinted.files.size, "Expect expect 3 files")
        assertEquals(5, minified.files.size, "Expect expect 3 files")
        assertEquals(expectedIntentWithFallbackAndEvent, prettyPrinted.files["intents/FallbackTestWithEvent.json"].replaceTimestamp())
        assertEquals(expectedIntentWithFallbackAndEvent.minified(), minified.files["intents/FallbackTestWithEvent.json"].replaceTimestamp())
        assertEquals(expectedUsersaysFallbackAndEvent, prettyPrinted.files["intents/FallbackTestWithEvent_usersays_de.json"])
        assertEquals(expectedUsersaysFallbackAndEvent.minified(), minified.files["intents/FallbackTestWithEvent_usersays_de.json"])
        assertEquals(expectedIntentListElementsWithMultipleEvents, prettyPrinted.files["intents/ListElementsWithMultipleEvents.json"].replaceTimestamp())
        assertEquals(expectedIntentListElementsWithMultipleEvents.minified(), minified.files["intents/ListElementsWithMultipleEvents.json"].replaceTimestamp())
        assertEquals(expectedUsersaysForColors, prettyPrinted.files["intents/ListElementsWithMultipleEvents_usersays_de.json"])
        assertEquals(expectedUsersaysForColors.minified(), minified.files["intents/ListElementsWithMultipleEvents_usersays_de.json"])
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
        private val expectedUsersaysWithSystemEntities = """
            [
              {
                "id": "c65f278c-1284-36c7-b856-45417bb1f6f2",
                "data": [
                  {
                    "text": "Ich bin ",
                    "userDefined": false
                  },
                  {
                    "text": "0",
                    "alias": "age",
                    "meta": "@sys.number",
                    "userDefined": false
                  },
                  {
                    "text": " und kommt aus ",
                    "userDefined": false
                  },
                  {
                    "text": "München",
                    "alias": "city",
                    "meta": "@sys.geo-city",
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
        private val expectedIntentWithSystemEntities = """
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
                      "id": "67199ddf-4516-33ce-8e1d-3a53f3c8067c",
                      "dataType": "@sys.number",
                      "name": "age",
                      "value": "${'$'}age",
                      "isList": false
                    },
                    {
                      "id": "2b88d619-659b-3242-9d0d-4995bff44931",
                      "dataType": "@sys.geo-city",
                      "name": "city",
                      "value": "${'$'}city",
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
                    "text": "master1",
                    "alias": "a",
                    "meta": "@TypeA",
                    "userDefined": false
                  },
                  {
                    "text": " ",
                    "userDefined": false
                  },
                  {
                    "text": "master4",
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
                    "text": "master2",
                    "alias": "b",
                    "meta": "@TypeB",
                    "userDefined": false
                  },
                  {
                    "text": " ",
                    "userDefined": false
                  },
                  {
                    "text": "master4",
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
              "id": "7500bbbf-380c-3e5d-b133-645ffb674da9",
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
              "id": "43735a21-33da-3efb-9313-84e5aeb8ec39",
              "name": "TypeB",
              "isOverridable": false,
              "isEnum": false,
              "automatedExpansion": false
            }""".trimIndent()
        private val expectedTypeBEntries = """
            [
              {
                "value": "key",
                "synonyms": [
                  "master2",
                  "foobar"
                ]
              }
            ]""".trimIndent()
        private val expectedTypeC = """
            {
              "id": "b9fb2829-817d-3445-8e04-d402e4ce8014",
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
        private val expectedIntentWithFallbackAndEvent = """
            {
              "id": "1864ae49-2563-3913-b8bb-b522f5170fde",
              "name": "FallbackTestWithEvent",
              "auto": true,
              "contexts": [],
              "responses": [
                {
                  "resetContexts": false,
                  "action": "FallbackTestWithEvent",
                  "affectedContexts": [],
                  "parameters": [],
                  "messages": [
                    {
                      "type": 0,
                      "lang": "de",
                      "speech": [
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
              "fallbackIntent": true,
              "events": [
                "Works"
              ]
            }
        """.trimIndent()
        private val expectedIntentListElementsWithMultipleEvents = """
            {
              "id": "ffb418fb-53da-32cf-96bd-3e9ea6f74e5c",
              "name": "ListElementsWithMultipleEvents",
              "auto": true,
              "contexts": [],
              "responses": [
                {
                  "resetContexts": false,
                  "action": "ListElementsWithMultipleEvents",
                  "affectedContexts": [],
                  "parameters": [
                    {
                      "id": "5e959b94-caa7-3fe1-8f7d-dc5bb335e712",
                      "dataType": "@sys.color",
                      "name": "colors",
                      "value": "${'$'}colors",
                      "isList": true
                    }
                  ],
                  "messages": [
                    {
                      "type": 0,
                      "lang": "de",
                      "speech": [
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
              "events": [
                "Works",
                "Fine"
              ]
            }""".trimIndent()
        private val expectedUsersaysFallbackAndEvent = """
            [
              {
                "id": "d6c3e65a-36f0-3e45-80ff-b2080dc26d0d",
                "data": [
                  {
                    "text": "Test",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              }
            ]""".trimIndent()
        private val expectedUsersaysForColors = """
            [
              {
                "id": "7109c5dd-485c-3e9a-8a2e-5da3177c116f",
                "data": [
                  {
                    "text": "I like ",
                    "userDefined": false
                  },
                  {
                    "text": "Blau",
                    "alias": "colors",
                    "meta": "@sys.color",
                    "userDefined": false
                  }
                ],
                "isTemplate": false,
                "count": 0
              }
            ]""".trimIndent()

        private fun String?.replaceAttributeValue(attribute: String, value: String) = this?.replace("\"$attribute\":( )?\\d+".toRegex(), "\"$attribute\":$1$value")
        private fun String?.replaceTimestamp() = this.replaceAttributeValue("lastUpdate", "4711")
        private fun String.minified() = replace("\": ", "\":").replace("\n *".toRegex(), "")
    }
}