package org.rewedigital.konversation

import java.io.File

actual class FileChecker {
    actual fun exists(path: String): Boolean = File(path).exists()
}