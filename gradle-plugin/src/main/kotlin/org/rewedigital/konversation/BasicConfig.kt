package org.rewedigital.konversation

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import java.io.File
import java.util.*

abstract class BasicConfig(project: Project) : ExtensionAware, VoiceAppConfig {
    override var invocationNames = mutableMapOf<Locale, String>()
    @InputFiles
    override val inputFiles = mutableListOf<File>()
    @OutputDirectory
    override var outputDirectory: File? = File(project.buildDir.path, "konversation")
}