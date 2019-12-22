package org.rewedigital.konversation.tasks.actions

import org.rewedigital.konversation.project
import java.io.File

@Suppress("UnstableApiUsage")
abstract class ExportAlexaAction : BaseAction() {
    override fun execute() {
        api.inputFiles += project.inputFiles.map(::File)
        api.inputFiles += project.alexa?.inputFiles.orEmpty().map(::File)
        api.invocationName = requireNotNull(project.invocationNames.values.firstOrNull() ?: project.alexa?.invocationNames?.values?.firstOrNull()) { "Invocation name not found" }
        val target = File(project.outputDirectory, api.invocationName?.replace(' ', '-')?.toLowerCase() + ".json")
        logger.lifecycle("Exporting ${api.invocationName} to ${target.absolutePath}...")
        api.exportAlexaSchema(target, true)
        logger.info("Export finished")
    }
}