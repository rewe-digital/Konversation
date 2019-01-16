package org.rewedigital.konversation

expect class FileChecker() {
    fun exists(path: String): Boolean
}