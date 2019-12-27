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
}