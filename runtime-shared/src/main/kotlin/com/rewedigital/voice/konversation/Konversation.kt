package com.rewedigital.voice.konversation

class Konversation(val name: String, private val environment: Environment) {
    private val answer = Reader().loadAnswer(name, environment)
    fun create(): String {
        val sb = StringBuilder()
        answer.parts
            .filter { it.type == PartType.Text || environment.voiceOnly }
            .forEach { part ->
                sb.append(part.variants[random.next(part.variants.size)])
            }
        return sb.toString()
    }

    companion object {
        val random = Random()
    }
}