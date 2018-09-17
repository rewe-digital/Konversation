package com.rewedigital.voice.konversation

external fun require(module: String): dynamic

actual class Reader {
    actual fun loadAnswer(name: String): Prompt {
        val json = try {
            //require("./$name.kson")
            val fs = require("fs")
            val content = fs.readFileSync("./$name.kson", "utf8") as String
            JSON.parse(content) as dynamic
        } catch (e: Throwable) {
            val request = XMLHttpRequest()
            request.open("GET", "$name.kson", false)
            request.send(null)

            if (request.status == 200) {
                JSON.parse(request.responseText)
            } else {
                throw Error("HTTP-Error: " + request.status)
            }
        }
        return PromptImpl(json)
    }
}

external class XMLHttpRequest {
    fun open(method: String, file: String, async: Boolean)
    fun send(payload: String?)

    val status: Int
    val responseText: String
}