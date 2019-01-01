package com.rewedigital.voice.konversation

open class Prompt(
    val parts: MutableList<Part> = mutableListOf(),
    val suggestions: List<String> = emptyList(),
    val reprompts: Map<String, List<String>> = mapOf()) {
    constructor(part: Part) : this(mutableListOf(part))
}