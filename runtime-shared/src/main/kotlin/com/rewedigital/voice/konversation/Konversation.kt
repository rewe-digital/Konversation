package com.rewedigital.voice.konversation

//@JsQualifier("")
class Konversation(val name: String) {
    private val answer = Reader().loadAnswer(name)
    fun create() : String {
        val sb = StringBuilder()
        answer.parts.forEach { part : Part ->
            sb.append(part.variant[random.next(part.variant.size)])
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {
        val random = Random()
    }
}