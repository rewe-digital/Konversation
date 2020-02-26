package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.project
import java.io.File
import javax.inject.Inject

abstract class UpdateDialogflowTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractUpdateTask(workerExecutor, UpdateDialogflowAction::class.java), DialogflowSetupProvider {
    override fun getOutputFiles(project: GradleProject) = emptyList<File>()
}

abstract class UpdateDialogflowAction : AbstractAction(), DialogflowSetupProvider {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(actionInputFiles)
        api.invocationName = getInvocationName(actionProject)
        logger.lifecycle("Uploading ${api.invocationName} to Dialogflow...")
        logger.debug("serviceAccount=${project.dialogflow?.serviceAccount}, project:${project.dialogflow?.projectId}, invocation:${api.invocationName}}")
        api.updateDialogflowProject(project.dialogflow?.projectId!!)
        logger.lifecycle("Done")
    }
}