package org.rewedigital.konversation.tasks.actions

import org.rewedigital.konversation.project
import java.io.File

@Suppress("UnstableApiUsage")
abstract class ExportDialogflowAction : BaseAction() {
    override fun execute() {
        api.inputFiles += project.inputFiles.map(::File)
        api.inputFiles += project.dialogflow?.inputFiles.orEmpty().map(::File)
        api.invocationName = requireNotNull(project.invocationNames.values.firstOrNull() ?: project.dialogflow?.invocationNames?.values?.firstOrNull()) { "Invocation name not found" }
        val outDir = File(requireNotNull(project.dialogflow?.outputDirectory ?: project.outputDirectory) { "Output directory not set" })
        logger.debug("Exporting ${api.invocationName} to $outDir...")
        api.exportDialogflow(outDir, true)
        logger.debug("Export finished")
    }
}