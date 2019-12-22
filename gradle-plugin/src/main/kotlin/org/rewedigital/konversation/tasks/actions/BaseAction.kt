package org.rewedigital.konversation.tasks.actions

import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("UnstableApiUsage")
abstract class BaseAction : WorkAction<KonversationProjectParameters> {
    protected val logger = LoggerFactory.getLogger(this.javaClass) as Logger
    protected val api = KonversationApi().apply {
        logger = createLoggingFacade(this@BaseAction.logger)
    }
    protected val outputDir
        get() = File(parameters.outputDir.get())
    protected val inputFiles
        get() = parameters.inputFiles.get().map(::File)
}