package org.rewedigital.konversation.tasks.actions

import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.project
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("UnstableApiUsage")
abstract class UpdateAlexaAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        val api = KonversationApi("", "")
        api.inputFiles += project.inputFiles
        api.inputFiles += project.alexa.inputFiles
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateAlexaAction::class.java))
        api.invocationName = project.invocationName ?: project.alexa.invocationName ?: project.invocationNames.values.firstOrNull() ?: project.alexa.invocationNames.values.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invationname not found")
        val tmp = createTempDir(prefix = "alexa-", directory = File(""))
        println("Updating to $tmp")
        api.exportAlexaSchema(File(tmp, "schema.json"), api.invocationName.orEmpty())
        //Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}