package org.rewedigital.konversation.config

import java.io.File

data class Auth constructor(
    var alexaRefreshToken: String? = null,
    var alexaClientId: String? = null,
    var alexaClientSecret: String? = null,
    var dialogflowServiceAccount: File? = null
)