package org.rewedigital.konversation

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.serialization.json.Json
import org.rewedigital.konversation.config.AlexaProject
import org.rewedigital.konversation.config.KonversationConfig
import org.rewedigital.konversation.config.KonversationProject
import org.rewedigital.konversation.config.ask.AskCliConfig
import org.rewedigital.konversation.config.cheapDecrypt
import org.rewedigital.konversation.generator.alexa.models.Skill
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream
import kotlin.system.exitProcess

open class Cli(
    private val settings: KonversationConfig,
    private val api: KonversationApi) {
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
    private var enumFileNamespace: String? = null

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

    fun parseArgs(args: Array<String>) {
        if (args.isEmpty()) {
            L.error("Missing arguments! Please specify at least the kvs or grammar file you want to process.")
            L.error()
            help()
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
                        "--export-enum" -> if (++argNo < args.size) {
                            enumFileNamespace = args[argNo]
                        } else {
                            throw IllegalArgumentException("No package name defined")
                        }
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
                            project = createProject(settings).also { newProject ->
                                settings.projects[newProject.invocations.values.first()] = newProject
                            }
                            println(Yaml(configuration = YamlConfiguration(encodeDefaults = false)).stringify(KonversationConfig.serializer(), settings))
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
                api.exportDialogflow(File(outDir, "$projectName.zip"), prettyPrint)
            }
            if (exportKson) {
                api.exportKson(outDir, prettyPrint)
            }
            if (exportDump) {
                val outputDirectory = projectName?.let { name ->
                    File(outDir, name)
                } ?: outDir
                api.exportPlain(outputDirectory)
            }
            enumFileNamespace?.let { namespace ->
                api.exportEnum(outDir, namespace)
            }
            if (updateDialogflow) {
                api.invocationName = projectData.dialogflowInvocations.values.first()
                println("Uploading ${api.invocationName} to Dialogflow...")
                val projectId = requireNotNull(projectData.dialogflow?.projectId) { "No project id set" }
                api.updateDialogflowProject(projectId)
            }
            if (updateAlexa) {
                api.invocationName = projectData.alexaInvocations.values.first()
                println("Uploading ${api.invocationName} to Alexa...")
                api.updateAlexaSchema(requireNotNull(projectData.alexa?.skillId) { "No skill id set" })
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
        val askCliVendorId = importFromAskCli()
        if (!api.alexa.isLoggedIn) {
            print("Do you want to connect your Amazon account for the Alexa setup? (yes/no): ")
            if (ask() == true && api.authorizeAlexa(21337)) {
                println("Login successful!")
            }
        }
        return if (api.alexa.isLoggedIn) {
            val skill = loadSkillData(askCliVendorId)
            skill?.let {
                if (settings.auth.alexaClientId == api.alexa.clientId) {
                    KonversationProject(alexa = AlexaProject(skill.skillId), invocations = skill.nameByLocale.toMutableMap())
                } else {
                    KonversationProject(alexa = AlexaProject(skill.skillId, api.alexa.refreshToken, api.alexa.clientId, api.alexa.clientSecret), invocations = skill.nameByLocale.toMutableMap())
                }
            } ?: throw java.lang.IllegalStateException("No skill selected")
        } else {
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
            KonversationProject(invocations = generalInvocations)
        }
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
                i?.let { i-- }
                break
            }
        }
        return i
    }

    private fun importFromAskCli(): String? =
        if (settings.auth.alexaRefreshToken == null) {
            val home = System.getProperty("user.home")
            val askConfigFile = File("$home${File.separator}.ask${File.separator}cli_config")
            if (askConfigFile.exists()) {
                print("ASK CLI detected, do you want to use the already saved credentials? (yes/no): ")
                if (ask() == true) {
                    api.amazonClientId = AskCliConfig.clientId
                    api.amazonClientSecret = AskCliConfig.clientSecret
                    val profiles = Json.nonstrict.parse(AskCliConfig.serializer(), askConfigFile.readText()).profiles
                    profiles.keys.forEachIndexed { i, profile ->
                        println("${i + 1}) $profile")
                    }
                    askForIndex("Please choose your ask cli profile: ", profiles.size)?.let { index ->
                        val profile = profiles.values.toList()[index]
                        api.alexa.loadToken(profile.token.refresh_token)
                        if (!api.alexa.isLoggedIn) {
                            throw IllegalStateException("Login failed!")
                        }
                        return profile.vendor_id
                    }
                }
            }
            null
        } else null

    private fun chooseVendor(): String? {
        print("Do you want to import an Alexa skill? (yes/no): ")
        return if (ask() == true) {
            val vendors = api.alexa.fetchVendors()
            when {
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
                        vendors.getOrNull(index)?.id
                    }
                }
            }
        } else null
    }

    private fun loadSkillData(defaultVendorId: String?) =
        (defaultVendorId ?: chooseVendor())?.let { vendorId ->
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
                sortedSkills.getOrNull(index)
            }
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
            "[-p|--project] <project>" to "tba",
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
        var L: LoggerFacade = DefaultLogger()
        const val version = "2.0.0-beta1"
        val exposeToken = java.lang.Boolean.parseBoolean("true")

        @JvmStatic
        fun main(args: Array<String>) {
            val amazonClientId by lazy { "Zmd3fiI4eGxvTkxLSlpYW1kXUiFxayogJjc7LHU9VFMCUlsVQUcfSEnCtsOnwrjDr8K2wqDCosKhw7jCrsKXw4bDgMKRwprCmMOTw5fDnMKD".cheapDecrypt() }
            val amazonClientSecret by lazy { "PzJrJiYuL38rREMaHh8IUgFZDHJ6cnl+Lmsxb2hsBFQGUg8REkEdT0jCt8OgwrrCssOowqnCpMKnwqvCrcKXwprCkMOIw4/Cl8KAw5PCiMKNw5/DscO0".cheapDecrypt() }
            val settings: KonversationConfig
            val api: KonversationApi
            val settingsFile = searchFile(File(".").absoluteFile.parentFile, "konversation.yaml")
            api = if (settingsFile?.exists() == true) {
                settings = Yaml.default.parse(KonversationConfig.serializer(), settingsFile.readText())
                KonversationApi(
                    settings.auth.alexaClientId ?: amazonClientId,
                    settings.auth.alexaClientSecret ?: amazonClientSecret,
                    settings.auth.dialogflowServiceAccount).also { thisApi ->
                    settings.auth.alexaRefreshToken?.let { token ->
                        thisApi.alexa.loadToken(token)
                    }
                }
            } else {
                settings = KonversationConfig()
                KonversationApi(amazonClientId, amazonClientSecret)
            }
            api.logger = L
            try {
                Cli(settings, api).parseArgs(args)
            } catch (e: java.lang.IllegalArgumentException) {
                L.error(e.message.orEmpty())
                exitProcess(-1)
            }
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