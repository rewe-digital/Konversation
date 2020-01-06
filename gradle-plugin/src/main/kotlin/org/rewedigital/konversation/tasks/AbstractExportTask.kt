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
    private val workClass: Class<out WorkAction<KonversationProjectParameters>>) : DefaultTask() {

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
    var inputFiles: List<File> = emptyList()
        get() = settings?.inputFiles ?: field
    @OutputDirectory
    var outputDirectory: File? = null

    @TaskAction
    fun executeTask() {
        workerExecutor.noIsolation().submit(workClass) { actionParameters ->
            setupParameters(actionParameters, requireNotNull(settings) { "Settings must not be null" })
        }
    }

    abstract fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension)

    protected val KonversationExtension.inputFiles
        get() = (projects.flatMap { (_, project) ->
            project.inputFiles + project.dialogflow?.inputFiles.orEmpty() + project.alexa?.inputFiles.orEmpty()
        } + attentionalNonExportedFiles).toHashSet().resolveFiles(sourceSets)

    private fun Iterable<String>.resolveFiles(sourceSets: List<File>) = flatMap { path ->
        sourceSets.flatMap { baseDir ->
            val file = File(baseDir, path)
            when {
                path.contains('*') && file.parentFile.exists() -> {
                    val matcher = file.name.replace(".", "\\.").replace("*", ".*?").toRegex()
                    file.parentFile.listFiles { _, name ->
                        matcher.matches(name)
                    }.orEmpty().toList()
                }
                file.exists() ->
                    listOf(file)
                else ->
                    emptyList()
            }
        }.also { result ->
            if (result.isEmpty()) {
                throw IllegalArgumentException("Input file \"$path\" not found in any source set (${sourceSets.joinToString { it.absolutePath }})!")
            }
        }
    }
}