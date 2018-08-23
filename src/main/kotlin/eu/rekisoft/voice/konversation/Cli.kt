package eu.rekisoft.voice.konversation

import eu.rekisoft.voice.konversation.parts.Utterance
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess


class Cli(args: Array<String>) {
    var intent: String? = null
    val intents = mutableListOf<Intent>()

    init {
        var input: String? = null
        var cacheEverything = true // should be not the default value
        var countPermutations = false
        var stats = false
        var outputFile = "result.json"
        var limit: Long? = null
        var prettyPrint = false
        if (args.isEmpty()) {
            println("Missing arguments! Please specify at least the kvs or grammar file you want to process.")
            help()
            exitProcess(-1)
        } else {
            var argNo = 0
            while (argNo < args.size) {
                val arg = args[argNo]
                if (File(arg).exists()) {
                    input = arg
                } else {
                    when (arg.toLowerCase()) {
                        "help",
                        "-help",
                        "-h",
                        "/?" -> help()
                        "count",
                        "-count" -> countPermutations = true
                        "cache",
                        "-cache" -> cacheEverything = true
                        "out",
                        "-out" -> {
                            if(++argNo < args.size) {
                                outputFile = args[argNo]
                            } else {
                                println("Target is missing")
                                exitProcess(-1)
                            }
                        }
                        "stats",
                        "-stats" -> stats = true
                        "limit",
                        "-limit",
                        "top",
                        "-top" -> {
                            if(++argNo < args.size) {
                                try {
                                    limit = java.lang.Long.parseLong(args[argNo])
                                } catch (e: NumberFormatException) {
                                    println("\"${args[argNo]}\" is no valid count of utterances.")
                                    exitProcess(-1)
                                }
                            } else {
                                println("Count is missing!")
                                exitProcess(-1)
                            }
                        }
                        "prettyprint",
                        "-prettyprint" -> prettyPrint = true
                        else -> println("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }
            if (!File(input.orEmpty()).exists()) {
                println("Input file not found!")
                exitProcess(-1)
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
                if (stats) println("${intent.name} has ${intent.utterances.size} utterances which have in total ${count.formatted()} permutations")
                total += count
            }
            if (stats) println("That are in total ${total.formatted()} permutations!")
        }

        val all = intents.sumBy { intent ->
            val permutations = intent.utterances.sumBy { utterance -> utterance.permutations.size }
            if(stats) println("${intent.name} has now $permutations sample utterances")
            permutations
        }
        if(stats) println("Generated in total $all Utterances")
        //println("- " + all.sorted().joinToString(separator = "\n- "))

        outputFile.let {
            val stream = File(outputFile).outputStream()
            if (prettyPrint) {
                generateJson({ line ->
                    stream.write(line.toByteArray())
                }, File(input).absoluteFile.parent, limit ?: Long.MAX_VALUE)
            } else {
                generateJsonMinimized({ line ->
                    stream.write(line.toByteArray())
                }, File(input).absoluteFile.parent)
            }
        }
        println("Output written to $outputFile")
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

    private fun help() {
        println("Arguments:")
        println("-help         Print this help")
        println("-count        Count the permutations and print this to the console")
        println("-stats        Print out some statistics while generation")
        println("-cache        Cache everything even if an utterance has just a single permutation")
        println("-out OUTFILE  Write the resulting json to OUTFILE instead of result.json")
        println("-limit COUNT  While pretty printing the json to the output file limit the utterances count per intent")
        println("-prettyprint  Generate a well formatted json for easier debugging")
        println("FILE          The grammar or kvs file to parse")
        println()
    }

    private fun generateJson(printer: (output: String) -> Unit, baseDir: String, limit: Long) {
        // write prefix
        printer("{\n" +
                "  \"interactionModel\" : {\n" +
                "    \"languageModel\" : {\n" +
                "      \"invocationName\" : \"rewe\",\n" +
                "      \"intents\" : [\n")

        // write out intents
        intents.forEachIterator { intent ->
            printer("        {\n" +
                    "          \"name\" : \"${intent.name}\",\n" +
                    "          \"slots\" : [\n")
            val allSlots = intent.utterances.flatMap { it.slotTypes }.toHashSet()
            allSlots.forEachIterator { slot ->
                val (name, type) = if (slot.contains(':')) {
                    val parts = slot.split(':')
                    Pair(parts[0], parts[1])
                } else {
                    Pair(slot, slot)
                }
                // write slot types
                printer("            {\n" +
                        "              \"name\" : \"$name\",\n" +
                        "              \"type\" : \"$type\"\n" +
                        "            }" + (if (hasNext()) "," else "") + "\n")
            }
            // write sample utterances
            printer("          ],\n" +
                    "          \"samples\" : [\n")
            var total: Int
            var moreUtterances: Boolean
            intent.utterances.forEachBreakable { utterance ->
                total = 0
                moreUtterances = hasNext()
                utterance.permutations.forEachBreakable {
                    total++
                    if (total > limit) {
                        stop()
                        moreUtterances = false
                    }
                    printer("            \"$it\"" + (if (hasNext() || moreUtterances) "," else "") + "\n")
                }
                if (total > limit) {
                    stop()
                }
            }
            printer("          ]\n" +
                    "        }" + (if (hasNext()) "," else "") + "\n")
        }
        // write the custom slot type definitions
        printer("      ],\n" +
                "      \"types\" : [\n")

        intents.flatMap { it.utterances.flatMap { it.slotTypes } }
                .toHashSet()
                .map { Pair(it, File("$baseDir/$it.values")) }
                .filter { (slot, file) ->
                    if (file.exists())
                        true else {
                        val parts = slot.split(':')
                        if (parts.size == 2) {
                            if (!parts[1].startsWith("AMAZON.")) println("WARNING: No definition for slot type \"${parts[1]}\" found")
                        } else {
                            println("WARNING: No definition for slot type \"$slot\" found")
                        }
                        false
                    }
                }
                .map { Pair(it.first, it.second.readLines().filter { it.isNotEmpty() }) }
                .forEachIterator { (slotType, values) ->
                    printer("        {\n" +
                            "          \"name\": \"$slotType\",\n" +
                            "          \"values\": [\n")
                    values.forEachIterator { value ->
                        if (value.startsWith('{')) {
                            val aliases = value.substring(1, value.length - 1).split("|")
                            val id = aliases.first()
                            printer("            {\n" +
                                    "              \"name\": {\n" +
                                    "                \"value\": \"$id\",\n" +
                                    "                \"synonyms\": [\n")
                            aliases.stream().skip(1).forEachIterator { alias ->
                                printer("                  \"$alias\"" + (if (hasNext()) "," else "") + "\n")
                            }
                            printer("                ]\n" +
                                    "              }\n" +
                                    "            }" + (if (hasNext()) "," else "") + "\n")
                        } else {
                            printer("            {\n" +
                                    "              \"name\": {\n" +
                                    "                \"value\": \"$value\"\n" +
                                    "              }\n" +
                                    "            }" + (if (hasNext()) "," else "") + "\n")
                        }
                    }
                    printer("          ]\n" +
                            "        }" + (if (hasNext()) "," else "") + "\n")
                }

        // write suffix
        printer("      ]\n" +
                "    }\n" +
                "  }\n" +
                "}")
    }

    private fun generateJsonMinimized(printer: (output: String) -> Unit, baseDir: String) {
        // write prefix
        printer("{" +
                "\"interactionModel\":{" +
                "\"languageModel\":{" +
                "\"invocationName\":\"rewe\"," +
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

        intents.flatMap { it.utterances.flatMap { it.slotTypes } }
                .toHashSet()
                .map { Pair(it, File("$baseDir/$it.values")) }
                .filter { it.second.exists() }
                .map { Pair(it.first, it.second.readLines().filter { it.isNotEmpty() }) }
                .forEachIterator { (slotType, values) ->
                    printer("{" +
                            "\"name\":\"$slotType\"," +
                            "\"values\":[")
                    values.forEachIterator { value ->
                        if (value.startsWith('{')) {
                            val aliases = value.substring(1, value.length - 1).split("|")
                            val id = aliases.first()
                            printer("{" +
                                    "\"name\":{" +
                                    "\"value\":\"$id\"," +
                                    "\"synonyms\":[")
                            aliases.stream().skip(1).forEachIterator { alias ->
                                printer("\"$alias\"" + (if (hasNext()) "," else ""))
                            }
                            printer("]" +
                                    "}" +
                                    "}" + (if (hasNext()) "," else ""))
                        } else {
                            printer("{" +
                                    "\"name\":{" +
                                    "\"value\":\"$value\"" +
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

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Cli(args)
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