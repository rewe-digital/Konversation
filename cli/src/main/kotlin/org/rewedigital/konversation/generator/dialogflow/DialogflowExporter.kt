package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.*
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.StreamExporter
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.max

class DialogflowExporter(private val invocationName: String) : StreamExporter {
    private val lang = "de"
    private val supportedGenericTypes = arrayOf("any", "number", "ordinal", "color", "de-city", "at-city", "eu-city", "us-city", "gb-city")

    override fun prettyPrinted(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        val json = StringBuilder()
        intents.filter { !it.name.startsWith("AMAZON.", ignoreCase = true) }.forEachSlotType(entities) { slotType ->
            json.clear()
            val meta = EntityMetaData(automatedExpansion = false,
                id = UUID.nameUUIDFromBytes("$invocationName:${slotType.name}".toByteArray()),
                isEnum = false,
                isOverridable = false,
                name = slotType.name)

            meta.prettyPrinted { s -> json.append(s) }
            zipStream.add("entities/${slotType.name}.json", json)
            //println("entities/${slotType.name}.json:\n$json")
            json.clear()
            val entries = slotType.values.map { entry ->
                Entity(key = entry.key, value = entry.master, synonyms = entry.synonyms)
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
        var i = 0
        intents.filter { !it.name.startsWith("AMAZON.", ignoreCase = true) }.forEach { intent ->
            json.clear()
            val intentData = DialogflowIntent(intent, createResponses(intent))
            intentData.prettyPrinted { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
            json.clear()
            //println("intents/${intent.name}_usersays_<LANG>.json:")
            json.append("[\n")
            //println("Dumping ${intent.name} (${intent.utterances.sumByLong { it.permutationCount }} utterances):")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to parts.last()
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val (data, permutations) = createParts(sentence, slots, entities, i)
                    i += permutations
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
                id = UUID.nameUUIDFromBytes("$invocationName:${slotType.name}".toByteArray()),
                isEnum = false,
                isOverridable = false,
                name = slotType.name.cleanupSlotName())
            meta.minified { s -> json.append(s) }
            zipStream.add("entities/${slotType.name}.json", json)
            json.clear()
            val entries = slotType.values.map { entry ->
                Entity(key = entry.key, value = entry.master, synonyms = entry.synonyms)
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
        var i = 0
        intents.filter { !it.name.startsWith("AMAZON.") }.forEach { intent ->
            json.clear()
            val intentData = DialogflowIntent(intent, createResponses(intent))
            intentData.minified { s -> json.append(s) }
            zipStream.add("intents/${intent.name}.json", json)
            json.clear()
            json.append("[")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to parts.last()
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                val hasMoreUtterances = hasNext()
                utterance.permutations.forEachBreakable { sentence ->
                    val (data, permutations) = createParts(sentence, slots, entities, i)
                    i += permutations
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

    private fun createParts(sentence: String,
        slots: Map<String, String>,
        entities: List<Entities>?,
        sampleOffset: Int): Pair<List<DialogflowUtterance.UtterancePart>, Int> {
        var i = sampleOffset
        return if (sentence.contains("{") && sentence.contains("}")) {
            sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                val type = slots[part]?.removePrefix("AMAZON.")
                type?.let {
                    val values = entities?.firstOrNull { it.name == slots[part] }?.values?.map { it.master }
                    val sample = values?.getOrNull(i % max(1, values.size)) ?: defaultValue(type, i)
                    i++

                    DialogflowUtterance.UtterancePart(text = sample, alias = part, meta = "@${useSystemTypes(type)}", userDefined = false)
                } ?: DialogflowUtterance.UtterancePart(text = part, userDefined = false)

            }
        } else {
            listOf(DialogflowUtterance.UtterancePart(text = sentence, userDefined = false))
        } to i - sampleOffset
    }

    private fun createResponses(intent: Intent) =
        listOf(Response(action = intent.name,
            messages = listOfNotNull(Message(
                lang = lang, // FIXME the structure of the exporter does not allow that we know other translations.
                speech = intent.prompt.generateSamples()),
                createSuggestion(intent.suggestions)),
            parameters = intent.utterances.flatMap { it.slotTypes }.toHashSet().map {
                ResponseParameter(it, intent.annotations["ListParameters"]?.contains(it.substringBefore(':')) == true)
            }.sortedBy { it.name }))

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
        .map { it.copy(name = it.name.cleanupSlotName()) }
        .forEach(action)

    private fun useSystemTypes(slot: String): String = when (slot) {
        "any" -> "sys.any"
        "number" -> "sys.number"
        "ordinal" -> "sys.ordinal"
        "color" -> "sys.color"
        "de-city",
        "at-city",
        "eu-city",
        "us-city",
        "gb-city" -> "sys.geo-city"
        else -> slot
    }

    private fun defaultValue(type: String, int: Int) = when (type) {
        "any" -> "foo bar"
        "number" -> int.toString()
        "ordinal" -> "$int."
        "color" -> "Blau"
        "de-city" -> listOf("Stuttgart", "München", "Berlin", "Potsdam", "Bremen", "Hamburg", "Wiesbaden", "Schwerin", "Hannover", "Düsseldorf", "Mainz", "Saarbrücken", "Dresden", "Magdeburg", "Kiel", "Erfurt").run {
            get(int % size)
        }
        "at-city" -> listOf("Eisenstadt", "Klagenfurt am Wörthersee", "St. Pölten", "Salzburg", "Graz", "Innsbruck", "Bregenz", "Wien").run {
            get(int % size)
        }
        "eu-city" -> listOf("Amsterdam",
            "Andorra",
            "Athen",
            "Belgrad",
            "Berlin",
            "Bern",
            "Bratislava",
            "Brüssel",
            "Budapest",
            "Bukarest",
            "Chișinău",
            "Città",
            "Dublin",
            "Helsinki",
            "Kiew",
            "Kopenhagen",
            "Lissabon",
            "Ljubljana",
            "London",
            "Luxemburg",
            "Madrid",
            "Minsk",
            "Monaco",
            "Moskau",
            "Nikosia",
            "Oslo",
            "Paris",
            "Podgorica",
            "Prag",
            "Reykjavík",
            "Riga",
            "Rom",
            "Sarajevo",
            "Skopje",
            "Sofia",
            "Stockholm",
            "Tallinn",
            "Tirana",
            "Vaduz",
            "Valletta",
            "Vatikanstadt",
            "Vilnius",
            "Warschau",
            "Wien",
            "Zagreb").run {
            get(int % size)
        }
        "us-city" -> listOf("Montgomery",
            "Juneau",
            "Phoenix",
            "Rock",
            "Sacramento",
            "Denver",
            "Hartford",
            "Dover",
            "Tallahassee",
            "Atlanta",
            "Honolulu",
            "Boise",
            "Springfield",
            "Indianapolis",
            "Moines",
            "Topeka",
            "Frankfort",
            "Rouge",
            "Augusta",
            "Annapolis",
            "Boston",
            "Lansing",
            "Paul",
            "Jackson",
            "City",
            "Helena",
            "Lincoln",
            "City",
            "Concord",
            "Trenton",
            "Fe",
            "Albany",
            "Raleigh",
            "Bismarck",
            "Columbus",
            "City",
            "Salem",
            "Harrisburg",
            "Providence",
            "Columbia",
            "Pierre",
            "Nashville",
            "Austin",
            "City",
            "Montpelier",
            "Richmond",
            "Olympia",
            "Charleston",
            "Madison",
            "Cheyenne").run {
            get(int % size)
        }
        "gb-city" -> listOf("London", "Edinburgh", "Cardiff", "Belfast").run {
            get(int % size)
        }
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

    private fun String.cleanupSlotName() = when {
        startsWith("AMAZON.", ignoreCase = true) ->
            drop(7).also {
                Cli.L.warn("Found Amazon prefix in slot type \"$this\", removing it and use \"$it\" now.")
            }
        contains('.') -> {
            Cli.L.warn("Found \".\" in slot type \"$this\", replacing it by \"_\".")
            replace(".", "_")
        }
        else -> this
    }
}