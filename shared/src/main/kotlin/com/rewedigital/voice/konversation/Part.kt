package com.rewedigital.voice.konversation

interface Part {
    val variants: MutableList<String>
    val type: PartType
}