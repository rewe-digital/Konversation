package com.rewedigital.voice.konversation

interface Part {
    val variant: MutableList<String>
    val type: PartType
}