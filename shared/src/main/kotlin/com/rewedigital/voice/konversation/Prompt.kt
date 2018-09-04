package com.rewedigital.voice.konversation

interface Prompt {
    val parts: List<Part>
    val suggestions: List<Suggestion>
}