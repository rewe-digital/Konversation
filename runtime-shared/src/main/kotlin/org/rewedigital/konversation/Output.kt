package org.rewedigital.konversation

/**
 * Holds a static output of a reply.
 */
data class Output(
    /** The display test. */
    val displayText: String,
    /** The Speech Synthesis Markup Language, the spoken output. */
    val ssml: String,
    /** The reprompts which should the user hear when the user gives no input. */
    val reprompts: Map<Int, String>,
    /** The list of suggestions which should been displayed. */
    val suggestions: List<String>,
    /** The strings for UI elements */
    val extras: Map<String, String>) {
    operator fun plus(output: Output) = Output(
        displayText = displayText + "\n" + output.displayText,
        ssml = ssml.removeSuffix("</speak>") + "\n" + output.ssml.removePrefix("<speak>"),
        extras = extras + output.extras,
        reprompts = reprompts + output.reprompts,
        suggestions = suggestions + output.suggestions)
}