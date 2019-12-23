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
abstract class AbstractExportTask @Inject constructor(
    private var workerExecutor: WorkerExecutor,
    private val workClass: Class<out WorkAction<KonversationProjectParameters>>) : DefaultTask(), TaskSetupProvider {

    @Input
    var projectName: String? = null
        set(value) {
            field = requireNotNull(value) { "Project name must not be null" }
            settings?.projects?.get(projectName)?.let { project ->
                inputFiles = getInputFiles(project)
                outputFiles = getOutputFiles(project)
            }
        }
    @Internal
    var settings: KonversationExtension? = null
        set(value) {
            field = requireNotNull(value) { "Settings must not be null" }
            value.projects[projectName]?.let { project ->
                inputFiles = getInputFiles(project)
                outputFiles = getOutputFiles(project)
            }
            // This static field will be overwritten multiple times with the same value
            Utterance.cacheDir = value.cacheDir
        }
    @InputFiles
    var inputFiles: List<File> = emptyList()
        private set
    @OutputFiles
    var outputFiles: List<File> = emptyList()
        private set

    @TaskAction
    fun executeTask() {
        workerExecutor.noIsolation().submit(workClass) {
            setupParameters(it, requireNotNull(settings) { "Settings must not be null" }, requireNotNull(projectName) { "Project name must not be null" })
        }
    }
}

interface TaskSetupProvider {
    // abstraction layer for simpler way to define the input and output files of the task
    fun getInputFiles(project: GradleProject): List<File>

    fun getOutputFiles(project: GradleProject): List<File>
    fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, projectName: String)
}