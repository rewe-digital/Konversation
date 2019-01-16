package org.rewedigital.konversation

data class Output(
    val displayText: String,
    val ssml: String,
    val reprompts : Map<Int, String>,
    val suggestions: List<String>,
    val extras: Map<String, String>)