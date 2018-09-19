package com.rewedigital.voice.konversation

expect class FileChecker() {
    fun exists(path: String): Boolean
}