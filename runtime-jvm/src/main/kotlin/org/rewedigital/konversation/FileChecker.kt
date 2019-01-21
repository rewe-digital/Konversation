package org.rewedigital.konversation

import java.io.File

/**
 * Helper tool to verify if the given file exists.
 */
actual class FileChecker {
    /** Returns `true` if the file exists at the given path. */
    actual fun exists(path: String): Boolean = File(path).exists()
}