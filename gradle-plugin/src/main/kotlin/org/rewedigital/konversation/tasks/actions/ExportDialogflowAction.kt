package org.rewedigital.konversation.tasks.actions

import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.project
import org.slf4j.LoggerFactory

@Suppress("UnstableApiUsage")
abstract class ExportDialogflowAction : WorkAction<KonversationProjectParameters> {
    private val logger = LoggerFactory.getLogger(ExportDialogflowAction::class.java) as Logger

    override fun execute() {
        val api = KonversationApi()
        api.inputFiles += project.inputFiles
        api.inputFiles += project.dialogflow?.inputFiles.orEmpty()
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateDialogflowAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.dialogflow?.invocationNames?.values?.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invationname not found")
        logger.lifecycle("Exporting ${api.invocationName} to ${project.outputDirectory}...")
        api.exportDialogflow(project.outputDirectory!!, true)
        logger.info("Export finished")
    }
}