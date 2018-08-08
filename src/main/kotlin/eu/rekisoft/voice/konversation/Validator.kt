package eu.rekisoft.voice.konversation

import eu.rekisoft.voice.konversation.parts.Utterance
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream


class Validator(args: Array<String>) {
    var intent: String? = null
    val intents = mutableListOf<Intent>()

    init {
        //val t = "cache/RecipeSearchIntent-3fa6c77a.cache"
        //val stream = FileInputStream(File(t))
        ////val stream = Files.newInputStream(Paths.get("C:\\Users\\rene.kilczan\\Programmierung\\konversation-validator\\"+URI.create(t)))
        //var count = 0
        //val buf = ByteArray(1048576000)
        //val start = System.currentTimeMillis()
        //var read = stream.read(buf, 0, buf.size)
        //var total = read
        //val nl = 13.toByte()
        //do {
        //    for (i in 0 until read) {
        //        if (buf[i] == nl) count++
        //    }
        //    read = stream.read(buf, 0, buf.size)
        //    total += read
        //} while (read > 0)
        //val end = System.currentTimeMillis()
        //println("Found $count lines in $t in (${end - start}ms) -> ${String.format("%.2f", total / (end - start * 1.0) / 1.024 / 1024)}MB/s")


        var input: String? = null
        var cacheEverything = true // should be not the default value
        var countPermutations = false
        var generatePermutations = false
        var writeOutput = false // should be true
        if (args.isEmpty()) {
            println("Missing arguments! Please specify at last the kvs or grammar file you want to process.")
            input = "C:\\Users\\rene.kilczan\\Programmierung\\REWE-Voice\\alexa-docs\\rewe.grammar"
            println("In this debug build will the file \"$input\" be used.")
        } else {
            var argNo = 0
            while (argNo < args.size) {
                val arg = args[argNo]
                if (File(arg).exists()) {
                    input = arg
                } else {
                    when (arg) {
                        "count",
                        "-count" -> countPermutations = true
                        "cache",
                        "-cache" -> cacheEverything = true
                        "generate",
                        "-generate",
                        "generatePermutations",
                        "-generatePermutations" -> generatePermutations = true
                        "write" -> writeOutput = true
                        else -> println("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }
            if (!File(input.orEmpty()).exists()) {
                println("Input file not found!")
                input = "C:\\Users\\rene.kilczan\\Programmierung\\REWE-Voice\\alexa-docs\\rewe.grammar"
                println("In this debug build will the file \"$input\" be used.")
            }
        }

        //input = "C:\\Users\\rene.kilczan\\Programmierung\\REWE-Voice\\alexa-docs\\rewe.grammar"
        //input = "demo.kvs"
        //input = "foo.grammar"
        val isGrammarFile = input!!.endsWith(".grammar")
        val lines = File(input).readLines()
        lines.forEach { line ->
            when {
                line.startsWith("//") || line.isBlank() -> {
                    // ignore comments and blank lines
                }
                line.endsWith(":") -> { // intent found
                    intent = line.substring(0, line.length - 1)
                    if (intents.find { it.name.equals(intent, true) } != null) {
                        printErr("Intent \"$intent\" already defined. Appending new parts. You have been warned.")
                    } else {
                        intents.add(Intent(intent as String))
                    }
                }
                line.startsWith("->") -> addTo {
                    // Voice only option

                }
                line.startsWith(">") -> addTo {
                    // Voice only

                }
                line.startsWith("-") -> addTo {
                    // option

                }
                line.startsWith("#switch") -> { // switch

                }
                line.startsWith("#if") -> { // if

                }
                line.startsWith("!") -> addTo {
                    addUtterance(this, line.substring(2))
                }
                else -> addTo {
                    if (isGrammarFile) {
                        // handle as sample utterance since this is a grammar file
                        addUtterance(this, line)
                    } else {
                        // static part
                    }
                }
            }
        }
        println("Parsing finished. Found ${intents.size} intents.")

        if (countPermutations) {
            fun Long.formatted() = String.format(Locale.getDefault(), "%,d", this)

            var total = 0L
            intents.forEach { intent ->
                var count = 0L
                intent.utterances.forEach { count += it.permutationCount }
                println("${intent.name} has ${intent.utterances.size} utterances which have in total ${count.formatted()} permutations")
                total += count
            }
            println("That are in total ${total.formatted()} permutations!")
        }

        if (generatePermutations) {
            val all = intents.sumBy {
                val permutations = it.utterances.sumBy { it.permutations.size }
                println("${it.name} has now $permutations sample utterances")
                permutations
            }
            println("This is in total $all")
            //println("- " + all.sorted().joinToString(separator = "\n- "))

            println("Just to test the caching:")
            val check = intents[0].utterances
            //val check = intents.find { it.name == "RecipeSearchIntent" }?.utterances
            val utterances = check.sumBy { it.permutations.size }
            println("The intent has ${check.size} sample utterances which generated $utterances permutations")
        }

        if (writeOutput) {
            val prefix = "{\n" +
                    "  \"interactionModel\" : {\n" +
                    "    \"languageModel\" : {\n" +
                    "      \"invocationName\" : \"rewe\",\n" +
                    "      \"intents\" : [\n"
            println(prefix.trimEnd())

            intents.forEachIterator { intent ->
                println("        {\n" +
                        "          \"name\" : \"AddOfferToShoppingListIntent\",\n" +
                        "          \"slots\" : [")
                val allSlots = intent.utterances.flatMap { it.slotTypes }.toHashSet()
                allSlots.forEachIterator { slot ->
                    val (name, type) = if (slot.contains(':')) {
                        val parts = slot.split(':')
                        Pair(parts[0], parts[1])
                    } else {
                        Pair(slot, slot)
                    }
                    println("            {\n" +
                            "              \"name\" : \"$name\",\n" +
                            "              \"type\" : \"$type\"\n" +
                            "            }" + if (hasNext()) "," else "")
                }
                println("          ],\n" +
                        "          \"samples\" : [")
                var total = 0
                var moreUtterances = false
                intent.utterances.forEachBreakable { utterance ->
                    total = 0
                    moreUtterances = hasNext()
                    utterance.permutations.forEachBreakable {
                        total++
                        if (total > 20) {
                            stop()
                            moreUtterances = false
                        }
                        println("            \"$it\"" + if (hasNext() || moreUtterances) "," else "")
                    }
                    if (total > 20) {
                        stop()
                    }
                }
                println("          ]\n" +
                        "        }" + if (hasNext()) "," else "")

                //val permutations = intent.utterances.sumBy { it.permutations.size }
                //println("${intent.name} has ${intent.utterances.size} utterances which have $permutations permutations and ${allSlots.size} (${allSlots.joinToString()})")
            }
            println("      ],\n" +
                    "      \"types\" : [")

            val baseDir = File(input).absoluteFile.parent
            intents.flatMap { it.utterances.flatMap { it.slotTypes } }
                    .toHashSet()
                    .map { Pair(it, File("$baseDir/$it.values")) }
                    .filter { it.second.exists() }
                    .map { Pair(it.first, it.second.readLines().filter { it.isNotEmpty() }) }
                    .forEachIterator { (slotType, values) ->
                        println("        {\n" +
                                "          \"name\": \"$slotType\",\n" +
                                "          \"values\": [")
                        values.forEachIterator { value ->
                            if (value.startsWith('{')) {
                                val aliases = value.substring(1, value.length - 1).split("|")
                                val id = aliases.first()
                                println("            {\n" +
                                        "              \"name\": {\n" +
                                        "                \"value\": \"$id\"\n" +
                                        "                \"synonyms\": [")
                                aliases.stream().skip(1).forEachIterator { alias ->
                                    println("                  \"$alias\"" + if (hasNext()) "," else "")
                                }
                                println("                ]\n" +
                                        "              }\n" +
                                        "            }" + if (hasNext()) "," else "")
                            } else {
                                println("            {\n" +
                                        "              \"name\": {\n" +
                                        "                \"value\": \"$value\"\n" +
                                        "              }\n" +
                                        "            }" + if (hasNext()) "," else "")
                            }
                        }
                        println("          ]\n" +
                                "        }" + if (hasNext()) "," else "")
                    }

            println("      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}")
            /*
                {
                    "name": "OFFER_CATEGORIES",
                    "values": [
                        {
                            "name": {
                                "value": "1.1",
                                "synonyms": [
                                    "Obst & Gemüse",
                                    "Gemüse",
                                    "Tomaten",
                                    "Gurken",
                                    "Kartoffeln",
             */
        }
    }

    class Intent(val name: String) {
        val parts = mutableListOf<Part>()
        val utterances = mutableListOf<Utterance>()
        val answers = mutableListOf<Utterance>()

    }

    private fun addUtterance(intent: Intent, utterance: String) {
        intent.utterances.add(Utterance(utterance, intent.name))
    }

    private fun addTo(block: Intent.() -> Unit) = intent?.let {
        intents.find { it.name == intent }?.let(block::invoke)
    } ?: printErr("No intent defined.")

    private fun printErr(errorMsg: String) =
            System.err.println(errorMsg)

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Validator(args)
        }
    }
}

fun <T> Iterable<T>.forEachIterator(block: Iterator<T>.(element: T) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) block(iterator, iterator.next())
}

fun <T> Stream<T>.forEachIterator(block: Iterator<T>.(element: T) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) block(iterator, iterator.next())
}

fun <T> Iterable<T>.forEachBreakable(block: BreakableIterator<T>.(element: T) -> Unit) {
    val iterator = BreakableIterator(iterator())
    while (iterator.hasNext()) block(iterator, iterator.next())
}

class BreakableIterator<T>(private val inner: Iterator<T>) : Iterator<T> {
    private var resume = true

    override fun forEachRemaining(action: Consumer<in T>) =
            inner.forEachRemaining(action)

    override fun hasNext() = resume && inner.hasNext()

    override fun next() = inner.next()

    fun stop() {
        resume = false
    }
}