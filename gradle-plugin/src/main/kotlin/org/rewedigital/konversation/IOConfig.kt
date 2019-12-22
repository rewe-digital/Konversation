package org.rewedigital.konversation

interface IOConfig {
    var invocationNames: MutableMap<String, String>
    var inputFiles: MutableList<String>
    var outputDirectory: String?
}

fun MutableMap<String, String>?.orUse(invocations: MutableMap<String, String>?) =
    if (this.isNullOrEmpty()) {
        invocations.orEmpty().toMutableMap()
    } else this