package org.rewedigital.konversation

import org.rewedigital.konversation.generator.Printer
import org.rewedigital.konversation.generator.alexa.AlexaExporter
import org.rewedigital.konversation.generator.kson.KsonExporter
import org.rewedigital.konversation.parser.Parser
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

open class Cli {
    val intentDb = mutableMapOf<String, MutableList<Intent>>()
    private var cacheEverything = true // should be not the default value
    private var countPermutations = false
    private var stats = false
    private var outputFile = File("result.json")
    private var limit: Long? = null
    private var prettyPrint = false
    private var ksonDir: String? = null
    private var dumpOnly = false
    private var invocationName: String? = null
    private var exportAlexa: Boolean = false
    private var inputFileCount = 0

    fun parseArgs(args: Array<String>) {
        val inputFiles = mutableListOf<File>()
        if (args.isEmpty()) {
            L.error("Missing arguments! Please specify at least the kvs or grammar file you want to process.")
            L.error()
            help()
            exit(-1)
        } else {
            var argNo = 0
            while (argNo < args.size) {
                val arg = args[argNo]
                if (File(arg).exists()) {
                    inputFiles += File(arg)
                } else if (arg.endsWith(".kvs") || arg.endsWith(".grammar")) {
                    L.error("Input file \"$arg\" not found!")
                    exit(-1)
                } else {
                    when (arg.toLowerCase()) {
                        "help",
                        "-help",
                        "-h",
                        "/?" -> {
                            help()
                            return
                        }
                        "count",
                        "-count" -> countPermutations = true
                        "cache",
                        "-cache" -> cacheEverything = true
                        "--export-alexa" -> if (++argNo < args.size) {
                            exportAlexa = true
                            outputFile = File(args[argNo])
                        } else {
                            L.error("Target is missing")
                            exit(-1)
                        }
                        "invocation",
                        "-invocation" -> if (++argNo < args.size) {
                            invocationName = args[argNo]
                        }
                        "--export-kson" -> if (++argNo < args.size) {
                            ksonDir = args[argNo]
                        } else {
                            L.error("Target directory is missing")
                            exit(-1)
                        }
                        "stats",
                        "-stats" -> stats = true
                        "limit",
                        "-limit",
                        "top",
                        "-top" -> if (++argNo < args.size) {
                            try {
                                limit = args[argNo].toLong()
                            } catch (e: Throwable) {
                                L.error("\"${args[argNo]}\" is no valid count of utterances.")
                                exit(-1)
                            }
                        } else {
                            L.error("Count is missing!")
                            exit(-1)
                        }
                        "prettyprint",
                        "-prettyprint" -> prettyPrint = true
                        "dump",
                        "-dump" -> dumpOnly = true
                        else -> L.error("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }

            inputFiles.forEach { inputFile ->
                when {
                    inputFile.isFile -> {
                        inputFileCount++
                        intentDb.getOrPut("") { mutableListOf() } += parseFile(inputFile)
                    }
                    inputFile.isDirectory -> inputFile
                        .listFiles { dir: File?, name: String? ->
                            File(dir, name).isDirectory && (name == "konversation" || name?.startsWith("konversation-") == true)
                        }.toList()
                        .flatMap { it.listFiles { _, name -> name.endsWith(".kvs") || name.endsWith(".grammar") }.toList() }
                        .also {
                            inputFileCount += it.size
                        }
                        .forEach {
                            val prefix = it.parentFile.absolutePath.substring(inputFile.absolutePath.length + 13).trimStart('-')
                            intentDb.getOrPut(prefix) { mutableListOf() } += parseFile(it)
                        }
                    else -> {
                        L.error("Input file not found!")
                        exit(-1)
                    }
                }
            }

            showStats()
            exportData(ksonDir?.let(::File) ?: outputFile)
        }
    }

    open fun parseFile(file: File): List<Intent> = Parser(file).intents

    private fun showStats() = intentDb[""]?.let { intents ->
        val intentCount = intentDb.values.flatten().distinctBy { it.name }.size
        L.info("Parsing of $inputFileCount file${if (inputFileCount != 1) "s" else ""} finished. Found $intentCount intent${if (intentCount != 1) "s" else ""}.")

        if (countPermutations) {
            fun Long.formatted() = String.format(Locale.getDefault(), "%,d", this)

            var total = 0L
            intents.forEach { intent: Intent ->
                var count = 0L
                intent.utterances.forEach { it -> count += it.permutationCount }
                if (stats) L.debug("${intent.name} has ${intent.utterances.size} utterances which have in total ${count.formatted()} permutations")
                total += count
            }
            if (stats) L.debug("That are in total ${total.formatted()} permutations!")
        }

        val all = intents.sumBy { intent ->
            val permutations = intent.utterances.sumBy { utterance -> utterance.permutations.size }
            if (stats) L.debug("${intent.name} has now $permutations sample utterances")
            permutations
        }
        if (stats) L.info("Generated in total $all Utterances")
        //println("- " + all.sorted().joinToString(separator = "\n- "))

        if (dumpOnly) {
            intents.forEach { intent ->
                if (intent.utterances.isEmpty()) {
                    L.info("Skipping empty intent ${intent.name}...")
                } else {
                    L.log("Dumping ${intent.name}...")
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

        // FIXME or remove me
        //if (!input.endsWith(".grammar") && dumpOnly) {
        //    intents.forEach { intent ->
        //        println("Response of ${intent.name}")
        //        //intent.prompt.create().runIfNotNullOrEmpty(::println)
        //        println("---")
        //    }
        //}

        //println(intents[1].prompt.create())
    }

    private fun exportData(baseDir: File) = intentDb.forEach { (config, intents) ->
        val targetDir = File(baseDir.path + File.separator + "konversation".join("-", config))
        ksonDir?.let {
            intents.forEach { intent ->
                val exporter = KsonExporter(intent.name)
                targetDir.mkdirs()
                val stream = File(targetDir, "${intent.name}.kson").outputStream()
                val printer: Printer = { line ->
                    stream.write(line.toByteArray())
                }
                if (prettyPrint) {
                    exporter.prettyPrinted(printer, intents)
                } else {
                    exporter.minified(printer, intents)
                }
                stream.close()
            }
        }

        if (exportAlexa) {
            outputFile.absoluteFile.parentFile.mkdirs()
            invocationName?.let { skillName ->
                val exporter = AlexaExporter(skillName, targetDir, limit ?: Long.MAX_VALUE)
                val stream = outputFile.outputStream()
                val printer: Printer = { line ->
                    stream.write(line.toByteArray())
                }
                if (prettyPrint) {
                    exporter.prettyPrinted(printer, intents)
                } else {
                    exporter.minified(printer, intents)
                }
                stream.close()
            } ?: run {
                L.error("Invocation name is missing! Please specify the invocation name with the parameter -invocation <name>.")
                exit(-1)
            }
        }
    }

    private fun help() {
        L.log("Arguments for konversation:")
        L.log("[-help]                     Print this help")
        L.log("[-count]                    Count the permutations and print this to the console")
        L.log("[-stats]                    Print out some statistics while generation")
        L.log("[-cache]                    Cache everything even if an utterance has just a single permutation")
        L.log("[--export-alexa <OUTFILE>]  Write the resulting json to OUTFILE instead of result.json")
        L.log("[-invocation <NAME>]        Define the invocation name for the Alexa export")
        L.log("[-limit <COUNT>]            While pretty printing the json to the output file limit the utterances count per intent")
        L.log("[--export-kson <OUTDIR>]    Compiles the kvs file to kson resource files which are required for the runtime")
        L.log("[-dump]                     Dump out all intents to its own txt file")
        L.log("[-prettyprint]              Generate a well formatted json for easier debugging")
        L.log("<FILE>                      The grammar or kvs file to parse")
        L.log()
    }

    open fun exit(status: Int) {
        exitProcess(status)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Cli().parseArgs(args)
        }

        var L: LoggerFacade = DefaultLogger()
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

fun String.join(delimiter: String, value: String?) =
    if (value.isNullOrBlank()) {
        this
    } else {
        "$this$delimiter$value"
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