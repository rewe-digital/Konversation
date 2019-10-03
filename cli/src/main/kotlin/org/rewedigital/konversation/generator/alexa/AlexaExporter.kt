package org.rewedigital.konversation.generator.alexa

import org.rewedigital.konversation.*
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import java.io.File
import java.util.*

class AlexaExporter(private val skillName: String, private val baseDir: File, private val limit: Int) : Exporter {
    private val supportedGenericTypes = arrayOf("any", "number", "ordinal", "color", "de-city", "at-city", "eu-city", "us-city", "gb-city")

    // TODO make sure that both branches have equal functionality
    override fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        // write prefix
        printer("{\n" +
                "  \"interactionModel\": {\n" +
                "    \"languageModel\": {\n" +
                "      \"invocationName\": \"$skillName\",\n" +
                "      \"intents\": [\n")

        // write out intents
        intents.filter { intent ->
            intent.utterances.isNotEmpty() || intent.name.startsWith("AMAZON.", ignoreCase = true)
        }.forEachIterator { intent ->
            printer("        {\n" +
                    "          \"name\": \"${intent.cleanName}\",\n" +
                    "          \"slots\": [")
            val allSlots = intent.utterances.flatMap { it.slotTypes }.toHashSet()
            if (allSlots.isEmpty()) {
                printer("],")
            }
            printer("\n")
            allSlots.forEachIterator { slot ->
                val (name, type) = if (slot.contains(':')) {
                    val parts = slot.split(':')
                    Pair(parts[0], useSystemTypes(parts[1]))
                } else {
                    Pair(slot, useSystemTypes(slot))
                }
                // write slot types
                printer("            {\n" +
                        "              \"name\": \"$name\",\n" +
                        "              \"type\": \"$type\"\n" +
                        "            }" + (if (hasNext()) "," else "") + "\n")
            }
            // write sample utterances
            if (allSlots.isNotEmpty()) {
                printer("          ],\n")
            }
            printer("          \"samples\": [")
            if (intent.utterances.isEmpty()) {
                printer("]")
            }
            printer("\n")
            var total: Int
            val hashes = TreeSet<Int>()
            var first = true
            intent.utterances.forEachBreakable { utterance ->
                total = 0
                utterance.permutations.forEachBreakable {
                    it.toLowerCase().hashCode().let { hash ->
                        if (!hashes.contains(hash)) {
                            total++
                            if (total >= limit) {
                                stop()
                            }
                            hashes += hash
                            if (first) {
                                first = false
                            } else {
                                printer(",\n")
                            }
                            printer("            \"${it.removeAndWarn("?").removeAndWarn(",").removeAndWarn(".")}\"")
                        }
                    }
                }
                if (total > limit) {
                    stop()
                }
            }
            if (intent.utterances.isNotEmpty()) {
                printer("\n          ]\n")
            }
            printer("        }" + (if (hasNext()) "," else "") + "\n")
        }
        // write the custom slot type definitions
        printer("      ],\n" +
                "      \"types\": [")

        val types = intents
            .flatMap {
                it.utterances.flatMap { utterance ->
                    utterance.slotTypes
                }
            }.map {
                // remove name of the type
                it.substringAfter(':', it)
            }.toHashSet()

        if (types.isEmpty()) {
            printer("]")
        }
        printer("\n")

        types.forEachSlotType(entities) { (slotType, entities) ->
            printer("        {\n" +
                    "          \"name\": \"$slotType\",\n" +
                    "          \"values\": [")
            if (entities == null) {
                printer("]")
            }
            printer("\n")
            entities?.values?.forEachIterator { entity ->
                printer("            {\n")
                entity.key?.let {
                    printer("              \"id\": \"${entity.key}\",\n")
                }
                printer("              \"name\": {\n" +
                        "                \"value\": \"${entity.master}\"")

                val synonyms = entity.synonyms.removeEntriesWithEmoticons()
                if (synonyms.isNotEmpty()) {
                    printer(",\n                \"synonyms\": [\n")
                    synonyms.forEachIterator { alias ->
                        printer("                  \"$alias\"" + (if (hasNext()) "," else "") + "\n")
                    }
                    printer("                ]")
                }
                printer("\n              }\n" +
                        "            }" + (if (hasNext()) "," else "") + "\n")

            }
            if (types.isNotEmpty()) {
                printer("          ]\n")
            }
            printer("        }" + (if (hasNext()) "," else "") + "\n")
        }

        // write suffix
        if (types.isNotEmpty()) {
            printer("      ]\n")
        }
        printer("    }\n" +
                "  }\n" +
                "}")
    }

    override fun minified(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        // write prefix
        printer("{" +
                "\"interactionModel\":{" +
                "\"languageModel\":{" +
                "\"invocationName\":\"$skillName\"," +
                "\"intents\":[")

        // write out intents
        intents.filter { intent ->
            intent.utterances.isNotEmpty() || intent.name.startsWith("AMAZON.", ignoreCase = true)
        }.forEachIterator { intent ->
            printer("{" +
                    "\"name\":\"${intent.cleanName}\"," +
                    "\"slots\":[")
            val allSlots = intent.utterances.flatMap { it.slotTypes }.toHashSet()
            allSlots.forEachIterator { slot ->
                val (name, type) = if (slot.contains(':')) {
                    val parts = slot.split(':')
                    Pair(parts[0], useSystemTypes(parts[1]))
                } else {
                    Pair(slot, useSystemTypes(slot))
                }
                // write slot types
                printer("{" +
                        "\"name\":\"$name\"," +
                        "\"type\":\"$type\"" +
                        "}" + (if (hasNext()) "," else ""))
            }
            // write sample utterances
            printer("]," +
                    "\"samples\":[")
            val hashes = TreeSet<Int>()
            var first = true
            intent.utterances.forEachIterator { utterance ->
                utterance.permutations.forEachIterator {
                    it.toLowerCase().hashCode().let { hash ->
                        if (!hashes.contains(hash)) {
                            if (first) {
                                first = false
                            } else {
                                printer(",")
                            }
                            printer("\"$it\"")
                            hashes += hash
                        } else {
                            println("AHA! $it")
                        }
                    }
                }
            }
            printer("]" +
                    "}" + (if (hasNext()) "," else ""))
        }
        // write the custom slot type definitions
        printer("]," +
                "\"types\":[")

        intents
            .flatMap { it.utterances.flatMap { utterance -> utterance.slotTypes } }
            .map {
                val type = it.split(':').last()
                Pair(type, File("$baseDir/$type.values"))
            }
            .filter { it.second.exists() }
            .map { Pair(it.first, it.second.readLines().filter { line -> line.isNotEmpty() }) }
            .toHashSet()
            .forEachIterator { (slotType, values) ->
                printer(
                    "{" +
                            "\"name\":\"$slotType\"," +
                            "\"values\":["
                )
                values.forEachIterator { valueLine ->
                    if (valueLine.startsWith('{')) {
                        // FIXME the pretty printed code looks different
                        val aliases = valueLine.substring(1, valueLine.length - 1).split('|')
                        val (id, value) = aliases.first().split(':', limit = 2).let {
                            when (it.size) {
                                1 -> Pair(null, it.first())
                                2 -> Pair(it.first(), it.last())
                                else -> throw IllegalArgumentException("The key must not be empty. In the line: $valueLine")
                            }
                        }
                        printer("{")
                        id?.let {
                            printer("\"id\":\"$id\",")
                        }
                        printer("\"name\":{" +
                                "\"value\":\"$value\"," +
                                "\"synonyms\":[")
                        aliases.removeEntriesWithEmoticons().toHashSet().apply {
                            remove(aliases.first()) // remove key
                        }.forEachIterator { alias ->
                            printer("\"$alias\"" + (if (hasNext()) "," else ""))
                        }
                        printer("]" +
                                "}" +
                                "}" + (if (hasNext()) "," else ""))
                    } else {
                        printer("{" +
                                "\"name\":{" +
                                "\"value\":\"$valueLine\"" +
                                "}" +
                                "}" + (if (hasNext()) "," else ""))
                    }
                }
                printer("]" +
                        "}" + (if (hasNext()) "," else ""))
            }

        // write suffix
        printer("]" +
                "}" +
                "}" +
                "}")
    }

    private fun Boolean.runIfTrue(and: Boolean = true, block: () -> Unit) = this.also {
        if (this && and) block.invoke()
    }

    private fun Boolean.runIfFalse(and: Boolean = true, block: () -> Unit) = this.also {
        if (!this && and) block.invoke()
    }

    private fun String.removeAndWarn(string: String) =
        if (contains(string)) {
            Cli.L.warn("Found \"$string\" in utterance \"$this\", removing it.")
            replace(string, "")
        } else this

    private fun String.cleanupIntentName() =
        if (contains('.') && !startsWith("AMAZON.")) {
            Cli.L.warn("Found \".\" in intent name \"$this\", replacing it by \"_\".")
            replace(".", "_")
        } else this

    private val Intent.cleanName
        get() = (annotations["AlexaName"]?.first() ?: name).cleanupIntentName()

    private fun useSystemTypes(slot: String): String = when (slot) {
        "any" -> "AMAZON.SearchQuery"
        "number" -> "AMAZON.NUMBER"
        "ordinal" -> "AMAZON.Ordinal"
        "color" -> "AMAZON.Color"
        "de-city" -> "AMAZON.DE_CITY"
        "at-city" -> "AMAZON.AT_CITY"
        "eu-city" -> "AMAZON.EUROPE_CITY"
        "us-city" -> "AMAZON.US_CITY"
        "gb-city" -> "AMAZON.GB_CITY"
        else -> slot
    }

    private fun HashSet<String>.forEachSlotType(entities: List<Entities>?, action: Iterator<Pair<String, Entities?>>.(Pair<String, Entities?>) -> Unit) = this
        .map {
            val type = it.split(':').last()
            Pair(type, entities?.firstOrNull { entity -> entity.name == type })
        }
        .filter { (slot, entities) ->
            (entities != null).runIfFalse(!slot.substringAfter(':').startsWith("AMAZON.") && !supportedGenericTypes.contains(slot)) {
                Cli.L.warn("No definition for slot type \"$slot\" found")
            }
        }
        .forEachIterator(action)
}

private fun List<String>.removeEntriesWithEmoticons() = filter { !it.containsEmoticons }

private val String.containsEmoticons
    get() = any {
        when (it.toInt()) {
            in 0x1F600..0x1F64F, // Emoticons
            in 0x1F300..0x1F5FF, // Misc Symbols and Pictographs
            in 0x1F680..0x1F6FF, // Transport and Map
            in 0x1F1E6..0x1F1FF, // Regional country flags
            in 0x2600..0x26FF, // Misc symbols
            in 0x2700..0x27BF, // Dingbats
            in 0xE0020..0xE007F, // Tags
            in 0xFE00..0xFE0F, // Variation Selectors
            in 0x1F900..0x1F9FF, // Supplemental Symbols and Pictographs
            in 0x1F018..0x1F270, // Various asian characters
            in 0x238C..0x2454, // Misc items
            in 0x20D0..0x20FF -> // Combining Diacritical Marks for Symbols
                true
            else -> false
        }
    }