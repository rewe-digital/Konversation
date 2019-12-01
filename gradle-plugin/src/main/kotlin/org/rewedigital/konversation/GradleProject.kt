package org.rewedigital.konversation

import java.io.File

data class GradleProject(
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    var alexa: AlexaConfig? = null,
    var dialogflow: DialogflowConfig? = null,
    override val inputFiles: MutableList<File>,
    override var outputDirectory: File?) : IOConfig, java.io.Serializable {

    val alexaConfig: AlexaConfig
        get() = alexa ?: AlexaConfig().also {
            alexa = it
        }
    val dialogflowConfig: DialogflowConfig
        get() = dialogflow ?: DialogflowConfig().also {
            dialogflow = it
        }

    override fun toString() =
        "GradleProject(alexa=$alexa, dialogflow=$dialogflow, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}