package org.rewedigital.konversation.config

import java.io.File

data class DialogflowProject(
    val projectId: String,
    val serviceAccount: File? = null,
    override val invocations: MutableMap<String, String> = mutableMapOf()
) : VoiceAppConfig