package com.rewedigital.voice.konversation

interface Answer {
    val parts: List<Part>
    val suggestions: List<Suggestion>
}

interface Part {
    val option: List<String>
    val type: PartType
}

interface Suggestion {
    val label: String
    val data: String
}

enum class PartType {
    Text,
    Voice
}