package org.rewedigital.konversation

/**
 * The intent of the user, holding the utterances, prompts and so on.
 */
data class Intent(
    /** The name of this intent. Typically the generated kson file will have this file name. */
    val name: String,
    /** The utterances of this intent. That are the sentences the user can say to invoke this intent. */
    val utterances: MutableList<Utterance> = mutableListOf(),
    /** The prompt of this intent. That are the outputs what the voice application will say or show the user. */
    val prompt: Prompt = Prompt(),
    /** The reprompt which is said when the user gives no input. */
    val reprompt: MutableMap<Int, Prompt> = mutableMapOf(),
    /** The input context, for future usage. */
    var inContext: MutableList<String> = mutableListOf(),
    /** The output context, for future usage. */
    var outContext: MutableList<String> = mutableListOf(),
    /** The next logical intent, for future usage. */
    val followUp: MutableList<String> = mutableListOf(),
    /** The suggestions what the user can say next when using this intent. */
    val suggestions: MutableList<String> = mutableListOf(),
    /** The extras e.g. for UI Elements, for future usage. */
    val extras: MutableMap<String, Any> = mutableMapOf())