@file:Suppress("UnstableApiUsage")

package org.rewedigital.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.AbstractTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.internal.operations.DefaultBuildOperationIdFactory
import org.gradle.internal.time.Time
import org.gradle.tooling.internal.consumer.SynchronizedLogging
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.parser.Utterance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

open class KonversationPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {

        val kvs = extensions.create("konversation", KonversationExtension::class.java, project) as ExtensionAware
        kvs.extensions.create("alexa", AlexaTargetExtension::class.java, project)
        kvs.extensions.create("dialogflow", DialogflowTargetExtension::class.java, project)

        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
        val inputDirs = mutableListOf<File>()
        javaConvention.sourceSets.forEach { sourceSet ->
            sourceSet.resources.srcDirs("build/konversation/res/${sourceSet.name}/")
            inputDirs += File(projectDir, "src/${sourceSet.name}/konversation")
        }

        val compile = tasks.create("compileKonversation", CompileTask::class.java) { task ->
            task.inputFiles += inputDirs.listFilesByExtension("kvs")
            task.outputDirectories += javaConvention.sourceSets.map { File(buildDir, "konversation/res/${it.name}") }
        }
        tasks.getByName("processResources").dependsOn += compile

        //tasks.create("exportAlexa", AlexaExportTask::class.java) { task ->
        //    task.inputFiles += inputDirs.listFilesByExtension("kvs", "grammar")
        //}.groupToKonversation()

        val projects: Map<String, KonversationProject> = mapOf()

        buildExportTaskTree(projects, tasks)
        buildUpdateTaskTree(projects, tasks)
    }

    private fun buildExportTaskTree(projects: Map<String, KonversationProject>, tasks: TaskContainer) {
        val exportAll = tasks.create("exportAll", DefaultTask::class.java).groupToKonversation("Export all intent models.")
        val exportAlexa = tasks.create("exportAlexa", DefaultTask::class.java).groupToKonversation("Export all Alexa intent models.")
        exportAll.dependsOn += exportAlexa
        val exportDialogflow = tasks.create("exportDialogflow", DefaultTask::class.java).groupToKonversation("Export all Dialogflow projects.")
        exportAll.dependsOn += exportDialogflow

        projects.forEach { (name, project) ->
            val gradleName = name.split(' ', '-').joinToString(separator = "") { word -> word.capitalize() }
            tasks.create("export$gradleName", DefaultTask::class.java).groupToKonversation("Export the $name project for all platforms.").also { exportProject ->
                if (project.alexa.enabled) {
                    val exportProjectOnAlexa = tasks.create("export${gradleName}ForAlexa", ExportTask::class.java) { task ->
                        task.config = project.copy(dialogflow = BasicPlatformConfig())
                    }.groupToKonversation("Export $name for Alexa.")
                    exportProject.dependsOn += exportProjectOnAlexa
                    exportAlexa.dependsOn += exportProjectOnAlexa
                }
                if (project.dialogflow.enabled) {
                    val exportProjectOnDialogflow = tasks.create("export${gradleName}ForDialogflow", ExportTask::class.java) { task ->
                        task.config = project.copy(alexa = BasicPlatformConfig())
                    }.groupToKonversation("Export $name for Dialogflow.")
                    exportProject.dependsOn += exportProjectOnDialogflow
                    exportAlexa.dependsOn += exportProjectOnDialogflow
                }
            }
        }
    }

    private fun buildUpdateTaskTree(projects: Map<String, KonversationProject>, tasks: TaskContainer) {
        val updateAll = tasks.create("updateAll", DefaultTask::class.java).groupToKonversation("Update all intent models.")
        val updateAlexa = tasks.create("updateAlexa", DefaultTask::class.java).groupToKonversation("Update all Alexa intent models.")
        updateAll.dependsOn += updateAlexa
        val updateDialogflow = tasks.create("updateDialogflow", DefaultTask::class.java).groupToKonversation("Update all Dialogflow projects.")
        updateAll.dependsOn += updateDialogflow

        projects.forEach { (name, project) ->
            val gradleName = name.split(' ', '-').joinToString(separator = "") { word -> word.capitalize() }
            tasks.create("update$gradleName", DefaultTask::class.java).groupToKonversation("Update the $name project on all platforms.").also { updateProject ->
                if (project.alexa.enabled) {
                    val updateProjectOnAlexa = tasks.create("update${gradleName}OnAlexa", UpdateTask::class.java) { task ->
                        task.config = project.copy(dialogflow = BasicPlatformConfig())
                    }.groupToKonversation("Update $name on Alexa.")
                    updateProject.dependsOn += updateProjectOnAlexa
                    updateAlexa.dependsOn += updateProjectOnAlexa
                }
                if (project.dialogflow.enabled) {
                    val updateProjectOnDialogflow = tasks.create("update${gradleName}OnDialogflow", UpdateTask::class.java) { task ->
                        task.config = project.copy(alexa = BasicPlatformConfig())
                    }.groupToKonversation("Update $name on Dialogflow.")
                    updateProject.dependsOn += updateProjectOnDialogflow
                    updateAlexa.dependsOn += updateProjectOnDialogflow
                }
            }
        }
    }
}

private fun Iterable<File>.listFilesByExtension(vararg extensions: String) =
    flatMap { dir ->
        if (dir.exists()) {
            dir.listFiles { _, name ->
                extensions.any { extension ->
                    name.endsWith(".$extension")
                }
            }?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }

abstract class KonversationExtension(project: Project) : BasicConfig(project) {
    var cacheDir = project.buildDir.path + "/konversation/cache"
    var alexaIntentSchemaFile = project.buildDir.path + "/konversation/alexa-intent-schema.json"
    val alexa: AlexaTargetExtension?
        get() = getExtension("alexa")
    val dialogflow: DialogflowTargetExtension?
        get() = getExtension("dialogflow")
    val projects = mutableMapOf<String, KonversationProject>()

    override fun toString() =
        "KonversationExtension(cacheDir='$cacheDir', alexaIntentSchemaFile='$alexaIntentSchemaFile', invocationName=$invocationName, invocationNames=$invocationNames, alexa=$alexa, dialogflow=$dialogflow. projects=$projects)"
}

data class KonversationProject(
    val alexa: BasicPlatformConfig = BasicPlatformConfig(),
    val dialogflow: BasicPlatformConfig = BasicPlatformConfig(),
    override var invocationName: String?,
    override var language: String?,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override val inputFiles: MutableList<File>,
    override var outputDirectory: File?) : VoiceAppConfig, java.io.Serializable {

    init {
        println("Invocation name result: ${invocationName?.isNotBlank() == true}")
        //require(!((invocationName == null && invocationNames.isEmpty()) ||
        //        (alexa.invocationName == null && alexa.invocationNames.isEmpty()) ||
        //        (dialogflow.invocationName == null && dialogflow.invocationNames.isEmpty()))) {
        //    "You must set ether the invocationName or at least one translation of invocationNames. You can set it in the root config per platform."
        //}
    }

    override fun toString() =
        "KonversationProject(alexa=$alexa, dialogflow=$dialogflow, invocationName=$invocationName, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}

data class BasicPlatformConfig(
    override var invocationName: String? = null,
    override var language: String? = null,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null,
    var enabled: Boolean = false
) : VoiceAppConfig, java.io.Serializable

interface VoiceAppConfig {
    var invocationName: String?
    var language: String?
    var invocationNames: MutableMap<String, String>
    val inputFiles: MutableList<File>
    var outputDirectory: File?
}

abstract class BasicConfig(project: Project) : ExtensionAware, VoiceAppConfig {
    override var invocationName: String? = null
    override var invocationNames = mutableMapOf<String, String>()
    @InputFiles
    override val inputFiles = mutableListOf<File>()
    @OutputDirectory
    override var outputDirectory: File? = File(project.buildDir.path, "konversation")
}

abstract class AlexaTargetExtension(project: Project) : BasicConfig(project) {
    var token: String? = null
    var authorization: File? = null

    override fun toString() = "AlexaTargetExtension(outputDirectory=$outputDirectory, invocationName=$invocationName, token=$token, authorization=$authorization)"
}

abstract class DialogflowTargetExtension(project: Project) : BasicConfig(project) {
    var outputDir = File(project.buildDir.path, "konversation")
    var authorization: File? = null

    override fun toString() = "DialogflowTargetExtension(outputDir=$outputDir, invocationName=$invocationName, authorization=$authorization)"
}

fun createLoggingFacade(logger: Logger) = object : LoggerFacade {
    override fun log(msg: String) = logger.info(msg)
    override fun debug(msg: String) = logger.debug(msg)
    override fun info(msg: String) = logger.info(msg)
    override fun error(msg: String) = logger.error(msg)
    override fun warn(msg: String) = logger.warn(msg)
}

@CacheableTask
open class CompileTask : DefaultTask() {

    private val progressLoggerFactory = SynchronizedLogging(Time.clock(), DefaultBuildOperationIdFactory()).progressLoggerFactory
    private val LOGGER = LoggerFactory.getLogger(CompileTask::class.java)

    init {
        Cli.L = createLoggingFacade(LOGGER)
    }

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @OutputDirectories
    val outputDirectories = mutableListOf<File>()

    @TaskAction
    fun compile() {
        val cli = Cli()
        val config = project.extensions.getByName("konversation") as? KonversationExtension
        Utterance.cacheDir = config?.cacheDir ?: project.buildDir.path + "/konversation/cache"
        val op = progressLoggerFactory.newOperation(CompileTask::class.java)
        //op.loggingHeader = "header"
        op.description = "description"
        //org.gradle.api.logging.Logger().lifecycle("blah")
        //op.setShortDescription("description")
        //val foo = op.start("description", "description")
        LOGGER.info("Processing ${inputFiles.size} files...")
        op.started()
        inputFiles.forEach { file ->
            //Thread.sleep(5000)
            op.progress("${op.description}: ${file.path}")
            LOGGER.debug("${op.description}: ${file.path}")
            val start = file.path.indexOf("src" + File.separator) + 4
            val end = file.path.indexOf(File.separatorChar, start)
            val sourceSet = file.path.substring(start, end)
            cli.parseArgs(arrayOf("--export-kson", project.buildDir.path + "/konversation/res/$sourceSet/", file.path))
        }
        //Thread.sleep(3000)
        op.completed()
    }
}

@CacheableTask
open class AlexaExportTask : DefaultTask() {
    private val LOGGER = LoggerFactory.getLogger(AlexaExportTask::class.java)
    private val config: KonversationExtension?

    init {
        Cli.L = createLoggingFacade(LOGGER)
        config = getExtension("konversation")
    }

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @OutputFile
    val outputFile = File(config?.alexaIntentSchemaFile.orEmpty())

    @TaskAction
    fun exportAlexaIntentSchema() {
        val cli = Cli()
        check(!config?.invocationName.isNullOrBlank()) { "The alexa export task required the invocation name in the konversation configuration" }

        LOGGER.debug("Processing files: " + inputFiles.joinToString { it.absolutePath })

        Utterance.cacheDir = config?.cacheDir!!
        cli.parseArgs((listOf("-invocation", config.invocationName!!, "--export-alexa", config.alexaIntentSchemaFile, "-prettyprint") + inputFiles.map { it.absolutePath }).toTypedArray())
    }
}

interface KonversationProjectParameters : WorkParameters {
    val project: Property<KonversationProject>
}

abstract class UpdateAlexaAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        val api = KonversationApi("", "")
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa.inputFiles
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationName ?: project.alexa.invocationName ?: project.invocationNames.values.firstOrNull() ?: project.alexa.invocationNames.values.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invationname not found")
        val tmp = createTempDir(prefix = "alexa-", directory = File(""))
        println("Updating to $tmp")
        api.exportAlexaSchema(File(tmp, "schema.json"), api.invocationName.orEmpty())
        //Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}

abstract class UpdateDialogflowAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        println("Deploying ${parameters.project.get()}")
        Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}

abstract class ExportAlexaAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        println("Deploying ${parameters.project.get()}")
        Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}

abstract class ExportDialogflowAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        println("Deploying ${parameters.project.get()}")
        Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}

open class ExportTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var config: KonversationProject? = null

    @TaskAction
    fun provision() = when {
        config?.dialogflow?.enabled == true -> workerExecutor.noIsolation().submit(ExportDialogflowAction::class.java) {
            it.project.set(config)
        }
        config?.alexa?.enabled == true -> workerExecutor.noIsolation().submit(ExportAlexaAction::class.java) {
            it.project.set(config)
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }
}

open class UpdateTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var config: KonversationProject? = null

    @TaskAction
    fun provision() = when {
        config?.dialogflow?.enabled == true -> workerExecutor.noIsolation().submit(UpdateDialogflowAction::class.java) {
            it.project.set(config)
        }
        config?.alexa?.enabled == true -> workerExecutor.noIsolation().submit(UpdateAlexaAction::class.java) {
            it.project.set(config)
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }
}

private fun AbstractTask.groupToKonversation(description: String) = apply {
    this.group = "Konversation"
    this.description = description
}

@Suppress("UNCHECKED_CAST")
private fun <T> ExtensionAware.getExtension(name: String): T? = try {
    extensions.getByName(name) as? T
} catch (e: UnknownDomainObjectException) {
    null
}

@Suppress("UNCHECKED_CAST")
private fun <T> Project.getExtension(name: String): T = extensions.getByName(name) as T

private val WorkAction<KonversationProjectParameters>.project
    get() = parameters.project.get()