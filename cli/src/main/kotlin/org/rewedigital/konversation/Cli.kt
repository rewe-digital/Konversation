package org.rewedigital.konversation

import org.rewedigital.konversation.generator.Printer
import org.rewedigital.konversation.generator.alexa.AlexaExporter
import org.rewedigital.konversation.generator.dialogflow.DialogflowExporter
import org.rewedigital.konversation.generator.kson.KsonExporter
import org.rewedigital.konversation.parser.Parser
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

open class Cli {
    val intentDb = mutableMapOf<String, MutableList<Intent>>()
    val entityDb = mutableMapOf<String, MutableList<Entities>>()
    private var cacheEverything = true // should be not the default value
    private var countPermutations = false
    private var stats = false
    private var alexaIntentSchema: File? = null
    private var limit: Int? = null
    private var prettyPrint = false
    private var ksonDir: File? = null
    private var dialogflowDir: File? = null
    private var dumpOnly = false
    private var invocationName: String? = null
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
                if (File(arg).absoluteFile.exists()) {
                    inputFiles += File(arg).absoluteFile
                } else if (arg.endsWith(".kvs") || arg.endsWith(".grammar") || arg.endsWith(".values")) {
                    if (arg.contains('*')) {
                        val matcher = arg.substringAfterLast('\\').substringAfterLast('/').replace(".", "\\.").replace("*", ".*?").toRegex()
                        File(arg).parentFile.listFiles { _, name ->
                            matcher.matches(name)
                        }.map { file ->
                            inputFiles += file.absoluteFile
                        }
                    } else {
                        L.error("Input file \"$arg\" not found!")
                        exit(-1)
                    }
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
                            alexaIntentSchema = File(args[argNo])
                        } else {
                            L.error("Target is missing")
                            exit(-1)
                        }
                        "--export-dialogflow" -> if (++argNo < args.size) {
                            dialogflowDir = File(args[argNo]).absoluteFile
                        } else {
                            L.error("Target is missing")
                            exit(-1)
                        }
                        "invocation",
                        "-invocation" -> if (++argNo < args.size) {
                            invocationName = args[argNo]
                        }
                        "--export-kson" -> if (++argNo < args.size) {
                            ksonDir = File(args[argNo])
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
                                limit = args[argNo].toInt()
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
                        "-v",
                        "-version" -> L.log("Konversation CLI version 1.0.1")
                        else -> L.error("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }

            inputFiles.forEach { inputFile ->
                when {
                    inputFile.isFile -> {
                        inputFileCount++
                        val parser = parseFile(inputFile)
                        intentDb.getOrPut("") { mutableListOf() } += parser.intents
                        parser.entities?.let { entityDb.getOrPut("") { mutableListOf() } += it }
                    }
                    inputFile.isDirectory -> inputFile
                        .listFiles { dir: File?, name: String? ->
                            File(dir, name).isDirectory && (name == "konversation" || name?.startsWith("konversation-") == true)
                        }.toList()
                        .flatMap { it.listFiles { _, name -> name.endsWith(".kvs") || name.endsWith(".grammar") || name.endsWith(".values") }.toList() }
                        .also {
                            inputFileCount += it.size
                        }
                        .forEach { file ->
                            val prefix = file.parentFile.absolutePath.substring(inputFile.absolutePath.length + 13).trimStart('-')
                            val parser = parseFile(file)
                            intentDb.getOrPut(prefix) { mutableListOf() } += parser.intents
                            parser.entities?.let { entityDb.getOrPut(prefix) { mutableListOf() } += it }
                        }
                    else -> {
                        L.error("Input file not found!")
                        exit(-1)
                    }
                }
            }

            showStats()

            ksonDir?.let(::exportKson)
            alexaIntentSchema?.let(::exportAlexa)
            dialogflowDir?.let(::exportDialogflow)
        }
    }

    open fun parseFile(file: File) = Parser(file)

    private fun showStats() = intentDb[""]?.let { intents ->
        val intentCount = intentDb.values.flatten().distinctBy { it.name }.size
        L.info("Parsing of $inputFileCount file${if (inputFileCount != 1) "s" else ""} finished. Found $intentCount intent${if (intentCount != 1) "s" else ""}.")

        fun Number.formatted() = String.format(Locale.getDefault(), "%,d", this)

        if (countPermutations) {
            var total = 0L
            intents.forEach { intent: Intent ->
                var count = 0L
                intent.utterances.forEach { count += it.permutationCount }
                if (stats) L.debug("${intent.name} has ${intent.utterances.size} utterances which have in total ${count.formatted()} permutations")
                total += count
            }
            if (stats) L.debug("That are in total ${total.formatted()} permutations!")
        }

        val all = intents.sumBy { intent ->
            val permutations = intent.utterances.sumBy { utterance -> utterance.permutations.size }
            if (stats) L.debug("${intent.name} has now ${permutations.formatted()} sample utterances")
            if (permutations>1000) L.warn("${intent.name} has ${permutations.formatted()} utterances, Dialogflow just support up to ${1000.formatted()}!")
            permutations
        }
        if (stats) L.info("Generated in total ${all.formatted()} Utterances")
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
    }

    private fun exportKson(baseDir: File) = intentDb.forEach { (config, intents) ->
        val targetDir = File(baseDir.absolutePath + File.separator + "konversation".join("-", config))
        ksonDir?.let {
            intents.forEach { intent ->
                val exporter = KsonExporter(intent.name)
                targetDir.mkdirs()
                val stream = File(targetDir, "${intent.name}.kson").outputStream()
                val printer: Printer = { line ->
                    stream.write(line.toByteArray())
                }
                if (prettyPrint) {
                    exporter.prettyPrinted(printer, intents, entityDb[config])
                } else {
                    exporter.minified(printer, intents, entityDb[config])
                }
                stream.close()
            }
        }
    }

    private fun exportAlexa(alexaIntentSchema: File) = intentDb.forEach { (config, intents) ->
        val targetDir = File(alexaIntentSchema.path + File.separator + "konversation".join("-", config))
        alexaIntentSchema.absoluteFile.parentFile.mkdirs()
        invocationName?.let { skillName ->
            val exporter = AlexaExporter(skillName, targetDir, limit ?: Int.MAX_VALUE)
            val stream = alexaIntentSchema.outputStream()
            val printer: Printer = { line ->
                stream.write(line.toByteArray())
            }
            if (prettyPrint) {
                exporter.prettyPrinted(printer, intents, entityDb[config])
            } else {
                exporter.minified(printer, intents, entityDb[config])
            }
            stream.close()
        } ?: run {
            L.error("Invocation name is missing! Please specify the invocation name with the parameter -invocation <name>.")
            exit(-1)
        }
    }

    private fun exportDialogflow(baseDir: File) = intentDb.forEach { (config, intents) ->
        invocationName?.let { skillName ->
            val exporter = DialogflowExporter(skillName)
            val stream = File(baseDir, "dialogflow-$config.zip").outputStream()
            if (prettyPrint) {
                exporter.prettyPrinted(stream, intents, entityDb[config])
            } else {
                exporter.minified(stream, intents, entityDb[config])
            }
            stream.close()
        } ?: run {
            L.error("Invocation name is missing! Please specify the invocation name with the parameter -invocation <name>.")
            exit(-1)
        }
    }

    private fun help() {
        L.log("Arguments for konversation:")
        L.log("[-help]                         Print this help")
        L.log("[-version]                      Print the version of this build")
        L.log("[-count]                        Count the permutations and print this to the console")
        L.log("[-stats]                        Print out some statistics while generation")
        L.log("[-cache]                        Cache everything even if an utterance has just a single permutation")
        L.log("[--export-alexa <OUTFILE>]      Write the resulting json to OUTFILE instead of result.json")
        L.log("[--export-dialogflow <OUTDIR>]  Write the dialogflow zip file to the OUTDIR")
        L.log("[-invocation <NAME>]            Define the invocation name for the Alexa export")
        L.log("[-limit <COUNT>]                While pretty printing the json to the output file limit the utterances count per intent")
        L.log("[--export-kson <OUTDIR>]        Compiles the kvs file to kson resource files which are required for the runtime")
        L.log("[-dump]                         Dump out all intents to its own txt file")
        L.log("[-prettyprint]                  Generate a well formatted json for easier debugging")
        L.log("<FILE>                          The grammar, kvs or values files to parse")
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