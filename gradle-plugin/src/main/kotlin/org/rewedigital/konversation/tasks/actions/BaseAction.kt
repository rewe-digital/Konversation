package org.rewedigital.konversation.tasks.actions

import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.*
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("UnstableApiUsage")
abstract class BaseAction : WorkAction<KonversationProjectParameters> {
    protected val logger = LoggerFactory.getLogger(this.javaClass) as Logger
    protected val api = KonversationApi().apply {
        logger = createLoggingFacade(this@BaseAction.logger)
    }
    protected val actionOutputDir
        get() = File(parameters.outputDir.get())
    protected val actionInputFiles
        get() = parameters.inputFiles.get().map(::File)
    protected val actionProject: GradleProject
        get() = parameters.project.get()
    protected val KonversationExtension.inputFiles
        get() = projects.flatMap { (_, project) ->
            project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
        }
}