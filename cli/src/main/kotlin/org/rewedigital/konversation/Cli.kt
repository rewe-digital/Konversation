package org.rewedigital.konversation

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import org.rewedigital.konversation.config.AlexaProject
import org.rewedigital.konversation.config.Auth
import org.rewedigital.konversation.config.KonversationConfig
import org.rewedigital.konversation.config.KonversationProject
import org.rewedigital.konversation.generator.alexa.models.Skill
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

open class Cli {
    private val settings: KonversationConfig
    private val api: KonversationApi
    private var countPermutations = false
    private var stats = false
    private var prettyPrint = false
    private var inspect = false
    private var project: KonversationProject? = null
    private var projectName: String? = null
    private val projectData
        get() = requireNotNull(project) { "No project defined" }
    private var updateAlexa = false
    private var updateDialogflow = false
    private var exportAlexa = false
    private var exportDialogflow = false
    private var exportDump = false
    private var exportKson = false
    private var exposeDialogflowToken = false
    private var exposeAlexaToken = false
    private var outDir = File(".").absoluteFile.parentFile

    suspend inline fun <reified T> HttpClient.getOrNull(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T? = try {
        get {
            url.takeFrom(urlString)
            block()
        }
    } catch (_: Exception) {
        null
    }

    init {
        val settingsFile = searchFile(File(".").absoluteFile.parentFile, "konversation.yaml")
        api = if (settingsFile?.exists() == true) {
            settings = Yaml.default.parse(KonversationConfig.serializer(), settingsFile.readText())
            KonversationApi(
                settings.config.alexaClientId ?: amazonClientId,
                settings.config.alexaClientSecret ?: amazonClientSecret,
                settings.config.dialogflowServiceAccount).also { api ->
                settings.config.alexaRefreshToken?.let { token ->
                    api.alexa.loadToken(token)
                }
            }
        } else {
            settings = KonversationConfig()
            KonversationApi(amazonClientId, amazonClientSecret)
        }
        api.logger = L
    }

    private fun searchFile(workDir: File, fileName: String): File? {
        val possibleFile = File(workDir, fileName)
        return when {
            possibleFile.exists() ->
                possibleFile
            workDir.parentFile != null && workDir.parentFile.absolutePath != workDir.absolutePath ->
                searchFile(workDir.parentFile, fileName)
            else -> null
        }
    }

    fun parseArgs(args: Array<String>) {
        if (args.isEmpty()) {
            L.error("Missing arguments! Please specify at least the kvs or grammar file you want to process.")
            L.error()
            help()
            exit(-1)
        } else {
            if (settings.projects.size == 1) {
                println("Using default project ${settings.projects.keys.first()}.")
                project = settings.projects.values.first()
            }
            var argNo = 0
            while (argNo < args.size) {
                val arg = args[argNo]
                val argFile = File(arg)
                if (argFile.absoluteFile.exists()) {
                    api.inputFiles += File(arg).absoluteFile
                } else if (arg.endsWith(".kvs") || arg.endsWith(".grammar") || arg.endsWith(".values")) {
                    if (arg.contains('*') && argFile.parentFile.exists()) {
                        val matcher = argFile.name.replace(".", "\\.").replace("*", ".*?").toRegex()
                        argFile.parentFile.listFiles { _, name ->
                            matcher.matches(name)
                        }.orEmpty().map { file ->
                            api.inputFiles += file.absoluteFile
                        }
                    } else {
                        throw IllegalArgumentException("Input file \"$arg\" not found!")
                    }
                } else {
                    when (arg.toLowerCase()) {
                        "-help",
                        "-h",
                        "/?" -> {
                            help()
                            return
                        }
                        "-count" -> countPermutations = true
                        "--export-alexa" -> exportAlexa = true
                        "--export-dialogflow" -> exportDialogflow = true
                        "--export-kson" -> exportKson = true
                        "--update-dialogflow" -> updateDialogflow = true
                        "--update-alexa" -> updateAlexa = true
                        "--alexa-token",
                        "--show-alexa-token" -> if (exposeToken) {
                            exposeAlexaToken = true
                        }
                        "--dialogflow-token",
                        "--show-dialogflow-token" -> if (exposeToken) {
                            exposeDialogflowToken = true
                        }
                        "-p",
                        "--project" -> if (++argNo < args.size) {
                            projectName = args[argNo]
                            project = settings.projects[args[argNo]] ?: throw IllegalArgumentException("No configuration for project $projectName found.")
                        } else {
                            throw IllegalArgumentException("Project name is missing")
                        }
                        "--show-projects",
                        "--list-projects",
                        "--projects" -> logProjects()
                        "--create-project" -> {
                            project = createProject(settings)
                        }
                        "-stats" -> stats = true
                        "-prettyprint" -> prettyPrint = true
                        "-dump" -> exportDump = true
                        "-v",
                        "-version" -> L.log("Konversation CLI version $version")
                        // TODO allow changing the output dir and the output filename
                        else -> throw IllegalArgumentException("Unknown argument \"$arg\".")
                    }
                }
                argNo++
            }

            showStats(api)

            lookForCollisions(api.intentDb)

            if (exportAlexa) {
                api.invocationName = projectData.alexaInvocations.values.first()
                api.exportAlexaSchema(File(outDir, "$projectName.json"), prettyPrint)
            }
            if (exportDialogflow) {
                api.invocationName = projectData.dialogflowInvocations.values.first()
                api.exportDialogflow(outDir, prettyPrint)
            }
            if (exportKson) {
                api.exportKson(outDir, prettyPrint)
            }
            if (updateDialogflow) {
                val invocationName = projectData.dialogflowInvocations.values.first()
                println("Uploading $invocationName to Dialogflow...")
                val projectId = requireNotNull(projectData.dialogflow?.projectId) { "No project id set" }
                api.updateDialogflowProject(projectId, invocationName)
            }
            if (updateAlexa) {
                val invocationName = projectData.alexaInvocations.values.first()
                println("Uploading $invocationName to Alexa...")
                api.updateAlexaSchema(invocationName, requireNotNull(projectData.alexa?.skillId) { "No skill id set" })
                println("Done")
            }
            if (exposeAlexaToken) {
                println("Alexa Token: " + api.alexa.accessToken)
            }
            if (exposeDialogflowToken) {
                println("Dialogflow Token: " + api.dialogflow.accessToken)
            }
        }
    }

    private fun createProject(settings: KonversationConfig): KonversationProject {
        if (settings.config.alexaRefreshToken == null) {
            print("Do you want to connect your Amazon account for the Alexa setup? (yes/no): ")
            if (ask() == true && api.authorizeAlexa(21337)) {
                println("Login successful!")
            }
        }
        if (api.alexa.isLoggedIn) {
            print("Do you want to import an Alexa skill? (yes/no): ")
            if (ask() == true) {
                val vendors = api.alexa.fetchVendors()
                val vendorId = when {
                    vendors == null -> {
                        println("Failed to fetch vendors.")
                        null
                    }
                    vendors.size == 1 -> {
                        println("Using vendor ${vendors.first().name}")
                        vendors.first().id
                    }
                    else -> {
                        println("WARNING: Your account has access to multiple accounts, please consider using an account with just the required vendors.")
                        println("Choose from your vendors:")
                        vendors.forEachIndexed { i, vendor ->
                            println("${i + 1}) ${vendor.name}")
                        }
                        askForIndex("Please select the vendor you want to use: ", vendors.size)?.let { index ->
                            vendors.getOrNull(index - 1)?.id
                        }
                    }
                }
                val skill = vendorId?.let {
                    val skills = api.alexa.fetchSkills(vendorId)
                    val publishedSkills = skills.orEmpty().filter { it.stage == "live" }.map { it.skillId }
                    val sortedSkills = skills.orEmpty().filter {
                        !(it.skillId in publishedSkills && it.stage == "development")
                    }.sortedBy {
                        it.name
                    }.sortedByDescending {
                        it.stage
                    }
                    sortedSkills.forEachIndexed { i, skill ->
                        println("${i + 1}) ${skill.name} on Stage ${skill.stage} (${skill.skillId})")
                    }
                    askForIndex("Please select the skill you want to import: ", sortedSkills.size)?.let { index ->
                        sortedSkills.getOrNull(index - 1)
                    }
                }
                skill?.let {
                    println("Importing $skill...")
                    val test = KonversationConfig(Auth(api.alexa.refreshToken, api.alexa.clientId, api.alexa.clientSecret))
                    test.projects[skill.name] = KonversationProject(alexa = AlexaProject(skill.skillId), invocations = skill.nameByLocale.toMutableMap())
                    println(Yaml(configuration = YamlConfiguration(encodeDefaults = false)).stringify(KonversationConfig.serializer(), test))
                }
            }
        }
        var name: String?
        do {
            print("Project name: ")
            name = readLine()
            if (name == null) {
                println("Something went wrong")
                break
            }
        } while (name?.isNotBlank() == false)
        val generalInvocations = readInvocations()

        return KonversationProject()
    }

    private fun askForIndex(request: String, maxIndex: Int): Int? {
        var i: Int? = null
        while (true) {
            print(request)
            val input = readLine()
            if (input == null) {
                println("Something went wrong")
                break
            }
            i = input.toIntOrNull()
            if (i in 1..maxIndex) {
                break
            }
        }
        return i
    }

    private val Skill.name
        get() = nameByLocale.getOrElse("de-DE") { nameByLocale.values.first() }

    private fun ask() =
        when (readLine()?.toLowerCase()) {
            "y", "yes" -> true
            "n", "no" -> false
            else -> null
        }

    private fun readInvocations(): MutableMap<String, String> {
        var firstLocale = true
        val translations = mutableMapOf<String, String>()
        println("Invocation name setup")
        while (true) {
            var locale: String?
            do {
                if (firstLocale) {
                    print("Language: ")
                } else {
                    print("Language (keep empty to skip more translations): ")
                }
                locale = readLine()
                if (locale == null) {
                    println("Something went wrong")
                    break
                }
            } while (firstLocale && locale?.isNotBlank() == false)
            var invocation: String? = null
            while (invocation.isNullOrBlank() && !locale.isNullOrBlank()) {
                print("Invocation ($locale): ")
                invocation = readLine()
                if (invocation == null) {
                    println("Something went wrong")
                    break
                }
            }
            if (locale?.isBlank() == true) break
            translations[locale!!] = invocation!!
            //println("Accepted $locale: $invocation")
            firstLocale = false
        }
        return translations
    }

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
        val commands = mapOf(
            "[-help]" to "Print this help",
            "[-version]" to "Print the version of this build",
            "[-count]" to "Count the permutations and print this to the console",
            "[-stats]" to "Print out some statistics while generation",
            "[--export-alexa]" to "Write the resulting json to OUTFILE instead of result.json",
            "[--export-dialogflow]" to "Write the dialogflow zip file to the OUTDIR",
            "[--export-kson]" to "Compiles the kvs file to kson resource files which are required for the runtime",
            "[--update-alexa]" to "tba",
            "[--update-dialogflow]" to "tba",
            "[-p|--project]" to "tba",
            "[--create-project]" to "tba",
            "[--show-projects]" to "tba",
            "[--show-alexa-token]" to "tba",
            "[--show-dialogflow-token]" to "tba",
            "[-dump]" to "Dump out all intents to its own txt file",
            "[-prettyprint]" to "Generate a well formatted json for easier debugging",
            "<FILE>" to "The grammar, kvs or values files to parse"
        )
        var space = 0
        commands.keys.forEach { key ->
            space = space.coerceAtLeast(key.length + 1)
        }
        commands.forEach { (key, value) ->
            L.log("$key${" ".repeat(space - key.length)}$value")
        }
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

    private fun logProjects() =
        settings.projectsWithDefaults.forEach { (name, config) ->
            println(name)
            println("  Alexa:")
            when {
                config.alexa?.invocations.isNullOrEmpty() -> println("    Invocation: (no invocation set)")
                config.alexa?.invocations?.size == 1 -> {
                    config.alexa?.invocations?.entries?.first()?.let { (lang, invocation) ->
                        println("    Invocation ($lang): $invocation")
                    }
                }
                else -> {
                    println("    Invocation:")
                    config.alexa?.invocations?.forEach { (lang, invocation) ->
                        println("      $lang: $invocation")
                    }
                }
            }
            println("    SkillID: ${config.alexa?.skillId}")

            println("  Dialogflow:")
            when {
                config.dialogflow?.invocations.isNullOrEmpty() -> println("    Invocation: (no invocation set)")
                config.dialogflow?.invocations?.size == 1 -> {
                    config.dialogflow?.invocations?.entries?.first()?.let { (lang, invocation) ->
                        println("    Invocation ($lang): $invocation")
                    }
                }
                else -> {
                    println("    Invocation:")
                    config.dialogflow?.invocations?.forEach { (lang, invocation) ->
                        println("      $lang: $invocation")
                    }
                }
            }
            println("    ProjectID: ${config.dialogflow?.projectId}")
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
        const val version = "1.2.0-beta1"
        const val amazonClientId = "amzn1.application-oa2-client.c57e86e21f464b0d8166b37ef867abd8"
        const val amazonClientSecret = "88f6586c4ff2519f6c129402a9d732e0a8baa7d375e29f80010796ac82f06a00"
        const val exposeToken = false
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