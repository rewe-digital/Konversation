package org.rewedigital.konversation

expect class Reader() {
    fun loadReply(name: String, environment: Environment) : Reply
}