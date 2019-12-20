package org.rewedigital.konversation

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters
import java.io.File

@Suppress("UnstableApiUsage")
interface KonversationProjectParameters : WorkParameters {
    val project: Property<GradleProject>
    val enumPackageName: Property<String>
    val inputFiles: ListProperty<File>
    val enumFile: Property<File>
}