package org.rewedigital.konversation

import java.io.File

interface IOConfig {
    var invocationNames: MutableMap<String, String>
    var inputFiles: MutableList<File>
    var outputDirectory: File?
}

fun MutableMap<String, String>?.orUse(invocations: MutableMap<String, String>?) =
    if (this.isNullOrEmpty()) {
        invocations.orEmpty().toMutableMap()
    } else this