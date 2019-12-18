package org.rewedigital.konversation

import org.rewedigital.konversation.config.KonversationConfig
import java.io.File

data class GradleProject(
    val name: String,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    var alexa: AlexaConfig? = null,
    var dialogflow: DialogflowConfig? = null,
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null) : IOConfig, java.io.Serializable {

    fun fillWith(config: KonversationConfig) = config.projects[name]?.let { defaults ->
        if (alexa == null) {
            alexa = AlexaConfig(defaults.alexa, config.config)
        } else {
            alexa?.fillWith(defaults.alexa, config.config)
        }
        if (dialogflow == null) {
            dialogflow = DialogflowConfig(defaults.dialogflow, config.config)
        } else {
            dialogflow?.fillWith(defaults.dialogflow, config.config)
        }
    }

    override fun toString() =
        "GradleProject(alexa=$alexa, dialogflow=$dialogflow, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}