package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkerExecutor
import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import org.rewedigital.konversation.parser.Utterance
import java.io.File
import javax.inject.Inject

@CacheableTask
@Suppress("UnstableApiUsage")
abstract class AbstractProjectExportingTask @Inject constructor(
    private var workerExecutor: WorkerExecutor,
    private val workClass: Class<out WorkAction<KonversationProjectParameters>>) : DefaultTask(), TaskSetupProvider {

    @Internal
    var settings: KonversationExtension? = null
        set(value) {
            field = requireNotNull(value) { "Settings must not be null" }
            // This static field will be overwritten multiple times with the same value
            Utterance.cacheDir = value.cacheDir
        }
    @Internal
    var project: GradleProject? = null
    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    val inputFiles: List<File> = emptyList()
        get() = project?.let(::getInputFiles)?.resolveFiles(settings?.sourceSets ?: error("Foo")) ?: field
    @OutputFiles
    val outputFiles: List<File> = emptyList()
        get() = project?.let(::getOutputFiles) ?: field

    @TaskAction
    fun executeTask() {
        workerExecutor.noIsolation().submit(workClass) {
            setupParameters(it, requireNotNull(settings) { "Settings must not be null" }, requireNotNull(project) { "Project must not be null" })
        }
    }
}