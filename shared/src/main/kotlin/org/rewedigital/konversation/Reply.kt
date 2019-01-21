package org.rewedigital.konversation

/**
 * Each Reply contains multiple [Part]s, suggestions and reprompts. The parts can be for the display or just for the audio output.
 */
open class Reply(
    /** The parts of the response which should be build. */
    open val parts: MutableList<Part> = mutableListOf(),
    /** The suggestions which are used in context of the runtime. */
    open val suggestions: MutableList<String> = mutableListOf(),
    /** The reprompts which are used in context of the runtime. */
    open val reprompts: MutableMap<String, List<String>> = mutableMapOf())