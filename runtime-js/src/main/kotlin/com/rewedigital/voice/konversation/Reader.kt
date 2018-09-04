package com.rewedigital.voice.konversation

external fun require(module: String): dynamic

actual class Reader {
    actual fun loadAnswer(name: String): Prompt {
        val json = try {
            require("../$name.json")
        } catch (e: Throwable) {
            val request = XMLHttpRequest()
            request.open("GET", "$name.json", false)
            request.send(null)

            if (request.status == 200) {
                JSON.parse(request.responseText)
            } else {
                throw Error("HTTP-Error: " + request.status)
            }
        }
        return AnswerImpl(json)
    }
}

external class XMLHttpRequest {
    fun open(method: String, file: String, async: Boolean)
    fun send(payload: String?)

    val status: Int
    val responseText: String
}