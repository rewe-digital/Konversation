package org.rewedigital.konversation.tasks

import org.gradle.api.tasks.Input
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.tasks.actions.BaseAction
import javax.inject.Inject

abstract class ExportKonversationEnumTask @Inject constructor(workerExecutor: WorkerExecutor) : AbstractExportTask(workerExecutor, ExportKonversationEnumAction::class.java) {
    @Input
    var enumPackageName: String? = null

    override fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, projectName: String?) {
        actionParameters.outputDir.set(extensionSettings.enumTargetDir?.path)
        actionParameters.enumPackageName.set(extensionSettings.enumPackageName)
    }
}

abstract class ExportKonversationEnumAction : BaseAction() {
    @Suppress("UnstableApiUsage")
    override fun execute() {
        api.inputFiles.addAll(actionInputFiles)
        val packageName = parameters.enumPackageName.get()
        logger.debug("Exporting Enum to $actionOutputDir for package name $packageName...")
        api.exportEnum(actionOutputDir, packageName)
        logger.debug("Export finished")
    }
}