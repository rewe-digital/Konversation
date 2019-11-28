@file:Suppress("UnstableApiUsage")

package org.rewedigital.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.AbstractTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.*
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.parser.Utterance
import org.rewedigital.konversation.tasks.CompileTask
import org.rewedigital.konversation.tasks.ExportTask
import org.rewedigital.konversation.tasks.UpdateTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

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

        val testRoot = File("")
        val outdir = File("")

        val projects: Map<String, KonversationProject> = mapOf(
            "Markt Demo" to KonversationProject(
                invocationNames = mutableMapOf(Locale.GERMANY to "rewe demo"),
                inputFiles = mutableListOf(File(testRoot, "market/market.grammar")),
                outputDirectory = outdir
            ).apply {
                inputFiles += File(testRoot, "market/").listFiles { _, name -> name.endsWith(".values") } ?: emptyArray()
                alexa.enabled = true
                alexa.inputFiles += File(testRoot, "market/market-alexa.grammar")
                dialogflow.enabled = true
                dialogflow.inputFiles += File(testRoot, "market/market-google.grammar")
                //},
                //"Shop Demo" to KonversationProject(
                //    invocationNames = mutableMapOf(Locale.GERMANY to "rewe shop demo"),
                //    inputFiles = mutableListOf(File(testRoot, "shop/shop.grammar")),
                //    outputDirectory = outdir
                //).apply {
                //    inputFiles += File(testRoot, "shop/").listFiles { _, name -> name.endsWith(".values") } ?: emptyArray()
                //    alexa.enabled = true
                //    alexa.inputFiles += File(testRoot, "shop/shop-alexa.grammar")
                //    dialogflow.enabled = true
                //    dialogflow.inputFiles += File(testRoot, "shop/shop-google.grammar")
            }
        )

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

abstract class AlexaTargetExtension(project: Project) : BasicConfig(project) {
    var token: String? = null
    var authorization: File? = null

    override fun toString() = "AlexaTargetExtension(outputDirectory=$outputDirectory, token=$token, authorization=$authorization)"
}

abstract class DialogflowTargetExtension(project: Project) : BasicConfig(project) {
    var outputDir = File(project.buildDir.path, "konversation")
    var authorization: File? = null

    override fun toString() = "DialogflowTargetExtension(outputDir=$outputDir, authorization=$authorization)"
}

fun createLoggingFacade(logger: Logger) = object : LoggerFacade {
    override fun log(msg: String) = logger.info(msg)
    override fun debug(msg: String) = logger.debug(msg)
    override fun info(msg: String) = logger.info(msg)
    override fun error(msg: String) = logger.error(msg)
    override fun warn(msg: String) = logger.warn(msg)
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
        check(!config?.invocationNames.isNullOrEmpty()) { "The alexa export task required the invocation name in the konversation configuration" }

        LOGGER.debug("Processing files: " + inputFiles.joinToString { it.absolutePath })

        Utterance.cacheDir = config?.cacheDir!!
        //cli.parseArgs((listOf("-invocation", config.invocationName!!, "--export-alexa", config.alexaIntentSchemaFile, "-prettyprint") + inputFiles.map { it.absolutePath }).toTypedArray())
    }
}

private fun AbstractTask.groupToKonversation(description: String) = apply {
    this.group = "Konversation"
    this.description = description
}

@Suppress("UNCHECKED_CAST")
fun <T> ExtensionAware.getExtension(name: String): T? = try {
    extensions.getByName(name) as? T
} catch (e: UnknownDomainObjectException) {
    null
}

@Suppress("UNCHECKED_CAST")
private fun <T> Project.getExtension(name: String): T = extensions.getByName(name) as T

val WorkAction<KonversationProjectParameters>.project
    get() = parameters.project.get()