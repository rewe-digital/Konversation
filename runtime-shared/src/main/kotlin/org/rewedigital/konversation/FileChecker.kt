package org.rewedigital.konversation

/**
 * Helper tool to verify if the given file exists.
 */
expect class FileChecker() {
    /** Returns `true` if the file exists at the given path. */
    fun exists(path: String): Boolean
}