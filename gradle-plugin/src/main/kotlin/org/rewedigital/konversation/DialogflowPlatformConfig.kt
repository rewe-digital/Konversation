package org.rewedigital.konversation

import java.io.File
import java.util.*

data class DialogflowPlatformConfig(
    override var invocationNames: MutableMap<Locale, String> = mutableMapOf(),
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null,
    var enabled: Boolean = false,
    var targetProject: String
) : VoiceAppConfig, java.io.Serializable