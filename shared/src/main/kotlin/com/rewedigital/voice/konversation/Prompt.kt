package com.rewedigital.voice.konversation

open class Prompt(val parts: List<Part>) {
    fun create(): String {
        return parts.joinToString { it.variant[0] } // FIXME
    }
}