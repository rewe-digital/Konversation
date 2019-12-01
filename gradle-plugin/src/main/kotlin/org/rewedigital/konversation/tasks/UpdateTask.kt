package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.tasks.actions.UpdateAlexaAction
import org.rewedigital.konversation.tasks.actions.UpdateDialogflowAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class UpdateTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var config: GradleProject? = null

    @TaskAction
    fun provision() = when {
        config?.dialogflow != null -> workerExecutor.noIsolation().submit(UpdateDialogflowAction::class.java) {
            it.project.set(config)
        }
        config?.alexa != null -> workerExecutor.noIsolation().submit(UpdateAlexaAction::class.java) {
            it.project.set(config)
        }
        else -> throw IllegalArgumentException("Config error: Nothing to deploy")
    }
}