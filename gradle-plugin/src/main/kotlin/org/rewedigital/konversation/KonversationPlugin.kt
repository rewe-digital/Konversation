package org.rewedigital.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.*
import org.gradle.internal.operations.DefaultBuildOperationIdFactory
import org.gradle.internal.time.Time
import org.gradle.tooling.internal.consumer.SynchronizedLogging
import org.rewedigital.konversation.parser.Utterance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

open class KonversationPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {

        extensions.create("konversation", KonversationExtension::class.java, project)

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
        }
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

open class KonversationExtension(project: Project) {
    var cacheDir = project.buildDir.path + "/konversation/cache"
    var alexaIntentSchemaFile = project.buildDir.path + "/konversation/alexa-intent-schema.json"
    var invocationName: String? = null
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
        config = project.extensions.getByName("konversation") as? KonversationExtension ?: KonversationExtension(project)
    }

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @OutputFile
    val outputFile = File(config.alexaIntentSchemaFile)

    @TaskAction
    fun exportAlexaIntentSchema() {
        val cli = Cli()
        if (config.invocationName.isNullOrBlank()) throw IllegalStateException("The alexa export task required the invocation name in the konversation configuration")

        LOGGER.debug("Processing files: " + inputFiles.map { it.absolutePath }.joinToString())

        Utterance.cacheDir = config.cacheDir
        cli.parseArgs((listOf("-invocation", config.invocationName!!, "--export-alexa", config.alexaIntentSchemaFile, "-prettyprint") + inputFiles.map { it.absolutePath }).toTypedArray())
    }
}