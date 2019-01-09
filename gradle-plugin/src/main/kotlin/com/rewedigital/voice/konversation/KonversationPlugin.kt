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
        //println("Tasks sind angelegt, AlteR!")

        afterEvaluate {
        }
    }
}

@CacheableTask
open class CompileTask : DefaultTask() {

    @InputFiles
    val inputFiles = mutableListOf(File(project.buildDir, "konversation-cache"))

    @TaskAction
    fun compile() {
        val cli = Cli()
        com.rewedigital.voice.konversation.parser.Utterance.cacheDir = project.buildDir.path + "/konversation-cache"
        inputFiles.forEach { file ->
            cli.parseArgs(arrayOf("--export-kson", file.parent, file.path))
        }
    }

}