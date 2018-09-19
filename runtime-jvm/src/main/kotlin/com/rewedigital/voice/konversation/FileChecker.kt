package com.rewedigital.voice.konversation

import java.io.File

actual class FileChecker {
    actual fun exists(path: String): Boolean = File(path).exists()
}