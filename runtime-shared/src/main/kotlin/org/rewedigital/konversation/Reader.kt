package org.rewedigital.konversation

/**
 * Helper class to read files.
 */
expect class Reader() {
    /**
     * Loads a reply for a given environment.
     * @param name The name of the reply.
     * @param environment The environment of the reply.
     */
    fun loadReply(name: String, environment: Environment) : Reply
}