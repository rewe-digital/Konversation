package org.rewedigital.konversation.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KonversationConfig(
    @SerialName("config")
    val auth: Auth = Auth(),
    val projects: MutableMap<String, KonversationProject> = mutableMapOf()
) {
    constructor(config: Auth = Auth(), vararg projects: Pair<String, KonversationProject>) : this(config, projects.toMap().toMutableMap())

    fun applyConfig() {
        projects.values.forEach { project ->
            project.alexa?.let { alexa ->
                project.alexa = AlexaProject(
                    skillId = alexa.skillId,
                    clientId = alexa.clientId ?: auth.alexaClientId,
                    clientSecret = alexa.clientSecret ?: auth.alexaClientSecret,
                    refreshToken = alexa.refreshToken ?: auth.alexaRefreshToken,
                    invocations = if (alexa.invocations.isEmpty()) project.invocations else alexa.invocations)
            }
            project.dialogflow?.let { dialogflow ->
                project.dialogflow = DialogflowProject(
                    projectId = dialogflow.projectId,
                    serviceAccount = dialogflow.serviceAccount ?: auth.dialogflowServiceAccount,
                    invocations = if (dialogflow.invocations.isEmpty()) project.invocations else dialogflow.invocations)
            }
        }
    }

    val projectsWithDefaults
        get() = projects.map { (name, project) ->
            name to KonversationProject(
                alexa = project.alexa?.let { alexa ->
                    AlexaProject(
                        skillId = alexa.skillId,
                        clientId = alexa.clientId ?: auth.alexaClientId,
                        clientSecret = alexa.clientSecret ?: auth.alexaClientSecret,
                        refreshToken = alexa.refreshToken ?: auth.alexaRefreshToken,
                        invocations = if (alexa.invocations.isEmpty()) project.invocations else alexa.invocations)
                },
                dialogflow = project.dialogflow?.let { dialogflow ->
                    DialogflowProject(
                        projectId = dialogflow.projectId,
                        serviceAccount = dialogflow.serviceAccount ?: auth.dialogflowServiceAccount,
                        invocations = if (dialogflow.invocations.isEmpty()) project.invocations else dialogflow.invocations)
                },
                invocations = project.invocations)
        }.toMap()
}