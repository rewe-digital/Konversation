package com.rewedigital.voice.konversation

import com.rewedigital.voice.konversation.generator.Printer
import com.rewedigital.voice.konversation.generator.alexa.AlexaExporter
import com.rewedigital.voice.konversation.generator.kson.KsonExporter
import com.rewedigital.voice.konversation.parser.Parser
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

class Cli(args: Array<String>) {
    private var intents: MutableList<Intent>

    init {
        var input: String? = null
        var cacheEverything = true // should be not the default value
        var countPermutations = false
        var stats = false
        var outputFile: String? = "result.json"
        var limit: Long? = null
        var prettyPrint = false
        var compile = false
        var dumpOnly = false
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
                            if (++argNo < args.size) {
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
                            if (++argNo < args.size) {
                                try {
                                    limit = args[argNo].toLong()
                                } catch (e: Throwable) {
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
                        "dump",
                        "-dump" -> dumpOnly = true
                        "compile",
                        "-compile" -> compile = true
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

        intents = Parser(input!!).intents
        println("Parsing finished. Found ${intents.size} intents.")

        if (countPermutations) {
            fun Long.formatted() = String.format(Locale.getDefault(), "%,d", this)

            var total = 0L
            intents.forEach { intent: Intent ->
                var count = 0L
                intent.utterances.forEach { it -> count += it.permutationCount }
                if (stats) println("${intent.name} has ${intent.utterances.size} utterances which have in total ${count.formatted()} permutations")
                total += count
            }
            if (stats) println("That are in total ${total.formatted()} permutations!")
        }

        val all = intents.sumBy { intent ->
            val permutations = intent.utterances.sumBy { utterance -> utterance.permutations.size }
            if (stats) println("${intent.name} has now $permutations sample utterances")
            permutations
        }
        if (stats) println("Generated in total $all Utterances")
        //println("- " + all.sorted().joinToString(separator = "\n- "))

        if (dumpOnly) {
            outputFile = null
            intents.forEach { intent ->
                if (intent.utterances.isEmpty()) {
                    println("Skipping empty intent ${intent.name}...")
                } else {
                    println("Dumping ${intent.name}...")
                    val stream = File("${intent.name}.txt").outputStream()
                    intent.utterances.forEach { utterance ->
                        utterance.permutations.forEach { permutation ->
                            stream.write(permutation.toByteArray())
                            stream.write(13)
                            stream.write(10)
                        }
                    }
                    stream.close()
                }
            }
        }

        intents.forEach { intent ->
            println("Response of ${intent.name}")
            intent.prompt.create().runIfNotNullOrEmpty(::println)
            println("---")
        }

        //println(intents[1].prompt.create())

        if (compile) {
            intents.forEach { intent ->
                val exporter = KsonExporter(intent.name)
                File("out").mkdirs()
                val stream = File("out/${intent.name}.kson").outputStream()
                val printer: Printer = { line ->
                    stream.write(line.toByteArray())
                }
                if (prettyPrint) {
                    exporter.prettyPrinted(printer, intents)
                } else {
                    exporter.minified(printer, intents)
                }
            }
        }

        outputFile?.let {
            val exporter = AlexaExporter(File(input).absoluteFile.parent, limit ?: Long.MAX_VALUE)
            val stream = File(outputFile).outputStream()
            val printer: Printer = { line ->
                stream.write(line.toByteArray())
            }
            if (prettyPrint) {
                exporter.prettyPrinted(printer, intents)
            } else {
                exporter.minified(printer, intents)
            }
        }
    }

    private fun help() {
        println("Arguments for konversation:")
        println("[-help]           Print this help")
        println("[-count]          Count the permutations and print this to the console")
        println("[-stats]          Print out some statistics while generation")
        println("[-cache]          Cache everything even if an utterance has just a single permutation")
        println("[-out <OUTFILE>]  Write the resulting json to OUTFILE instead of result.json")
        println("[-limit <COUNT>]  While pretty printing the json to the output file limit the utterances count per intent")
        println("[-dump]           Dump out all intents to its own txt file")
        println("[-prettyprint]    Generate a well formatted json for easier debugging")
        println("<FILE>            The grammar or kvs file to parse")
        println()
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

fun String?.runIfNotNullOrEmpty(callback: (String) -> Unit) = this?.let { if (this.isNotBlank()) callback.invoke(this) }