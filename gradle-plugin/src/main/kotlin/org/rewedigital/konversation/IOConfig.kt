package org.rewedigital.konversation

import java.io.File

interface IOConfig {
    var invocationNames: MutableMap<String, String>
    val inputFiles: MutableList<File>
    var outputDirectory: File?
}