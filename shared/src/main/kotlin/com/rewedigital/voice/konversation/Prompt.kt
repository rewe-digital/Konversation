package com.rewedigital.voice.konversation

open class Prompt(val parts: MutableList<Part> =  mutableListOf()) {
    constructor(part: Part) : this(mutableListOf(part))

    fun create(arguments : Map<String, Any> = mapOf()): String {
        return parts.joinToString(separator = " ") { it.variants[/*Random().next(it.variants.size)*/0] }
    }
}