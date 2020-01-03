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
        api.inputFiles.addAll(getInputFiles(actionProject))
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
        project.inputFiles.resolveFiles() + project.alexa?.inputFiles.orEmpty().resolveFiles()

    override fun getOutputFiles(project: GradleProject) = listOf(
        File(project.outputDirectory, getInvocationName(project).replace(' ', '-').toLowerCase() + ".json")
    )

    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, projectName: String?) {
        actionParameters.project.set(extensionSettings.projects[requireNotNull(projectName) { "Project name must not be null" }])
    }
}