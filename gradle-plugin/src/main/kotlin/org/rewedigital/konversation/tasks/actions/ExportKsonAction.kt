package org.rewedigital.konversation.tasks.actions

@Suppress("UnstableApiUsage")
abstract class ExportKsonAction : BaseAction() {
    override fun execute() {
        api.inputFiles.addAll(inputFiles)
        logger.debug("Exporting Kson to $outputDir...")
        api.exportKson(outputDir, true)
        logger.debug("Export finished")
    }
}