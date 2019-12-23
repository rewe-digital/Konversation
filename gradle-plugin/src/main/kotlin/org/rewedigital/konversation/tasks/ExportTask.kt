package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.parser.Utterance
import org.rewedigital.konversation.tasks.actions.ExportEnumAction
import org.rewedigital.konversation.tasks.actions.ExportKsonAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ExportTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var project: String? = null
    var settings: KonversationExtension? = null

    @TaskAction
    fun provision() = settings?.let { settings ->
        Utterance.cacheDir = settings.cacheDir
        when {
            taskName == "exportKonversationEnum" -> workerExecutor.noIsolation().submit(ExportEnumAction::class.java) {
                it.inputFiles.set(settings.inputFiles)
                it.outputDir.set(requireNotNull(settings.enumFile?.parent) { "Enum file must be set" })
                it.enumPackageName.set(requireNotNull(settings.enumPackageName) { "Package name must be set" })
            }
            taskName == "exportKson" -> workerExecutor.noIsolation().submit(ExportKsonAction::class.java) {
                it.inputFiles.set(settings.inputFiles)
                it.outputDir.set(requireNotNull(settings.ksonDir) { "Resources directory not set" })
            }
            else -> throw IllegalArgumentException("Config error: Nothing to deploy")
        }
    }

    private val taskName
        get() = taskIdentity.name

    private val KonversationExtension.inputFiles
        get() = projects.flatMap { (_, project) ->
            project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
        }
}