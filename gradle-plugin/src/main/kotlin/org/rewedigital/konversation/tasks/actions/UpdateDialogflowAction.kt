package org.rewedigital.konversation.tasks.actions

import org.rewedigital.konversation.project
import java.io.File

@Suppress("UnstableApiUsage")
abstract class UpdateDialogflowAction : BaseAction() {
    override fun execute() {
        api.inputFiles += project.inputFiles.map(::File)
        api.inputFiles += project.dialogflow?.inputFiles.orEmpty().map(::File)
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.dialogflow?.invocationNames?.values?.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        println("Uploading ${api.invocationName} to Dialogflow...")
        //println("DEBUG: serviceAccount=${project.dialogflow?.serviceAccount}, project:${project.dialogflow?.projectId}, invocation:${api.invocationName}}")
        api.updateDialogflowProject(project.dialogflow?.projectId!!, api.invocationName!!)
        println("Done")
    }
}