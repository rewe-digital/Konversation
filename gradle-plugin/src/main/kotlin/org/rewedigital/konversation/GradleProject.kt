package org.rewedigital.konversation

import org.rewedigital.konversation.config.KonversationConfig
import java.io.File

data class GradleProject(
    val name: String,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    var alexa: AlexaConfig? = null,
    var dialogflow: DialogflowConfig? = null,
    override var inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null) : IOConfig, java.io.Serializable {

    fun fillWith(config: KonversationConfig) = config.projects[name]?.let { defaults ->
        if (alexa == null) {
            alexa = AlexaConfig(defaults, config.config)
        } else {
            alexa?.fillWith(defaults, config.config)
        }
        if (dialogflow == null) {
            dialogflow = DialogflowConfig(defaults, config.config)
        } else {
            dialogflow?.fillWith(defaults, config.config)
        }
    }

    override fun toString() =
        "GradleProject(alexa=$alexa, dialogflow=$dialogflow, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}