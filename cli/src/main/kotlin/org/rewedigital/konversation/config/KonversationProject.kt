package org.rewedigital.konversation.config

data class KonversationProject(
    var alexa: AlexaProject? = null,
    var dialogflow: DialogflowProject? = null,
    override val invocations: MutableMap<String, String> = mutableMapOf()
) : VoiceAppConfig {
    constructor(skillId: String? = null, projectId: String? = null, vararg invocations: Pair<String, String>) : this(
        skillId?.let { AlexaProject(skillId) },
        projectId?.let { DialogflowProject(projectId) },
        invocations.toMap().toMutableMap())

    val alexaInvocations: Map<String, String>
        get() = alexa?.invocations.orIfEmpty(invocations).requireNotEmpty { "No invocation set" }

    val dialogflowInvocations: Map<String, String>
        get() = dialogflow?.invocations.orIfEmpty(invocations).requireNotEmpty { "No invocation set" }

    private fun <K, V> Map<K, V>?.orIfEmpty(other: Map<K, V>): Map<K, V> = if (isNullOrEmpty()) other else orEmpty()
    private fun <K, V> Map<K, V>?.requireNotEmpty(msg: () -> String): Map<K, V> = if (isNullOrEmpty()) throw java.lang.IllegalArgumentException(msg()) else orEmpty()
}