package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.tasks.actions.ExportAlexaAction
import org.rewedigital.konversation.tasks.actions.ExportDialogflowAction
import org.rewedigital.konversation.tasks.actions.ExportEnumAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ExportTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var project: GradleProject? = null
    var settings: KonversationExtension? = null

    @TaskAction
    fun provision() = when {
        taskName.endsWith("Dialogflow") -> workerExecutor.noIsolation().submit(ExportDialogflowAction::class.java) {
            it.project.set(project)
        }
        taskName.endsWith("Alexa") -> workerExecutor.noIsolation().submit(ExportAlexaAction::class.java) {
            it.project.set(project)
        }
        taskName == "exportKonversationEnum" -> workerExecutor.noIsolation().submit(ExportEnumAction::class.java) {
            val projectInputFiles = settings?.projects?.flatMap { (_, project) ->
                project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
            }
            it.inputFiles.set(projectInputFiles)
            it.enumFile.set(requireNotNull(settings?.enumFile?.parentFile) { "Enum file must be set" })
            it.enumPackageName.set(requireNotNull(settings?.enumPackageName) { "Package name must be set" })
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }

    private val taskName
        get() = taskIdentity.name
}