@file:Suppress("UnstableApiUsage")

package org.rewedigital.konversation

import com.google.gson.Gson
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.AbstractTask
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.config.KonversationConfig
import org.rewedigital.konversation.tasks.*
import org.slf4j.Logger
import java.io.File

open class KonversationPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {

        val kvs = extensions.create("konversation", KonversationExtension::class.java, project)
        val projectContainer = container(GradleProject::class.java) { name -> GradleProject(name) }
        kvs.extensions.add("projects", projectContainer)

        plugins.withId("kotlin") {
            val javaConvention = convention.getPlugin(JavaPluginConvention::class.java)
            kvs.sourceSets = javaConvention.sourceSets.map { sourceSet ->
                File(project.projectDir, "src/${sourceSet.name}/konversation")
            }

            val exportKson = tasks.create("exportKson", ExportKsonTask::class.java) { task ->
                task.settings = kvs
                task.outputDirectory = kvs.ksonDir?.let(::File)
            }
            val exportEnum = tasks.create("exportKonversationEnum", ExportKonversationEnumTask::class.java) { task ->
                task.settings = kvs
                task.outputDirectory = kvs.enumTargetDir
                task.enumPackageName = kvs.enumPackageName
            }

            gradle.projectsEvaluated {
                if (kvs.generateEnum) {
                    tasks.getByName("compileKotlin").dependsOn += exportEnum
                }
                if (kvs.generateKson) {
                    tasks.getByName("compileKotlin").dependsOn += exportKson
                }
                javaConvention.sourceSets.forEach { sourceSet ->
                    if (kvs.generateEnum) {
                        sourceSet.java.srcDirs("build/konversation/gen/${sourceSet.name}/")
                    }
                    if (kvs.generateKson) {
                        sourceSet.resources.srcDirs("build/konversation/res/${sourceSet.name}/")
                    }
                }
            }
        }

        val settingsFile = searchFile(File(".").absoluteFile.parentFile, "konversation.yaml")
        val config = Gson().fromJson(settingsFile?.readText().orEmpty(), KonversationConfig::class.java)

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
            project.applyConfig(config)
            if (project.outputDirectory == null) {
                project.outputDirectory = kvs.intentSchemaDirectory
                File(kvs.intentSchemaDirectory).mkdirs()
            }
            val gradleName = project.name.split(' ', '-').joinToString(separator = "") { word -> word.capitalize() }
            tasks.create("export$gradleName", DefaultTask::class.java).groupToKonversation("Export the ${project.name} project for all platforms.").also { exportProject ->
                project.alexa?.let {
                    val exportProjectOnAlexa = tasks.create("export${gradleName}ForAlexa", ExportAlexaTask::class.java) { task ->
                        task.settings = kvs
                        task.project = project
                    }.groupToKonversation("Export ${project.name} for Alexa.")
                    exportProject.dependsOn += exportProjectOnAlexa
                    exportAlexa.dependsOn += exportProjectOnAlexa
                }
                project.dialogflow?.let {
                    val exportProjectOnDialogflow = tasks.create("export${gradleName}ForDialogflow", ExportDialogflowTask::class.java) { task ->
                        task.settings = kvs
                        task.project = project
                    }.groupToKonversation("Export ${project.name} for Dialogflow.")
                    exportProject.dependsOn += exportProjectOnDialogflow
                    exportDialogflow.dependsOn += exportProjectOnDialogflow
                }
            }
            tasks.create("update$gradleName", DefaultTask::class.java).groupToKonversation("Update the ${project.name} project on all platforms.").also { updateProject ->
                project.alexa?.let {
                    val updateProjectOnAlexa = tasks.create("update${gradleName}OnAlexa", UpdateAlexaTask::class.java) { task ->
                        task.settings = kvs
                        task.project = project
                    }.groupToKonversation("Update ${project.name} on Alexa.")
                    updateProject.dependsOn += updateProjectOnAlexa
                    updateAlexa.dependsOn += updateProjectOnAlexa
                }
                project.dialogflow?.let {
                    val updateProjectOnDialogflow = tasks.create("update${gradleName}OnDialogflow", UpdateDialogflowTask::class.java) { task ->
                        task.settings = kvs
                        task.project = project
                    }.groupToKonversation("Update ${project.name} on Dialogflow.")
                    updateProject.dependsOn += updateProjectOnDialogflow
                    updateDialogflow.dependsOn += updateProjectOnDialogflow
                }
            }
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

val WorkAction<KonversationProjectParameters>.project: GradleProject
    get() = parameters.project.get()