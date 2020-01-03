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

    @Internal
    var settings: KonversationExtension? = null
        set(value) {
            field = requireNotNull(value) { "Settings must not be null" }
            // This static field will be overwritten multiple times with the same value
            Utterance.cacheDir = value.cacheDir
        }
    @InputFiles
    var inputFiles: List<File> = emptyList()
    @OutputDirectory
    var outputDirectory: File? = null

    @TaskAction
    fun executeTask() {
        workerExecutor.noIsolation().submit(workClass) {
            setupParameters(it, requireNotNull(settings) { "Settings must not be null" }, null)
        }
    }

    override fun getInputFiles(project: GradleProject) =
        project.inputFiles.resolveFiles() + project.dialogflow?.inputFiles.orEmpty().resolveFiles() + project.alexa?.inputFiles.orEmpty().resolveFiles()

    override fun getOutputFiles(project: GradleProject) = emptyList<File>()

    protected val KonversationExtension.inputFiles
        get() = projects.flatMap { (_, project) ->
            project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
        }.toHashSet().toList()
}