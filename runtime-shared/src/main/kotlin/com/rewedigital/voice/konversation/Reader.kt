package com.rewedigital.voice.konversation

expect class Reader() {
    fun loadAnswer(name: String, environment: Environment) : Prompt
}