package org.rewedigital.konversation.tasks

import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.*
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("UnstableApiUsage")
abstract class AbstractAction : WorkAction<KonversationProjectParameters>, TaskSetupProvider {
    protected val logger = LoggerFactory.getLogger(this.javaClass) as Logger
    protected val api = KonversationApi().apply {
        logger = createLoggingFacade(this@AbstractAction.logger)
    }
    protected val actionOutputDir
        get() = File(parameters.outputDir.get())
    protected val actionInputFiles
        get() = parameters.inputFiles.get().resolveFiles()
    protected val actionProject: GradleProject
        get() = parameters.project.get()
    protected val KonversationExtension.inputFiles
        get() = projects.flatMap { (_, project) ->
            project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
        }
}