package org.rewedigital.konversation.generator.alexa

import org.rewedigital.konversation.*
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import java.io.File

class AlexaExporter(private val skillName: String, private val baseDir: File, private val limit: Long) : Exporter {

    override fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        // write prefix
        printer("{\n" +
                "  \"interactionModel\": {\n" +
                "    \"languageModel\": {\n" +
                "      \"invocationName\" : \"$skillName\",\n" +
                "      \"intents\": [\n")

        // write out intents
        intents.forEachIterator { intent ->
            printer("        {\n" +
                    "          \"name\": \"${intent.name}\",\n" +
                    "          \"slots\": [")
            val allSlots = intent.utterances.flatMap { it.slotTypes }.toHashSet()
            if (allSlots.isEmpty()) {
                printer("],")
            }
            printer("\n")
            allSlots.forEachIterator { slot ->
                val (name, type) = if (slot.contains(':')) {
                    val parts = slot.split(':')
                    Pair(parts[0], parts[1])
                } else {
                    Pair(slot, slot)
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
                    printer("            \"$it\"" + (if (hasNext() || moreUtterances) "," else "") + "\n")
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

        types
            .map {
                val type = it.split(':').last()
                Pair(type, entities?.firstOrNull { entity -> entity.name == type })
            }
            .filter { (slot, entities) ->
                (entities == null && slot.startsWith("AMAZON.")).runIfTrue {
                    Cli.L.warn("No definition for slot type \"$slot\" found")
                }
            }
            .forEachIterator { (slotType, entities) ->
                printer(
                    "        {\n" +
                            "          \"name\": \"$slotType\",\n" +
                            "          \"values\": ["
                )
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
                    Pair(parts[0], parts[1])
                } else {
                    Pair(slot, slot)
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

        intents.flatMap { it.utterances.flatMap { utterance -> utterance.slotTypes } }
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
}