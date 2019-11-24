package org.rewedigital.konversation

import java.io.File

interface VoiceAppConfig {
    var invocationName: String?
    var language: String?
    var invocationNames: MutableMap<String, String>
    val inputFiles: MutableList<File>
    var outputDirectory: File?
}