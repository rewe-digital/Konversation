package org.rewedigital.konversation.tasks.actions

import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.project
import org.slf4j.LoggerFactory

@Suppress("UnstableApiUsage")
abstract class UpdateAlexaAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        val amazonClientId = ""
        val amazonClientSecret = ""
        val token = ""
        val skillId = ""
        val api = KonversationApi(amazonClientId, amazonClientSecret)
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa.inputFiles
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.alexa.invocationNames.values.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        api.updateAlexaSchema(token, api.invocationName!!, skillId)
        println("Done")
    }
}