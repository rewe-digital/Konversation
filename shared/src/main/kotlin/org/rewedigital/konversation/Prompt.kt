package org.rewedigital.konversation

open class Prompt(
    open val parts: MutableList<Part> = mutableListOf(),
    open val suggestions: MutableList<String> = mutableListOf(),
    open val reprompts: MutableMap<String, List<String>> = mutableMapOf()) {
    constructor(part: Part) : this(mutableListOf(part))
}