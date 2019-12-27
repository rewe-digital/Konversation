package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.project
import org.rewedigital.konversation.tasks.actions.BaseAction
import java.io.File
import javax.inject.Inject

abstract class UpdateDialogflowTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractUpdateTask(workerExecutor, UpdateDialogflowAction::class.java), DialogflowSetupProvider {
    override fun getOutputFiles(project: GradleProject) = emptyList<File>()
}

abstract class UpdateDialogflowAction : BaseAction(), DialogflowSetupProvider {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(getInputFiles(actionProject))
        api.invocationName = getInvocationName(actionProject)
        logger.lifecycle("Uploading ${api.invocationName} to Dialogflow...")
        logger.debug("serviceAccount=${project.dialogflow?.serviceAccount}, project:${project.dialogflow?.projectId}, invocation:${api.invocationName}}")
        api.updateDialogflowProject(project.dialogflow?.projectId!!, api.invocationName!!)
        logger.lifecycle("Done")
    }
}