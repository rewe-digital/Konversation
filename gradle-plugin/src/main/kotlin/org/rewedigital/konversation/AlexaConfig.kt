package org.rewedigital.konversation

import org.rewedigital.konversation.config.Auth
import org.rewedigital.konversation.config.KonversationProject

data class AlexaConfig(
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override var inputFiles: MutableList<String> = mutableListOf(),
    override var outputDirectory: String? = null,
    var skillId: String? = null,
    var refreshToken: String? = null,
    var clientId: String? = null,
    var clientSecret: String? = null
) : IOConfig, java.io.Serializable {
    constructor(project: KonversationProject?, auth: Auth) : this(
        invocationNames = project?.alexa?.invocations.orUse(project?.invocations),
        skillId = project?.alexa?.skillId,
        refreshToken = project?.alexa?.refreshToken ?: auth.alexaRefreshToken,
        clientId = project?.alexa?.clientId ?: auth.alexaClientId,
        clientSecret = project?.alexa?.clientSecret ?: auth.alexaClientSecret)

    fun fillWith(project: KonversationProject?, auth: Auth) {
        if (invocationNames.isEmpty()) {
            invocationNames = project?.alexa?.invocations.orUse(project?.invocations)
        }
        skillId = skillId ?: project?.alexa?.skillId
        refreshToken = refreshToken ?: project?.alexa?.refreshToken ?: auth.alexaRefreshToken
        clientId = clientId ?: project?.alexa?.clientId ?: auth.alexaClientId
        clientSecret = clientSecret ?: project?.alexa?.clientSecret ?: auth.alexaClientSecret
    }
}