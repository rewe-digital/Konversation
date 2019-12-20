package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.tasks.actions.ExportAlexaAction
import org.rewedigital.konversation.tasks.actions.ExportDialogflowAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ExportTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var config: GradleProject? = null

    @TaskAction
    fun provision() = when (taskIdentity.name.substringAfterLast("For")) {
        "Dialogflow" -> workerExecutor.noIsolation().submit(ExportDialogflowAction::class.java) {
            it.project.set(config)
        }
        "Alexa" -> workerExecutor.noIsolation().submit(ExportAlexaAction::class.java) {
            it.project.set(config)
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }
}