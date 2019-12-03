package org.rewedigital.konversation.config

import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Auth constructor(
    var alexaRefreshToken: String? = null,
    var alexaClientId: String? = null,
    var alexaClientSecret: String? = null,
    @Serializable(with = FileSerializer::class)
    var dialogflowServiceAccount: File? = null
)