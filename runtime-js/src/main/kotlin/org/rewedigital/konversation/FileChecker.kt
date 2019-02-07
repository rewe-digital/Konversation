package org.rewedigital.konversation

/**
 * Helper tool to verify if the given file exists.
 */
actual class FileChecker {
    /** Returns `true` if the file exists at the given path. */
    actual fun exists(path: String): Boolean = try {
        val fs = require("fs")
        fs.existsSync("./$path") as Boolean
    } catch (e: Throwable) {
        if (e.message?.startsWith("ENOENT:") == false) {
            val request = XMLHttpRequest()
            request.open("HEAD", path, false)
            request.send(null)

            request.status != 200
        } else {
            false
        }
    }
}