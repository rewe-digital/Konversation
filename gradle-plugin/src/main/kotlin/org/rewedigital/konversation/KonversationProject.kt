package org.rewedigital.konversation

import java.io.File
import java.util.*

data class KonversationProject(
    val alexa: BasicPlatformConfig = BasicPlatformConfig(),
    val dialogflow: BasicPlatformConfig = BasicPlatformConfig(),
    override var invocationNames: MutableMap<Locale, String> = mutableMapOf(),
    override val inputFiles: MutableList<File>,
    override var outputDirectory: File?) : VoiceAppConfig, java.io.Serializable {

    init {
        //println("Invocation name result: ${invocationName?.isNotBlank() == true}")
        //require(!((invocationName == null && invocationNames.isEmpty()) ||
        //        (alexa.invocationName == null && alexa.invocationNames.isEmpty()) ||
        //        (dialogflow.invocationName == null && dialogflow.invocationNames.isEmpty()))) {
        //    "You must set ether the invocationName or at least one translation of invocationNames. You can set it in the root config per platform."
        //}
    }

    override fun toString() =
        "KonversationProject(alexa=$alexa, dialogflow=$dialogflow, invocationNames=$invocationNames, inputFiles=$inputFiles, outputDirectory=$outputDirectory)"
}