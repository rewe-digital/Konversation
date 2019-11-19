@file:Suppress("UnstableApiUsage")

package org.rewedigital.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        tasks.create("exportAlexa", AlexaExportTask::class.java) { task ->
            task.inputFiles += inputDirs.listFilesByExtension("kvs", "grammar")
        }.groupToKonversation()
        tasks.create("provisioningAll", DefaultTask::class.java).groupToKonversation().dependsOn.addAll(
            listOf("foo", "bar").map { project ->
                tasks.create("provisioning${project.capitalize()}", DefaultTask::class.java).groupToKonversation().also {
                    it.dependsOn.addAll(
                        TargetPlatform.values().map { platform ->
                            tasks.create("provisioning${project.capitalize()}On${platform.name.capitalize()}", ProvisioningTask::class.java) { task ->
                                task.projectName = project
                                task.target = platform
                            }.groupToKonversation()
                        })
                }
            })
        tasks.getByName("processResources").dependsOn += compile
    }
}

private fun Iterable<File>.listFilesByExtension(vararg extensions: String) =
    flatMap { dir ->
        if (dir.exists()) {
            dir.listFiles { _, name ->
                extensions.any { extension ->
                    name.endsWith(".$extension")
                }
            }.toList()
        } else {
            emptyList()
        }
    }

abstract class KonversationExtension(project: Project) : BasicConfig(project) {
    var cacheDir = project.buildDir.path + "/konversation/cache"
    var alexaIntentSchemaFile = project.buildDir.path + "/konversation/alexa-intent-schema.json"
    val alexa: AlexaTargetExtension
        get() = getExtension("alexa")
    val dialogflow: DialogflowTargetExtension
        get() = getExtension("dialogflow")

    override fun toString() =
        "KonversationExtension(cacheDir='$cacheDir', alexaIntentSchemaFile='$alexaIntentSchemaFile', invocationName=$invocationName, invocationNames=$invocationNames, alexa=$alexa, dialogflow=$dialogflow)"
}

abstract class BasicConfig(project: Project) : ExtensionAware {
    var invocationName: String? = null
    var invocationNames = mutableMapOf<String, String>()
    @InputFiles
    val inputFiles = mutableListOf<File>()
    @OutputDirectory
    var outputDirectory = File(project.buildDir.path, "konversation")
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
    private val config: KonversationExtension

    init {
        Cli.L = createLoggingFacade(LOGGER)
        config = getExtension("konversation")
    }

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @OutputFile
    val outputFile = File(config.alexaIntentSchemaFile)

    @TaskAction
    fun exportAlexaIntentSchema() {
        val cli = Cli()
        check(!config.invocationName.isNullOrBlank()) { "The alexa export task required the invocation name in the konversation configuration" }

        LOGGER.debug("Processing files: " + inputFiles.joinToString { it.absolutePath })

        Utterance.cacheDir = config.cacheDir
        cli.parseArgs((listOf("-invocation", config.invocationName!!, "--export-alexa", config.alexaIntentSchemaFile, "-prettyprint") + inputFiles.map { it.absolutePath }).toTypedArray())
    }
}

interface ProvisioningParameters : WorkParameters {
    val projectName: Property<String>
    val platform: Property<TargetPlatform>
}

abstract class ProvisioningAction : WorkAction<ProvisioningParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        println("Deploying ${parameters.projectName.get()} on ${parameters.platform.get()}")
        Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}

enum class TargetPlatform {
    Alexa,
    Dialogflow
}

open class ProvisioningTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    //private val config = project.extensions.getByName("konversation") as KonversationExtension
    var target: TargetPlatform? = null
    var projectName: String? = null

    @TaskAction
    fun provision() {

        val config = project.getExtension<KonversationExtension>("konversation")
        println("Debug:")
        println(config.invocationNames)
        println(config.alexa)
        println(config.dialogflow)

        require(!(projectName == null || target == null)) { "config error project name or target not set" }
        workerExecutor.noIsolation().submit(ProvisioningAction::class.java) {
            //it.config.set(config)
            it.projectName.set(projectName)
            it.platform.set(target)
        }
    }
}

private fun AbstractTask.groupToKonversation() = apply {
    group = "Konversation"
    description = "Collection of tasks to the export and deployment of Alexa and Dialogflow projects."
}

@Suppress("UNCHECKED_CAST")
private fun <T> ExtensionAware.getExtension(name: String): T = extensions.getByName(name) as T

@Suppress("UNCHECKED_CAST")
private fun <T> Project.getExtension(name: String): T = extensions.getByName(name) as T