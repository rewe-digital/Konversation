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
    val reprompts : Map<Int, String>,
    /** The list of suggestions which should been displayed. */
    val suggestions: List<String>,
    /** The strings for UI elements */
    val extras: Map<String, String>)