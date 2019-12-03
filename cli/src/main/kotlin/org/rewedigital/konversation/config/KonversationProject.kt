package org.rewedigital.konversation.config

import kotlinx.serialization.Serializable

@Serializable
data class KonversationProject(
    var alexa: AlexaProject? = null,
    var dialogflow: DialogflowProject? = null,
    override val invocations: MutableMap<String, String> = mutableMapOf()
) : VoiceAppConfig {
    constructor(skillId: String? = null, projectId: String? = null, vararg invocations: Pair<String, String>) : this(
        skillId?.let { AlexaProject(skillId) },
        projectId?.let { DialogflowProject(projectId) },
        invocations.toMap().toMutableMap())
}