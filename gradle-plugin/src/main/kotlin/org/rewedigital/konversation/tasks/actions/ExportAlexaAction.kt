package org.rewedigital.konversation.tasks.actions

import org.gradle.workers.WorkAction
import org.rewedigital.konversation.KonversationProjectParameters
import kotlin.random.Random

@Suppress("UnstableApiUsage")
abstract class ExportAlexaAction : WorkAction<KonversationProjectParameters> {
    override fun execute() {
        //println("Deploying ${parameters.platform} on ${parameters.platform}. You know ${parameters.config.get().invocationName}")
        println("Deploying ${parameters.project.get()}")
        Thread.sleep(Random.nextLong(5000, 30000))
        println("Done")
    }
}