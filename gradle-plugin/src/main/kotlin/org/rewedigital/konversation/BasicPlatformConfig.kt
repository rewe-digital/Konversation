package org.rewedigital.konversation

import java.io.File

data class BasicPlatformConfig(
    override var invocationName: String? = null,
    override var language: String? = null,
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null,
    var enabled: Boolean = false
) : VoiceAppConfig, java.io.Serializable