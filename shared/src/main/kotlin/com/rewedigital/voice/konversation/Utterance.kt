package com.rewedigital.voice.konversation

interface Utterance {
    val slotTypes: MutableList<String>
    val permutationCount: Long
    val permutations: MutableSet<String>
}