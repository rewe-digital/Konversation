package org.rewedigital.konversation.tasks.actions

import org.rewedigital.konversation.project
import java.io.File

@Suppress("UnstableApiUsage")
abstract class UpdateAlexaAction : BaseAction() {
    override fun execute() {
        api.inputFiles += project.inputFiles.map(::File)
        api.inputFiles += project.alexa?.inputFiles.orEmpty().map(::File)
        api.invocationName = project.invocationNames.values.firstOrNull() ?: project.alexa?.invocationNames?.values?.firstOrNull() ?: throw java.lang.IllegalArgumentException("Invocation names not found")
        println("Uploading ${api.invocationName} to Alexa...")
        //println("DEBUG: token=${project.alexa?.refreshToken?.shorted}, skill:${project.alexa?.skillId?.shorted}, invocation:${api.invocationName}, clientId=${project.alexa?.clientId?.shorted}, clientSecret=${project.alexa?.clientSecret?.shorted}")
        api.updateAlexaSchema(project.alexa?.refreshToken!!, api.invocationName!!, project.alexa?.skillId!!)
        println("Done")
    }

    private val String.shorted
        get() = if (length > 20) "${take(10)}..${takeLast(10)}" else this
}