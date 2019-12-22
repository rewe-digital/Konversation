package org.rewedigital.konversation.tasks.actions

@Suppress("UnstableApiUsage")
abstract class ExportEnumAction : BaseAction() {
    override fun execute() {
        val packageName = parameters.enumPackageName.get()
        logger.debug("Exporting Enum to $outputDir for package name $packageName...")
        api.exportEnum(outputDir, packageName)
        logger.debug("Export finished")
    }
}