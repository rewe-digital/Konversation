package org.rewedigital.konversation

import org.gradle.api.Project

abstract class KonversationExtension(project: Project) /*: GradleProject*/ {
    var cacheDir = project.buildDir.path + "/konversation/cache"
    var alexaIntentSchemaFile = project.buildDir.path + "/konversation/alexa-intent-schema.json"
    //val alexa: AlexaTargetExtension?
    //    get() = getExtension("alexa")
    //val dialogflow: DialogflowTargetExtension?
    //    get() = getExtension("dialogflow")
    val projects = mutableMapOf<String, GradleProject>()

    //override fun toString() =
    //    "KonversationExtension(cacheDir='$cacheDir', alexaIntentSchemaFile='$alexaIntentSchemaFile', invocationNames=$invocationNames, alexa=$alexa, dialogflow=$dialogflow. projects=$projects)"
}