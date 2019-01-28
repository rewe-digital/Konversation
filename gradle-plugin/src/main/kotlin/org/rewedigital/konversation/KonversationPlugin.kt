package org.rewedigital.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
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

        tasks.create("compileKonversation", CompileTask::class.java) { task ->
            val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
            val main = javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val srcDirs = try {
                main.resources.srcDirs
            } catch (e: Throwable) {
                null
            }
            val inputFiles = srcDirs?.flatMap { resDir ->
                resDir.listFiles { dir: File?, name: String? ->
                    File(dir, name).isDirectory && (name == "konversation" || name?.startsWith("konversation-") == true)
                }.toList()
                    .flatMap { it.listFiles { _, name -> name.endsWith(".kvs") }.toList() }
            } ?: emptyList()
            task.inputFiles += inputFiles
            //task.outputFiles += inputFiles.map { File(it.path.replace("\\.ksv$".toRegex(), ".kson")) }
        }
        tasks.create("exportAlexa", AlexaExportTask::class.java) { task ->

            val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
            val main = javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            val srcDirs = try {
                main.resources.srcDirs
            } catch (e: Throwable) {
                null
            }
            val inputFiles = srcDirs?.flatMap { resDir ->
                resDir.listFiles { dir: File?, name: String? ->
                    (File(dir, name).isDirectory && (name == "konversation" || name?.startsWith("konversation-") == true)) || (File(dir, name).isFile && name?.endsWith(".grammar") == true)
                }.toList()
                    .flatMap { it.listFiles { _, name -> name.endsWith(".kvs") }.toList() }
            } ?: emptyList()
            task.inputFiles += inputFiles
            //task.outputFiles += inputFiles.map { File(it.path.replace("\\.ksv$".toRegex(), ".kson")) }
        }
        //println("Tasks sind angelegt, AlteR!")

        afterEvaluate {
        }
    }
}

open class KonversationExtension(project: Project) {
    var cacheDir = project.buildDir.path + "/konversation-cache"
    var invocationName: String? = null
}

fun createLoggingFacade(logger: Logger) = object: LoggerFacade {
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

    @TaskAction
    fun compile() {
        val cli = Cli()
        val config = project.extensions.getByName("konversation") as? KonversationExtension
        org.rewedigital.konversation.parser.Utterance.cacheDir = config?.cacheDir ?: project.buildDir.path + "/konversation-cache"
        val op = progressLoggerFactory.newOperation(CompileTask::class.java)
        //op.loggingHeader = "header"
        op.description = "description"
        //org.gradle.api.logging.Logger().lifecycle("blah")
        //op.setShortDescription("description")
        //val foo = op.start("description", "description")
        LOGGER.debug("Hallo")
        op.started()
        inputFiles.forEach { file ->
            Thread.sleep(5000)
            op.progress("${op.description}: ${file.path}")
            LOGGER.debug("${op.description}: ${file.path}")
            cli.parseArgs(arrayOf("--export-kson", file.parent, file.path)) // TODO the result should be written to /build/resources/... File(project.buildDir, file.path).path))
        }
        Thread.sleep(3000)
        op.completed()
    }
}

@CacheableTask
open class AlexaExportTask : DefaultTask() {

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @TaskAction
    fun compile() {
        val cli = Cli()
        val config = project.extensions.getByName("konversation") as? KonversationExtension ?: throw IllegalStateException("The alexa export task required the konversation configuration to define the invocation name")
        if(config.invocationName.isNullOrBlank()) throw IllegalStateException("The alexa export task required the invocation name in the konversation configuration")

        Utterance.cacheDir = config.cacheDir
        inputFiles.forEach { file ->
            cli.parseArgs(arrayOf("-invocation", config.invocationName!!, "--export-alexa", file.parent, file.path))
        }
    }

}