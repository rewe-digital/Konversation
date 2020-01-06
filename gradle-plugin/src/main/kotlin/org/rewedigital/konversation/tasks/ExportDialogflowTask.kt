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
        api.inputFiles.addAll(actionInputFiles)
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
        project.inputFiles + project.dialogflow?.inputFiles.orEmpty()

    override fun getOutputFiles(project: GradleProject) = listOf(
        File(project.outputDirectory, getInvocationName(project).replace(' ', '-').toLowerCase() + ".zip")
    )

    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, project: GradleProject) {
        actionParameters.project.set(project)
        actionParameters.inputFiles.set(getInputFiles(project).resolveFiles(extensionSettings.sourceSets))
    }
}