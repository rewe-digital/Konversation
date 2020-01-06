package org.rewedigital.konversation.tasks

import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import javax.inject.Inject

abstract class ExportKsonTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractExportTask(workerExecutor, ExportKsonAction::class.java) {
    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension) {
        actionParameters.inputFiles.set(requireNotNull(settings) { "Settings must not be null" }.inputFiles)
        actionParameters.outputDir.set(extensionSettings.ksonDir)
        actionParameters.enumPackageName.set(settings?.enumPackageName)
    }
}

abstract class ExportKsonAction : AbstractAction() {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(actionInputFiles)
        logger.debug("Exporting Kson to $actionOutputDir...")
        api.exportKson(actionOutputDir, true)
        logger.debug("Export finished")
    }
}