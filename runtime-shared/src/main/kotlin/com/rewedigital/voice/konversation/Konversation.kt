package com.rewedigital.voice.konversation

class Konversation(conversation: String) {
    private val answer = Reader().loadAnswer(conversation)
    fun create() : String {
        val sb = StringBuilder()
        answer.parts.forEach { part ->
            sb.append(part.option[random.next(part.option.size)])
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {
        val random = Random()
    }
}