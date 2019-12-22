package org.rewedigital.konversation

import org.rewedigital.konversation.config.Auth
import org.rewedigital.konversation.config.KonversationProject
import java.io.File

data class DialogflowConfig(
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override var inputFiles: MutableList<String> = mutableListOf(),
    override var outputDirectory: String? = null,
    var projectId: String? = null,
    var serviceAccount: File? = null
) : IOConfig, java.io.Serializable {
    constructor(project: KonversationProject?, auth: Auth) : this(
        invocationNames = project?.dialogflow?.invocations.orUse(project?.invocations),
        projectId = project?.dialogflow?.projectId,
        serviceAccount = project?.dialogflow?.serviceAccount ?: auth.dialogflowServiceAccount)

    fun fillWith(project: KonversationProject?, auth: Auth) {
        if (invocationNames.isEmpty())
            invocationNames = project?.dialogflow?.invocations.orUse(project?.invocations)
        projectId = projectId ?: project?.dialogflow?.projectId
        serviceAccount = serviceAccount ?: project?.dialogflow?.serviceAccount ?: auth.dialogflowServiceAccount
    }
}