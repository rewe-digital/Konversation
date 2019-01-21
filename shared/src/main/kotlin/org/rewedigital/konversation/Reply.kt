package org.rewedigital.konversation

/**
 * Each prompt consists of multiple [Part]s which can be for the display or just for the audio output.
 */
open class Reply(
    /** The parts of the response which should be build. */
    open val parts: MutableList<Part> = mutableListOf(),
    /** The suggestions which are used in context of the runtime. TODO this is for the Reply part only */
    open val suggestions: MutableList<String> = mutableListOf(),
    /** The reprompts which are used in context of the runtime. TODO this is for the Reply part only and should be just an Int instead of a String */
    open val reprompts: MutableMap<String, List<String>> = mutableMapOf()) {

    /** This is only constructor of the Prompt, the other are for the non yet existing Reply. */
    constructor(part: Part) : this(mutableListOf(part))
}