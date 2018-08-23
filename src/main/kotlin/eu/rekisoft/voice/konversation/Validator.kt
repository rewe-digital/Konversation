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
            val stream = File("result.json").outputStream()

            generateJsonMinimized({line ->
                stream.write(line.toByteArray())
            }, File(input).absoluteFile.parent)
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

    fun generateJson(printer: (output: String) -> Unit, baseDir: String) {
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
                    if (total > 20) {
                        stop()
                        moreUtterances = false
                    }
                    printer("            \"$it\"" + (if (hasNext() || moreUtterances) "," else "") + "\n")
                }
                if (total > 20) {
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
                                    "                \"value\": \"$id\"\n" +
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

    fun generateJsonMinimized(printer: (output: String) -> Unit, baseDir: String) {
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
                                    "\"value\":\"$id\"" +
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