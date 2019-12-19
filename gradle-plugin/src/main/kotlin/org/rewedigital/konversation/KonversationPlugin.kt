@file:Suppress("UnstableApiUsage")

package org.rewedigital.konversation

import com.charleskorn.kaml.Yaml
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.AbstractTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.config.KonversationConfig
import org.rewedigital.konversation.tasks.CompileTask
import org.rewedigital.konversation.tasks.ExportTask
import org.rewedigital.konversation.tasks.UpdateTask
import org.slf4j.Logger
import java.io.File

open class KonversationPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {

        val kvs = extensions.create("konversation", KonversationExtension::class.java, project) as ExtensionAware
        val projectContainer = project.container(GradleProject::class.java) { name -> GradleProject(name) }
        kvs.extensions.add("projects", projectContainer)

        project.plugins.withId("kotlin") {
            val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
            val inputDirs = mutableListOf<File>()
            javaConvention.sourceSets.forEach { sourceSet ->
                sourceSet.allJava.srcDirs("build/konversation/gen/")
                sourceSet.resources.srcDirs("build/konversation/res/${sourceSet.name}/")
                inputDirs += File(projectDir, "src/${sourceSet.name}/konversation")
            }

            val compile = tasks.create("compileKonversation", CompileTask::class.java) { task ->
                task.inputFiles += inputDirs.listFilesByExtension("kvs")
                task.outputDirectories += javaConvention.sourceSets.map { File(buildDir, "konversation/res/${it.name}") }
            }
            tasks.getByName("processResources").dependsOn += compile
        }

        val outdir = File("")
        val config = Yaml.default.parse(KonversationConfig.serializer(), File(outdir, "konversation.yaml").readText())

        val exportAll = tasks.create("exportAll", DefaultTask::class.java).groupToKonversation("Export all intent models.")
        val exportAlexa = tasks.create("exportAlexa", DefaultTask::class.java).groupToKonversation("Export all Alexa intent models.")
        exportAll.dependsOn += exportAlexa
        val exportDialogflow = tasks.create("exportDialogflow", DefaultTask::class.java).groupToKonversation("Export all Dialogflow projects.")
        exportAll.dependsOn += exportDialogflow
        val updateAll = tasks.create("updateAll", DefaultTask::class.java).groupToKonversation("Update all intent models.")
        val updateAlexa = tasks.create("updateAlexa", DefaultTask::class.java).groupToKonversation("Update all Alexa intent models.")
        updateAll.dependsOn += updateAlexa
        val updateDialogflow = tasks.create("updateDialogflow", DefaultTask::class.java).groupToKonversation("Update all Dialogflow projects.")
        updateAll.dependsOn += updateDialogflow

        projectContainer.all { project ->
            project.fillWith(config)
            val gradleName = project.name.split(' ', '-').joinToString(separator = "") { word -> word.capitalize() }
            tasks.create("export$gradleName", DefaultTask::class.java).groupToKonversation("Export the ${project.name} project for all platforms.").also { exportProject ->
                project.alexa?.let {
                    val exportProjectOnAlexa = tasks.create("export${gradleName}ForAlexa", ExportTask::class.java) { task ->
                        task.config = project.copy(dialogflow = null)
                    }.groupToKonversation("Export ${project.name} for Alexa.")
                    exportProject.dependsOn += exportProjectOnAlexa
                    exportAlexa.dependsOn += exportProjectOnAlexa
                }
                project.dialogflow?.let {
                    val exportProjectOnDialogflow = tasks.create("export${gradleName}ForDialogflow", ExportTask::class.java) { task ->
                        task.config = project.copy(alexa = null, outputDirectory = project.outputDirectory ?: File(buildDir, "konversation/intent-schemas/"))
                    }.groupToKonversation("Export ${project.name} for Dialogflow.")
                    exportProject.dependsOn += exportProjectOnDialogflow
                    exportAlexa.dependsOn += exportProjectOnDialogflow
                }
            }
            tasks.create("update$gradleName", DefaultTask::class.java).groupToKonversation("Update the ${project.name} project on all platforms.").also { updateProject ->
                project.alexa?.let {
                    val updateProjectOnAlexa = tasks.create("update${gradleName}OnAlexa", UpdateTask::class.java) { task ->
                        task.config = project.copy(dialogflow = null)
                    }.groupToKonversation("Update ${project.name} on Alexa.")
                    updateProject.dependsOn += updateProjectOnAlexa
                    updateAlexa.dependsOn += updateProjectOnAlexa
                }
                project.dialogflow?.let {
                    val updateProjectOnDialogflow = tasks.create("update${gradleName}OnDialogflow", UpdateTask::class.java) { task ->
                        task.config = project.copy(alexa = null)
                    }.groupToKonversation("Update ${project.name} on Dialogflow.")
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

fun createLoggingFacade(logger: Logger) = object : LoggerFacade {
    override fun log(msg: String) = logger.info(msg)
    override fun debug(msg: String) = logger.debug(msg)
    override fun info(msg: String) = logger.info(msg)
    override fun error(msg: String) = logger.error(msg)
    override fun warn(msg: String) = logger.warn(msg)
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