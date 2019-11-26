package org.rewedigital.konversation.tasks.actions

import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.project
import org.slf4j.LoggerFactory

@Suppress("UnstableApiUsage")
abstract class UpdateDialogflowAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        val api = KonversationApi("", "")
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa.inputFiles
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.dialogflow.invocationNames.values.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        //api.updateDialogflowProject(File(tmp, "schema.json"), true)
    }
}