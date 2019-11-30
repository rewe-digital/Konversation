package org.rewedigital.konversation.config

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable

@Serializable
data class KonversationConfig @ImplicitReflectionSerializer constructor(
    val config: Auth = Auth(),
    val projects: MutableMap<String, KonversationProject> = mutableMapOf()
)