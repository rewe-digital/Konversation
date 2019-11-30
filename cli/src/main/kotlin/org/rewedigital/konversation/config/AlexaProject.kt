package org.rewedigital.konversation.config

import kotlinx.serialization.Serializable

@Serializable
data class AlexaProject(
    val skillId: String,
    val refreshToken: String? = null,
    val clientId: String? = null,
    val clientSecret: String? = null,
    override val invocations: MutableMap<String, String> = mutableMapOf()
) : VoiceAppConfig