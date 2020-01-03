package org.rewedigital.konversation.tasks

import org.rewedigital.konversation.GradleProject
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.KonversationProjectParameters
import java.io.File

// abstraction layer for simpler way to define the input and output files of the task
interface TaskSetupProvider {
    fun getInputFiles(project: GradleProject): List<File>
    fun getOutputFiles(project: GradleProject): List<File>
    fun setupParameters(actionParameters: KonversationProjectParameters, extensionSettings: KonversationExtension, projectName: String?)

    fun List<String>.resolveFiles(): List<File> = flatMap { path ->
        val file = File(path)
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
                throw IllegalArgumentException("Input file \"$path\" not found!")
        }
    }
}