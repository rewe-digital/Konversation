package org.rewedigital.konversation.tasks.actions

import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.project
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("UnstableApiUsage")
abstract class UpdateDialogflowAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        val serviceAccount = File("")
        val projectName = ""
        val api = KonversationApi("", "", serviceAccount)
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa.inputFiles
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.dialogflow.invocationNames.values.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        println("Uploading ${api.invocationName} to Dialogflow...")
        api.updateDialogflowProject(projectName, api.invocationName!!)
        println("Done")
    }
}