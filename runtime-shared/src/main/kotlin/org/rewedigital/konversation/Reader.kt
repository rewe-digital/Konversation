package org.rewedigital.konversation

expect class Reader() {
    fun loadAnswer(name: String, environment: Environment) : Prompt
}