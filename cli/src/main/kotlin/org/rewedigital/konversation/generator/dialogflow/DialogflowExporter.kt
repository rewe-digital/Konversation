package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.*
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.StreamExporter
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DialogflowExporter(private val invocationName: String) : StreamExporter {
    private val lang = "de"
    private val supportedGenericTypes = arrayOf("any", "number", "ordinal", "color")

    override fun prettyPrinted(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        val json = StringBuilder()
        intents.filter { !it.name.startsWith("AMAZON.") }.forEachSlotType(entities) { slotType ->
            json.clear()
            val meta = EntityMetaData(automatedExpansion = false,
                id = UUID.nameUUIDFromBytes("$invocationName:$slotType".toByteArray()),
                isEnum = false,
                isOverridable = false,
                name = slotType.name)

            meta.prettyPrinted { s -> json.append(s) }
            zipStream.add("entities/${slotType.name}.json", json)
            //println("entities/${slotType.name}.json:\n$json")
            json.clear()
            val entries = slotType.values.map { entry ->
                Entity(value = entry.master, synonyms = entry.synonyms)
            }
            //println("\nentities/${slotType.name}_entries_<LANG>.json:")
            json.append("[\n")
            entries.forEachBreakable {
                it.prettyPrinted { s -> json.append(s) }
                if (hasNext()) json.append(",\n")
            }
            json.append("\n]")
            zipStream.add("entities/${slotType.name}_entries_$lang.json", json)
            //println(json)
        }
        intents.filter { !it.name.startsWith("AMAZON.") }.forEachIndexed { i, intent ->
            json.clear()
            val intentData = DialogflowIntent(
                id = UUID.nameUUIDFromBytes(intent.name.toByteArray()),
                lastUpdate = System.currentTimeMillis() / 1000,
                name = intent.name,
                responses = createResponses(intent))
            intentData.prettyPrinted { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
            json.clear()
            //println("intents/${intent.name}_usersays_<LANG>.json:")
            json.append("[\n")
            //println("Dumping ${intent.name} (${intent.utterances.sumByLong { it.permutationCount }} utterances):")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to useSystemTypes(parts.last())
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val data = if (sentence.contains("{") && sentence.contains("}")) {
                        sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                            val type = slots[part]
                            type?.let {
                                val values = entities?.firstOrNull { it.name == type }?.values?.flatMap { it.synonyms }
                                val sample = values?.getOrNull(i % Math.max(1, values.size)) ?: defaultValue(type, i)

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
            //println(json)
        }
        zipStream.add("package.json", java.lang.StringBuilder("{\n  \"version\": \"1.0.0\"\n}"))
        zipStream.close()
    }

    override fun minified(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        val json = StringBuilder()
        intents.filter { !it.name.startsWith("AMAZON.") }.forEachSlotType(entities) { slotType ->
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
                if (hasNext()) json.append(",")
            }
            json.append("]")
            zipStream.add("entities/${slotType.name}_entries_$lang.json", json)
            //println(json)
        }
        intents.filter { !it.name.startsWith("AMAZON.") }.forEachIndexed { i, intent ->
            json.clear()
            val intentData = DialogflowIntent(
                id = UUID.nameUUIDFromBytes(intent.name.toByteArray()),
                lastUpdate = System.currentTimeMillis() / 1000,
                name = intent.name,
                responses = createResponses(intent))
            intentData.minified { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
            json.clear()
            json.append("[")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to useSystemTypes(parts.last())
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val data = if (sentence.contains("{") && sentence.contains("}")) {
                        sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                            val type = slots[part]
                            type?.let {
                                val values = entities?.firstOrNull { it.name == type }?.values?.flatMap { it.synonyms }
                                val sample = values?.getOrNull(i % Math.max(1, values.size)) ?: defaultValue(type, i)

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
        }
        zipStream.add("package.json", java.lang.StringBuilder("{\"version\":\"1.0.0\"}"))
        zipStream.close()
    }

    private fun createResponses(intent: Intent) =
        listOf(Response(action = intent.name,
            messages = listOfNotNull(Message(
                lang = lang, // FIXME the structure of the exporter does not allow that we know other translations.
                speech = intent.prompt.generateSamples()),
                createSuggestion(intent.suggestions)),
            parameters = intent.utterances.flatMap { it.slotTypes }.toHashSet().map(::ResponseParameter)))

    private fun createSuggestion(suggestions: List<String>): NodeExporter? =
        if (suggestions.isEmpty()) null else QuickReply(lang, suggestions)

    fun ZipOutputStream.add(fileName: String, content: StringBuilder) {
        val file = ZipEntry(fileName)
        putNextEntry(file)
        write(content.toString().toByteArray())
        closeEntry()
    }

    private fun List<Intent>.forEachSlotType(entities: List<Entities>?, action: (Entities) -> Unit) = this
        .flatMap {
            it.utterances.flatMap { utterance ->
                utterance.slotTypes
            }
        }
        .toHashSet()
        .mapNotNull {
            val type = it.substringAfter(':')
            if ((!type.startsWith("@sys.") || !type.startsWith("sys.")) && !supportedGenericTypes.contains(type) && entities?.any { entity -> entity.name == type } != true) {
                Cli.L.warn("No definition for slot type \"$type\" found")
            }
            // TODO add migration hints or do that silent?
            entities?.firstOrNull { entity -> entity.name == type } //?:
        }
        .toHashSet()
        .forEach(action)

    private fun useSystemTypes(slot: String): String = when (slot) {
        "any" -> "sys.any"
        "number" -> "sys.number"
        "ordinal" -> "sys.ordinal"
        "color" -> "sys.color"
        else -> slot
    }

    private fun defaultValue(type: String, int: Int) = when (type) {
        "sys.any" -> "foo bar"
        "sys.number" -> int.toString()
        "sys.ordinal" -> "$int."
        "sys.color" -> "Blau"
        else -> type
    }

    // this method is something like a zip operation, but it equalized the list length first
    private fun Prompt.generateSamples(): List<String> {
        // syntactic sugar
        operator fun Part.component1() = variants

        // get maximal part length
        val sizes = map { it.variants.size }
        var max = 0
        sizes.forEach { max = Math.max(max, it) }

        // make all lists same size with rotating the values until all are same sized
        val equalizedLists = map { (variants) ->
            val newVariants = variants.toMutableList()
            for (i in variants.size until max) {
                newVariants += variants[i % variants.size]
            }
            newVariants
        }

        // zip the elements
        val samples = mutableListOf<String>()
        for (i in 0 until max) {
            samples += equalizedLists.joinToString(separator = " ") { it[i] }.replace(" *\n ".toRegex(), "\n")
        }
        return samples
    }
}