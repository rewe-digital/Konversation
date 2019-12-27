package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import java.io.File
import javax.inject.Inject

abstract class ExportDialogflowTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractProjectExportingTask(workerExecutor, ExportDialogflowAction::class.java), DialogflowSetupProvider

abstract class ExportDialogflowAction : AbstractAction(), DialogflowSetupProvider {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(getInputFiles(actionProject))
        api.invocationName = getInvocationName(actionProject)
        val outDir = getOutputFiles(actionProject).first()
        logger.debug("Exporting ${api.invocationName} to $outDir...")
        api.exportDialogflow(outDir.parentFile, true)
        logger.debug("Export finished")
    }
}

internal interface DialogflowSetupProvider : TaskSetupProvider {
    fun getInvocationName(project: GradleProject) =
        requireNotNull(project.dialogflow?.invocationNames?.values?.firstOrNull() ?: project.invocationNames.values.firstOrNull()) { "Invocation name not found" }

    override fun getInputFiles(project: GradleProject) =
        project.inputFiles.map(::File) + project.dialogflow?.inputFiles.orEmpty().map(::File)

    override fun getOutputFiles(project: GradleProject) = listOf(
        File(project.outputDirectory, getInvocationName(project).replace(' ', '-').toLowerCase() + ".zip")
    )

    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, projectName: String?) {
        actionParameters.project.set(extensionSettings.projects[requireNotNull(projectName) { "Project name must not be null" }])
    }
}