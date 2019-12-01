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
        val api = KonversationApi(project.alexa?.clientId, project.alexa?.clientSecret)
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa?.inputFiles.orEmpty()
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.alexa?.invocationNames?.values?.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        println("Uploading ${api.invocationName} to Alexa...")
        //println("DEBUG: token=${project.alexa?.refreshToken?.shorted}, skill:${project.alexa?.skillId?.shorted}, invocation:${api.invocationName}, clientId=${project.alexa?.clientId?.shorted}, clientSecret=${project.alexa?.clientSecret?.shorted}")
        api.updateAlexaSchema(project.alexa?.refreshToken!!, api.invocationName!!, project.alexa?.skillId!!)
        println("Done")
    }

    private val String.shorted
        get() = if (length > 20) "${take(10)}..${takeLast(10)}" else this
}