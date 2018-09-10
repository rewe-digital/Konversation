package com.rewedigital.voice.konversation

open class Prompt(val parts: MutableList<Part> =  mutableListOf()) {
    constructor(part: Part) : this(mutableListOf(part))

    fun create(): String {
        return parts.joinToString(separator = " ") { it.variant[0] } // FIXME
    }
}