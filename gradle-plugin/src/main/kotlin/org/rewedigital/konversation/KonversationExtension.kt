package org.rewedigital.konversation

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File

abstract class KonversationExtension(project: Project) : ExtensionAware /*: GradleProject*/ {
    private val buildDir = project.buildDir
    var cacheDir = buildDir.path + "/konversation/cache"
    var alexaIntentSchemaFile = buildDir.path + "/konversation/alexa-intent-schema.json"
    var enumPackageName: String = "org.rewedigital.konversation"
    var enumFile: File? = null
        get() = field ?: File(buildDir, "konversation/gen/main/" + enumPackageName.replace(".", "/") + "/Konversations.kt")
    val projects: Map<String, GradleProject>
        get() = (extensions.findByName("projects") as? NamedDomainObjectContainer<GradleProject>)?.map { it.name to it }?.toMap().orEmpty()

    //override fun toString() =
    //    "KonversationExtension(cacheDir='$cacheDir', alexaIntentSchemaFile='$alexaIntentSchemaFile', invocationNames=$invocationNames, alexa=$alexa, dialogflow=$dialogflow. projects=$projects)"
}