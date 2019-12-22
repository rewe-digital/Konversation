package org.rewedigital.konversation

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File

abstract class KonversationExtension(project: Project) : ExtensionAware {
    private val buildDir = project.buildDir
    var cacheDir = buildDir.path + "/konversation/cache"
    var intentSchemaDirectory = buildDir.path + "/konversation/intent-schemas/"
    var enumPackageName: String = "org.rewedigital.konversation"
    var enumFile: File? = null
        get() = field ?: File(buildDir, "konversation/gen/main/" + enumPackageName.replace(".", "/") + "/Konversations.kt")
    var ksonDir: String? = null
        get() = field ?: buildDir.path + "/konversation/res/"
    val projects: Map<String, GradleProject>
        get() = getExtension<NamedDomainObjectContainer<GradleProject>>("projects")?.map { it.name to it }?.toMap().orEmpty()
    var generateKson = true
    var generateEnum = true

    //override fun toString() =
    //    "KonversationExtension(cacheDir='$cacheDir', alexaIntentSchemaFile='$alexaIntentSchemaFile', invocationNames=$invocationNames, alexa=$alexa, dialogflow=$dialogflow. projects=$projects)"
}