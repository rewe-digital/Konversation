package com.rewedigital.voice.konversation

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import java.io.File

open class KonversationPlugin : Plugin<Project> {

    //private val logger = LoggerFactory.getLogger(KonversationPlugin::class.java)

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

@CacheableTask
open class CompileTask : DefaultTask() {

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @TaskAction
    fun compile() {
        val cli = Cli()
        val config = project.extensions.getByName("konversation") as? KonversationExtension
        com.rewedigital.voice.konversation.parser.Utterance.cacheDir = config?.cacheDir ?: project.buildDir.path + "/konversation-cache"
        inputFiles.forEach { file ->
            cli.parseArgs(arrayOf("--export-kson", file.parent, file.path))//File(project.buildDir, file.path).path))
        }
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

        com.rewedigital.voice.konversation.parser.Utterance.cacheDir = config.cacheDir
        inputFiles.forEach { file ->
            cli.parseArgs(arrayOf("-invocation", config.invocationName!!, "--export-alexa", file.parent, file.path))
        }
    }

}