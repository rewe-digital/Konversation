package org.rewedigital.konversation

import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

@Suppress("UnstableApiUsage")
interface KonversationProjectParameters : WorkParameters {
    val project: Property<GradleProject>
}