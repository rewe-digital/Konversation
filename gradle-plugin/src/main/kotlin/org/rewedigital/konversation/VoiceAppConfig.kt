package org.rewedigital.konversation

import java.io.File
import java.util.*

interface VoiceAppConfig {
    var invocationNames: MutableMap<Locale, String>
    val inputFiles: MutableList<File>
    var outputDirectory: File?
}