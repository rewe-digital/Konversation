package org.rewedigital.konversation

import org.rewedigital.konversation.parser.Parser
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

open class Cli {
    private var cacheEverything = true // should be not the default value
    private var countPermutations = false
    private var stats = false
    private var alexaIntentSchema: File? = null
    private var prettyPrint = false
    private var ksonDir: File? = null
    private var dialogflowDir: File? = null
    private var dumpOnly = false
    private var inspect = false
    private var amazonSkillId: String? = null
    private var dialogflowProject: String? = null
    private var dialogflowServiceAccount: File? = null

    private var invocationName: String = ""
        get() {
            if (field.isEmpty()) {
                throw IllegalArgumentException("Invocation name not set!")
            }
            return field
        }

    fun parseArgs(args: Array<String>) {
        val api = KonversationApi(amazonClientId, amazonClientSecret)
        api.logger = L
        var amazonRefreshToken: String? = null
        if (args.isEmpty()) {
            L.error("Missing arguments! Please specify at least the kvs or grammar file you want to process.")
            L.error()
            help()
            exit(-1)
        } else {
            var argNo = 0
            while (argNo < args.size) {
                val arg = args[argNo]
                val argFile = File(arg)
                if (argFile.absoluteFile.exists()) {
                    api.inputFiles += File(arg).absoluteFile
                } else if (arg.endsWith(".kvs") || arg.endsWith(".grammar") || arg.endsWith(".values")) {
                    if (arg.contains('*')) {
                        val matcher = argFile.name.replace(".", "\\.").replace("*", ".*?").toRegex()
                        argFile.parentFile.listFiles { _, name ->
                            matcher.matches(name)
                        }.map { file ->
                            api.inputFiles += file.absoluteFile
                        }
                    } else {
                        throw IllegalArgumentException("Input file \"$arg\" not found!")
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
                            throw IllegalArgumentException("Target is missing")
                        }
                        "--alexa-login" -> {
                            api.authorizeAmazon(21337)
                        }
                        "--alexa-token" -> if (++argNo < args.size) {
                            amazonRefreshToken = args[argNo]
                        } else {
                            throw IllegalArgumentException("Refresh token is missing")
                        }
                        "--alexa-upload" -> if (++argNo < args.size) {
                            amazonSkillId = args[argNo]
                        } else {
                            throw IllegalArgumentException("Skill id is missing")
                        }
                        "--export-dialogflow" -> if (++argNo < args.size) {
                            dialogflowDir = File(args[argNo]).absoluteFile
                        } else {
                            throw IllegalArgumentException("Target is missing")
                        }
                        "--dialogflow-upload" -> if (argNo + 2 < args.size) {
                            dialogflowServiceAccount = File(args[++argNo])
                            dialogflowProject = args[++argNo]
                            if (dialogflowServiceAccount?.exists() != true) {
                                throw IllegalArgumentException("Service account file not found")
                            }
                        } else {
                            throw IllegalArgumentException("Arguments missing: service account file and project name is required.")
                        }
                        "invocation",
                        "-invocation" -> if (++argNo < args.size) {
                            invocationName = args[argNo]
                        }
                        "--export-kson" -> if (++argNo < args.size) {
                            ksonDir = File(args[argNo])
                        } else {
                            throw IllegalArgumentException("Target directory is missing")
                        }
                        "stats",
                        "-stats" -> stats = true
                        "prettyprint",
                        "-prettyprint" -> prettyPrint = true
                        "dump",
                        "-dump" -> dumpOnly = true
                        "-v",
                        "-version" -> L.log("Konversation CLI version 1.2.0-beta1")
                        else -> throw IllegalArgumentException("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }

            showStats(api)

            lookForCollisions(api.intentDb)

            ksonDir?.let { dir ->
                api.exportKson(dir, prettyPrint)
            }
            alexaIntentSchema?.let { file ->
                api.exportAlexaSchema(file, invocationName, prettyPrint)
            }
            dialogflowDir?.let { dir ->
                api.exportDialogflow(dir, invocationName, prettyPrint)
            }
            amazonSkillId?.let { skillId ->
                amazonRefreshToken?.let { refreshToken ->
                    api.updateAlexaSchema(refreshToken, invocationName, skillId)
                } ?: throw IllegalArgumentException("Amazon token not set")
            }
            dialogflowServiceAccount?.let { serviceAccount ->
                api.updateDialogflowProject(serviceAccount, dialogflowProject!!, invocationName)
            }
        }
    }

    open fun parseFile(file: File) = Parser(file)

    private fun showStats(api: KonversationApi) = api.intentDb[""]?.let { intents ->
        val intentCount = api.intentDb.values.flatten().distinctBy { it.name }.size
        L.info("Parsing of ${api.inputFiles.size} file${if (api.inputFiles.size != 1) "s" else ""} finished. Found $intentCount intent${if (intentCount != 1) "s" else ""}.")

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
            val warn = when (permutations) {
                in 0..999 -> false
                in 1000..1999 -> {
                    L.warn("${intent.name} has ${permutations.formatted()} utterances, Actions on Google just support up to ${1000.formatted()}!")
                    true
                }
                else -> {
                    L.warn("${intent.name} has ${permutations.formatted()} utterances, Dialogflow just support up to ${2000.formatted()} and Actions on Google just ${1000.formatted()}!")
                    true
                }
            }
            if (inspect || warn) {
                L.info("Intent ${intent.name} has in total ${permutations.formatted()} utterances:")
                intent.utterances.forEach { utterance ->
                    L.log(String.format(Locale.getDefault(), "%,6d utterances for %s", utterance.permutations.size, utterance.source))
                }
            }
            permutations
        }
        if (stats) L.info("Generated in total ${all.formatted()} Utterances")
        //println("- " + all.sorted().joinToString(separator = "\n- "))
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

    private fun lookForCollisions(intentDb: Map<String, List<Intent>>) {
        val skip = mutableListOf<String>()
        // check all configurations
        intentDb.forEach { (_, intents) ->
            // check all intents
            intents.forEach { self ->
                // put the current intent on the blacklist to avoid checking it self and find the collision in the duplicated intents
                skip += self.name
                self.utterances.forEach { utterance ->
                    // look for a duplicate in the other intents
                    intents.forEach { other ->
                        // avoid double checks
                        if (!skip.contains(other.name)) {
                            // we know here that there is a duplicate
                            other.utterances.forEach { otherUtterance ->
                                // fetch the indices where the duplicate is
                                val intersection = otherUtterance.permutations.intersectionOf(utterance.permutations)
                                if (intersection.isNotEmpty()) {
                                    intersection.forEach {
                                        // print it out
                                        val sample = utterance.permutations.toArray()[it]
                                        L.error("Found a collision! Utterance \"$sample\" is generated by:")
                                        L.error("- ${self.name}: ${utterance.source}")
                                        L.error("- ${other.name}: ${otherUtterance.source}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Cli().parseArgs(args)
            } catch (e: java.lang.IllegalArgumentException) {
                L.error(e.message.orEmpty())
                exitProcess(-1)
            }
        }

        var L: LoggerFacade = DefaultLogger()
        const val amazonClientId = "amzn1.application-oa2-client.c57e86e21f464b0d8166b37ef867abd8"
        const val amazonClientSecret = "88f6586c4ff2519f6c129402a9d732e0a8baa7d375e29f80010796ac82f06a00"
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