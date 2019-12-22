package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.parser.Utterance
import org.rewedigital.konversation.tasks.actions.UpdateAlexaAction
import org.rewedigital.konversation.tasks.actions.UpdateDialogflowAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class UpdateTask @Inject constructor(private var workerExecutor: WorkerExecutor) : DefaultTask() {
    var project: String? = null
    var settings: KonversationExtension? = null

    @TaskAction
    fun provision() = settings?.let { settings ->
        Utterance.cacheDir = settings.cacheDir
        when {
            taskName.endsWith("Dialogflow") -> workerExecutor.noIsolation().submit(UpdateDialogflowAction::class.java) {
                it.project.set(settings.projects[project])
            }
            taskName.endsWith("Alexa") -> workerExecutor.noIsolation().submit(UpdateAlexaAction::class.java) {
                it.project.set(settings.projects[project])
            }
            else -> throw IllegalArgumentException("Config error: Nothing to deploy")
        }
    }

    private val taskName
        get() = taskIdentity.name
}