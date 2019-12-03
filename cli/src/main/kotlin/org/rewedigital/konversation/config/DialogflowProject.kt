package org.rewedigital.konversation.config

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class DialogflowProject(
    val projectId: String,
    @Serializable(with = FileSerializer::class)
    val serviceAccount: File? = null,
    override val invocations: MutableMap<String, String> = mutableMapOf()
) : VoiceAppConfig