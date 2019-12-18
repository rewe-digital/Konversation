package org.rewedigital.konversation

import org.rewedigital.konversation.config.Auth
import org.rewedigital.konversation.config.DialogflowProject
import java.io.File

data class DialogflowConfig(
    override var invocationNames: MutableMap<String, String> = mutableMapOf(),
    override val inputFiles: MutableList<File> = mutableListOf(),
    override var outputDirectory: File? = null,
    var projectId: String? = null,
    var serviceAccount: File? = null
) : IOConfig, java.io.Serializable {
    constructor(dialogflowProject: DialogflowProject?, auth: Auth) : this(
        invocationNames = dialogflowProject?.invocations.orEmpty().toMutableMap(),
        projectId = dialogflowProject?.projectId,
        serviceAccount = dialogflowProject?.serviceAccount ?: auth.dialogflowServiceAccount)

    fun fillWith(dialogflowProject: DialogflowProject?, auth: Auth) {
        if (invocationNames.isEmpty())
            invocationNames = dialogflowProject?.invocations ?: invocationNames
        projectId = projectId ?: dialogflowProject?.projectId
        serviceAccount = serviceAccount ?: dialogflowProject?.serviceAccount ?: auth.dialogflowServiceAccount
    }
}