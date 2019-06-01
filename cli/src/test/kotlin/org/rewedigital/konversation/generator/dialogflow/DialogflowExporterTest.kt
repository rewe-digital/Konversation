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
        assertEquals("{\"version\":\"1.0.0\"}", output.files.entries.first().value, "package.json has an unexpected content")
    }

    @Test
    fun `Verify intent without utterances`() {
        val output = ZipHelper()
        DialogflowExporter("test").minified(output, listOf(Intent("Foo")), null)
        assertEquals(3, output.files.size, "Expect expect 3 files")
        assertEquals("{\"version\":\"1.0.0\"}", output.files["package.json"], "package.json has an unexpected content")
        val intentJson =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":[]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":0,"fallbackIntent":false,"events":[]}"""
        assertEquals(output.files["intents/Foo.json"]?.replace("\"lastUpdate\":\\d+".toRegex(), "\"lastUpdate\":0"), intentJson)
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
        assertEquals("{\"version\":\"1.0.0\"}", output.files["package.json"], "package.json has an unexpected content")
        val intentJson =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":[]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":0,"fallbackIntent":false,"events":[]}"""
        assertEquals(output.files["intents/Foo.json"]?.replace("\"lastUpdate\":\\d+".toRegex(), "\"lastUpdate\":0"), intentJson)
        val usersays =
            """[{"id":"398f06a5-2d31-31bd-87fc-f125c035d979","data":[{"text":"aaa","userDefined":false}],"isTemplate":false,"count":0},{"id":"aac098f8-9e9f-311f-ba5d-eafb90377e43","data":[{"text":"bbb","userDefined":false}],"isTemplate":false,"count":0}]"""
        assertEquals(output.files["intents/Foo_usersays_de.json"]?.replace("\"updated\":\\d+".toRegex(), "\"updated\":0"), usersays)
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
        assertEquals("{\"version\":\"1.0.0\"}", output.files["package.json"], "package.json has an unexpected content")
        val intentJson =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":["hi"]},{"type":2,"lang":"de","replies":["Foo","Bar"]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":0,"fallbackIntent":false,"events":[]}"""
        assertEquals(output.files["intents/Foo.json"]?.replace("\"lastUpdate\":\\d+".toRegex(), "\"lastUpdate\":0"), intentJson)
        val usersays =
            """[{"id":"398f06a5-2d31-31bd-87fc-f125c035d979","data":[{"text":"aaa","userDefined":false}],"isTemplate":false,"count":0},{"id":"aac098f8-9e9f-311f-ba5d-eafb90377e43","data":[{"text":"bbb","userDefined":false}],"isTemplate":false,"count":0}]"""
        assertEquals(output.files["intents/Foo_usersays_de.json"]?.replace("\"updated\":\\d+".toRegex(), "\"updated\":0"), usersays)
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
        val intentJson =
            """{"id":"1356c67d-7ad1-338d-816b-fb822dd2c25d","name":"Foo","auto":true,"contexts":[],"responses":[{"resetContexts":false,"action":"Foo","affectedContexts":[],"parameters":[],"messages":[{"type":0,"lang":"de","speech":["hi"]},{"type":2,"lang":"de","replies":["Foo","Bar"]}],"defaultResponsePlatforms":{},"speech":[]}],"priority":500000,"webhookUsed":true,"webhookForSlotFilling":false,"lastUpdate":0,"fallbackIntent":false,"events":[]}"""
        assertEquals(output.files["intents/Foo.json"]?.replace("\": ", "\":")?.replace("\n *".toRegex(), "")?.replace("\"lastUpdate\":\\d+".toRegex(), "\"lastUpdate\":0"), intentJson)
        val usersays =
            """[{"id":"398f06a5-2d31-31bd-87fc-f125c035d979","data":[{"text":"aaa","userDefined":false}],"isTemplate":false,"count":0},{"id":"aac098f8-9e9f-311f-ba5d-eafb90377e43","data":[{"text":"bbb","userDefined":false}],"isTemplate":false,"count":0}]"""
        assertEquals(output.files["intents/Foo_usersays_de.json"]?.replace("\": ", "\":")?.replace("\n *".toRegex(), "")?.replace("\"updated\":\\d+".toRegex(), "\"updated\":0"), usersays)
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
}