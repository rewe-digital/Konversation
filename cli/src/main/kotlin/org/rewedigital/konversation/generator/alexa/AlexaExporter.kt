package org.rewedigital.konversation.generator.alexa

import org.rewedigital.konversation.*
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import java.io.File

class AlexaExporter(private val skillName: String, private val baseDir: File, private val limit: Long) : Exporter {
    private val supportedGenericTypes = arrayOf("any", "number", "ordinal", "color")

    // TODO make sure that both branches have equal functionality
    override fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        // write prefix
        printer("{\n" +
                "  \"interactionModel\": {\n" +
                "    \"languageModel\": {\n" +
                "      \"invocationName\": \"$skillName\",\n" +
                "      \"intents\": [\n")

        // write out intents
        intents.forEachIterator { intent ->
            printer("        {\n" +
                    "          \"name\": \"${intent.name.replaceAndWarn(".", "_")}\",\n" +
                    "          \"slots\": [")
            // TODO use here .take(limit) to limit the output and remove "total" some lines below
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
            var moreUtterances: Boolean
            intent.utterances.forEachBreakable { utterance ->
                total = 0
                moreUtterances = hasNext()
                utterance.permutations.forEachBreakable {
                    total++
                    if (total >= limit) {
                        stop()
                        moreUtterances = false
                    }
                    printer("            \"${it.removeAndWarn("?").removeAndWarn(",").removeAndWarn(".")}\"" + (if (hasNext() || moreUtterances) "," else "") + "\n")
                }
                if (total > limit) {
                    stop()
                }
            }
            if (intent.utterances.isNotEmpty()) {
                printer("          ]\n")
            }
            printer("        }" + (if (hasNext()) "," else "") + "\n")
        }
        // write the custom slot type definitions
        printer("      ],\n" +
                "      \"types\": [")

        val types = intents.flatMap { it.utterances.flatMap { utterance -> utterance.slotTypes } }.toHashSet()

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
                        "                \"value\": \"${entity.master}\",\n" +
                        "                \"synonyms\": [\n")
                entity.synonyms.forEachIterator { alias ->
                    printer("                  \"$alias\"" + (if (hasNext()) "," else "") + "\n")
                }
                printer("                ]\n" +
                        "              }\n" +
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
        intents.forEachIterator { intent ->
            printer("{" +
                    "\"name\":\"${intent.name}\"," +
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
            var total: Int
            var moreUtterances: Boolean
            intent.utterances.forEachIterator { utterance ->
                total = 0
                moreUtterances = hasNext()
                utterance.permutations.forEachIterator {
                    total++
                    //if (total > 20) {
                    //    stop()
                    //    moreUtterances = false
                    //}
                    printer("\"$it\"" + (if (hasNext() || moreUtterances) "," else ""))
                }
                //if (total > 20) {
                //    stop()
                //}
            }
            printer("]" +
                    "}" + (if (hasNext()) "," else ""))
        }
        // write the custom slot type definitions
        printer("]," +
                "\"types\":[")

        intents
            .flatMap { it.utterances.flatMap { utterance -> utterance.slotTypes } }
            .toHashSet()
            .map {
                val type = it.split(':').last()
                Pair(type, File("$baseDir/$type.values"))
            }
            .filter { it.second.exists() }
            .map { Pair(it.first, it.second.readLines().filter { line -> line.isNotEmpty() }) }
            .forEachIterator { (slotType, values) ->
                printer(
                    "{" +
                            "\"name\":\"$slotType\"," +
                            "\"values\":["
                )
                values.forEachIterator { valueLine ->
                    if (valueLine.startsWith('{')) {
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
                        aliases.toHashSet().apply {
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

    private fun String.replaceAndWarn(string: String, replacement: String) =
        if (contains(string)) {
            Cli.L.warn("Found \"$string\" in intent name \"$this\", replacing it by \"$replacement\".")
            replace(string, "")
        } else this

    private fun useSystemTypes(slot: String): String = when (slot) {
        "any" -> "AMAZON.SearchQuery"
        "number" -> "AMAZON.NUMBER"
        "ordinal" -> "AMAZON.Ordinal"
        "color" -> "AMAZON.Color"
        else -> slot
    }

    private fun HashSet<String>.forEachSlotType(entities: List<Entities>?, action: Iterator<Pair<String, Entities?>>.(Pair<String, Entities?>) -> Unit) = this.map {
        val type = it.split(':').last()
        Pair(type, entities?.firstOrNull { entity -> entity.name == type })
    }
        .filter { (slot, entities) ->
            (entities != null).runIfFalse(!slot.substringAfter(':').startsWith("AMAZON.")) {
                Cli.L.warn("No definition for slot type \"$slot\" found")
            }
        }
        .forEachIterator(action)
}