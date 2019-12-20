package org.rewedigital.konversation.tasks.actions

import org.gradle.api.logging.Logger
import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationApi
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.createLoggingFacade
import org.slf4j.LoggerFactory

@Suppress("UnstableApiUsage")
abstract class ExportEnumAction : WorkAction<KonversationProjectParameters> {
    private val logger = LoggerFactory.getLogger(ExportEnumAction::class.java) as Logger

    override fun execute() {
        val api = KonversationApi()
        api.inputFiles.addAll(parameters.inputFiles.get())
        api.logger = createLoggingFacade(logger)
        val enumFile = parameters.enumFile.get()
        val packageName = parameters.enumPackageName.get()
        logger.debug("Exporting Enum to $enumFile for package name $packageName...")
        api.exportEnum(enumFile, packageName)
        logger.debug("Export finished")
    }
}