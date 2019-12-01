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
        val api = KonversationApi(dialogflowServiceAccount = project.dialogflow?.serviceAccount)
        api.inputFiles += project.inputFiles
        api.inputFiles += project.dialogflow?.inputFiles.orEmpty()
        api.logger = createLoggingFacade(LoggerFactory.getLogger(UpdateDialogflowAction::class.java))
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.dialogflow?.invocationNames?.values?.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        println("Uploading ${api.invocationName} to Dialogflow...")
        //println("DEBUG: serviceAccount=${project.dialogflow?.serviceAccount}, project:${project.dialogflow?.projectId}, invocation:${api.invocationName}}")
        api.updateDialogflowProject(project.dialogflow?.projectId!!, api.invocationName!!)
        println("Done")
    }
}