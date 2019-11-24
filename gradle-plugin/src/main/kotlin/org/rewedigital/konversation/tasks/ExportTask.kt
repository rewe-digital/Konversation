package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.KonversationProject
import org.rewedigital.konversation.tasks.actions.ExportAlexaAction
import org.rewedigital.konversation.tasks.actions.ExportDialogflowAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class ExportTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var config: KonversationProject? = null

    @TaskAction
    fun provision() = when {
        config?.dialogflow?.enabled == true -> workerExecutor.noIsolation().submit(ExportDialogflowAction::class.java) {
            it.project.set(config)
        }
        config?.alexa?.enabled == true -> workerExecutor.noIsolation().submit(ExportAlexaAction::class.java) {
            it.project.set(config)
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }
}