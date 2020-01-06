package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.parser.Utterance
import java.io.File
import javax.inject.Inject

@Suppress("UnstableApiUsage")
abstract class AbstractUpdateTask @Inject constructor(
    private var workerExecutor: WorkerExecutor,
    private val workClass: Class<out WorkAction<KonversationProjectParameters>>) : DefaultTask(), TaskSetupProvider {

   var settings: KonversationExtension? = null
       set(value) {
           field = requireNotNull(value) { "Settings must not be null" }
           // This static field will be overwritten multiple times with the same value
           Utterance.cacheDir = value.cacheDir
       }
    var project: GradleProject? = null
    var inputFiles: List<File> = emptyList()
    var outputFiles: List<File> = emptyList()

    @TaskAction
    fun executeTask() {
        workerExecutor.noIsolation().submit(workClass) {
            setupParameters(it, requireNotNull(settings) { "Settings must not be null" }, requireNotNull(project) { "Project must not be null" })
        }
    }

    private fun setParameters(project: GradleProject) {
        inputFiles = getInputFiles(project).resolveFiles(requireNotNull(settings?.sourceSets))
        outputFiles = getOutputFiles(project)
    }
}