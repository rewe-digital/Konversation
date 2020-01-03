package org.rewedigital.konversation

import org.rewedigital.konversation.config.KonversationConfig

data class GradleProject(
    val name: String,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    var alexa: AlexaConfig? = AlexaConfig(),
    var dialogflow: DialogflowConfig? = DialogflowConfig(),
    override var inputFiles: MutableList<String> = mutableListOf(),
    override var outputDirectory: String? = null) : IOConfig, java.io.Serializable {

    fun applyConfig(config: KonversationConfig) = config.projects[name]?.let { defaults ->
        if (alexa == null) {
            alexa = AlexaConfig(defaults, config.auth)
        } else {
            alexa?.fillWith(defaults, config.auth)
        }
        if (dialogflow == null) {
            dialogflow = DialogflowConfig(defaults, config.auth)
        } else {
            dialogflow?.fillWith(defaults, config.auth)
        }
    }

    override fun toString() =
        "GradleProject(alexa=$alexa, dialogflow=$dialogflow, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}