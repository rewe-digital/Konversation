package com.rewedigital.voice.konversation.parts

class OptionPart(val isVoiceOnly: Boolean, val rawLine: String) {
    private var cache: List<String>? = null

    //val permutations: List<String>
    //    get() = cache ?: generatePermutations()
}