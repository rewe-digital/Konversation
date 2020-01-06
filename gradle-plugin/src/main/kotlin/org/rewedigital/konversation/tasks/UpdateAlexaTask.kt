package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.project
import java.io.File
import javax.inject.Inject

abstract class UpdateAlexaTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractUpdateTask(workerExecutor, UpdateAlexaAction::class.java), AlexaSetupProvider {
    override fun getOutputFiles(project: GradleProject) = emptyList<File>()
}

abstract class UpdateAlexaAction : AbstractAction(), AlexaSetupProvider {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(actionInputFiles)
        api.invocationName = getInvocationName(actionProject)
        logger.lifecycle("Uploading ${api.invocationName} to Alexa...")
        logger.debug("token=${project.alexa?.refreshToken?.shorted}, skill:${project.alexa?.skillId?.shorted}, invocation:${api.invocationName}, clientId=${project.alexa?.clientId?.shorted}, clientSecret=${project.alexa?.clientSecret?.shorted}")
        api.updateAlexaSchema(api.invocationName!!, project.alexa?.skillId!!)
        logger.lifecycle("Done")
    }

    private val String.shorted
        get() = if (length > 20) "${take(10)}..${takeLast(10)}" else this
}