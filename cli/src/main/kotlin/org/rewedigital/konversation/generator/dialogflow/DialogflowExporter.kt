package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.Cli
import org.rewedigital.konversation.Entities
import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.StreamExporter
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DialogflowExporter(private val invocationName: String) : StreamExporter {
    val lang = "de"

    override fun prettyPrinted(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        val json = StringBuilder()
        intents
            .flatMap {
                it.utterances.flatMap { utterance ->
                    utterance.slotTypes
                }
            }.toHashSet()
            .mapNotNull {
                if (it.substringAfter(':').startsWith("AMAZON.")) {
                    Cli.L.warn("No definition for slot type \"$it\" found")
                }
                val type = it.substringAfter(':')
                entities?.firstOrNull { entity -> entity.name == type }
            }.forEach { slotType ->
                json.clear()
                val meta = EntityMetaData(automatedExpansion = false,
                    id = UUID.nameUUIDFromBytes("$invocationName:$slotType".toByteArray()),
                    isEnum = false,
                    isOverridable = false,
                    name = slotType.name)

                meta.prettyPrinted { s -> json.append(s) }
                zipStream.add("entities/${slotType.name}.json", json)
                println("entities/${slotType.name}.json:\n$json")
                json.clear()
                val entries = slotType.values.map { entry ->
                    Entity(value = entry.master, synonyms = entry.synonyms)
                }
                println("\nentities/${slotType.name}_entries_<LANG>.json:")
                json.append("[\n")
                entries.forEachBreakable {
                    it.prettyPrinted { s -> json.append(s) }
                    if (hasNext()) json.append(",\n")
                }
                json.append("\n]")
                zipStream.add("entities/${slotType.name}_entries_$lang.json", json)
                println(json)
            }
        intents.forEachIndexed { i, intent ->
            json.clear()
            val intentData = DialogflowIntent(
                id = UUID.nameUUIDFromBytes(intent.name.toByteArray()),
                lastUpdate = System.currentTimeMillis() / 1000,
                name = intent.name,
                responses = createResponses(intent))
            intentData.prettyPrinted { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
            json.clear()
            println("intents/${intent.name}_usersays_<LANG>.json:")
            json.append("[\n")
            //println("Dumping ${intent.name} (${intent.utterances.sumByLong { it.permutationCount }} utterances):")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to parts.last()
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val data = if (sentence.contains("{") && sentence.contains("}")) {
                        sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                            val type = slots[part]
                            type?.let {
                                val values = entities?.firstOrNull { it.name == type }?.values?.flatMap { it.synonyms }
                                val sample = values?.get(i % values.size) ?: "foo"

                                DialogflowUtterance.UtterancePart(text = sample, alias = part, meta = "@$type", userDefined = false)
                            } ?: DialogflowUtterance.UtterancePart(text = part, userDefined = false)

                        }
                    } else {
                        listOf(DialogflowUtterance.UtterancePart(text = sentence, userDefined = false))
                    }
                    DialogflowUtterance(
                        count = 0,
                        data = data,
                        id = UUID.nameUUIDFromBytes("${intent.name}:$sentence".toByteArray()),
                        isTemplate = false,
                        updated = System.currentTimeMillis() / 1000)
                        .prettyPrinted { s -> json.append(s) }
                    if (hasNext() || hasMoreUtterances) json.append(",\n")
                }
            }
            json.append("\n]")
            zipStream.add("intents/${intent.name}_usersays_$lang.json", json)
            println(json)
        }
        zipStream.add("package.json", java.lang.StringBuilder("{\n  \"version\": \"1.0.0\"\n}"))
        zipStream.close()
    }

    override fun minified(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        val json = StringBuilder()
        intents
            .flatMap {
                it.utterances.flatMap { utterance ->
                    utterance.slotTypes
                }
            }.toHashSet()
            .mapNotNull {
                if (it.startsWith("AMAZON.")) {
                    Cli.L.warn("No definition for slot type \"$it\" found")
                }
                val type = it.split(':').last()
                entities?.firstOrNull { entity -> entity.name == type }
            }.forEach { slotType ->
                json.clear()
                val meta = EntityMetaData(automatedExpansion = false,
                    id = UUID.nameUUIDFromBytes("$invocationName:$slotType".toByteArray()),
                    isEnum = false,
                    isOverridable = false,
                    name = slotType.name)
                meta.minified { s -> json.append(s) }
                zipStream.add("entities/${slotType.name}.json", json)
                json.clear()
                val entries = slotType.values.map { entry ->
                    Entity(value = entry.master, synonyms = entry.synonyms)
                }
                json.append("[")
                entries.forEachBreakable {
                    it.minified { s -> json.append(s) }
                    if (hasNext()) json.append(",\n")
                }
                json.append("]")
                zipStream.add("entities/${slotType.name}_entries_$lang.json", json)
                println(json)
            }
        intents.forEachIndexed { i, intent ->
            json.clear()
            json.append("[")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to parts.last()
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val data = if (sentence.contains("{") && sentence.contains("}")) {
                        sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                            val type = slots[part]
                            type?.let {
                                val values = entities?.firstOrNull { it.name == type }?.values?.flatMap { it.synonyms }
                                val sample = values?.get(i % values.size) ?: "foo"

                                DialogflowUtterance.UtterancePart(text = sample, alias = part, meta = "@$type", userDefined = false)
                            } ?: DialogflowUtterance.UtterancePart(text = part, userDefined = false)

                        }
                    } else {
                        listOf(DialogflowUtterance.UtterancePart(text = sentence, userDefined = false))
                    }
                    DialogflowUtterance(
                        count = 0,
                        data = data,
                        id = UUID.nameUUIDFromBytes("${intent.name}:$sentence".toByteArray()),
                        isTemplate = false,
                        updated = System.currentTimeMillis() / 1000)
                        .minified { s -> json.append(s) }
                    if (hasNext() || hasMoreUtterances) json.append(",")
                }
            }
            json.append("]")
            zipStream.add("intents/${intent.name}_usersays_$lang.json", json)
            println(json)
            json.clear()
            val intentData = DialogflowIntent(id = UUID.nameUUIDFromBytes(intent.name.toByteArray()),
                lastUpdate = System.currentTimeMillis() / 1000,
                name = intent.name,
                responses = createResponses(intent))
            intentData.minified { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
        }
        zipStream.close()
    }

    fun createResponses(intent: Intent) =
        if (intent.prompt.sumBy { it.variants.size } == 0) {
            listOf(Response(action = intent.name,
                messages = listOf(Message(
                    lang = lang, // FIXME the structure of the exporter does not allow that we know other translations.
                    speech = emptyList(),
                    type = 0))))
        } else {
            listOf(Response(action = intent.name,
                // TODO generate all variants
                messages = listOf(Message(
                    lang = lang, // FIXME the structure of the exporter does not allow that we know other translations.
                    speech = listOf(intent.prompt.joinToString(separator = " ") { it.variants.first() }),
                    type = 0))
            ))
        }

    fun ZipOutputStream.add(fileName: String, content: StringBuilder) {
        val file = ZipEntry(fileName)
        putNextEntry(file)
        write(content.toString().toByteArray())
        closeEntry()
    }
}