package org.rewedigital.konversation

import org.rewedigital.konversation.config.AlexaProject
import org.rewedigital.konversation.config.Auth
import java.io.File

data class AlexaConfig(
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null,
    var skillId: String? = null,
    var refreshToken: String? = null,
    var clientId: String? = null,
    var clientSecret: String? = null
) : IOConfig, java.io.Serializable {
    fun fillWith(alexaProject: AlexaProject?, auth: Auth) {
        if (invocationNames.isEmpty()) {
            invocationNames = alexaProject?.invocations ?: invocationNames
        }
        skillId = skillId ?: alexaProject?.skillId
        refreshToken = refreshToken ?: alexaProject?.refreshToken ?: auth.alexaRefreshToken
        clientId = clientId ?: alexaProject?.clientId ?: auth.alexaClientId
        clientSecret = clientSecret ?: alexaProject?.clientSecret ?: auth.alexaClientSecret
    }
}