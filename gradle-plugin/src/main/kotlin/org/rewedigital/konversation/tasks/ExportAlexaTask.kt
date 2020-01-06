package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import java.io.File
import javax.inject.Inject

abstract class ExportAlexaTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractProjectExportingTask(workerExecutor, ExportAlexaAction::class.java), AlexaSetupProvider

abstract class ExportAlexaAction : AbstractAction(), AlexaSetupProvider {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(actionInputFiles)
        api.invocationName = getInvocationName(actionProject)
        val target = getOutputFiles(actionProject).first()
        logger.debug("Exporting ${api.invocationName} to ${target.absolutePath}...")
        api.exportAlexaSchema(target, true)
        logger.debug("Export finished")
    }
}

internal interface AlexaSetupProvider : TaskSetupProvider {
    fun getInvocationName(project: GradleProject) =
        requireNotNull(project.alexa?.invocationNames?.values?.firstOrNull() ?: project.invocationNames.values.firstOrNull()) { "Invocation name not found" }

    override fun getInputFiles(project: GradleProject) =
        project.inputFiles + project.alexa?.inputFiles.orEmpty()

    override fun getOutputFiles(project: GradleProject) = listOf(
        File(project.outputDirectory, getInvocationName(project).replace(' ', '-').toLowerCase() + ".json")
    )

    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, project: GradleProject) {
        actionParameters.project.set(project)
        actionParameters.inputFiles.set(getInputFiles(project).resolveFiles(extensionSettings.sourceSets))
    }
}