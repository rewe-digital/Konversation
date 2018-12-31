package com.rewedigital.voice.konversation

open class Prompt(val parts: MutableList<Part> = mutableListOf()) {
    constructor(part: Part) : this(mutableListOf(part))
}