package org.rewedigital.konversation

external fun require(module: String): dynamic

actual class Reader {
    actual fun loadReply(name: String, environment: Environment): Reply {
        val json = try {
            //require("./$name.kson")
            val fs = require("fs")
            val content = fs.readFileSync("./$name.kson", "utf8") as String
            JSON.parse(content) as dynamic
        } catch (e: Throwable) {
            if (e.message?.startsWith("ENOENT:") == false) {
                val request = XMLHttpRequest()
                request.open("GET", "$name.kson", false)
                request.send(null)

                if (request.status == 200) {
                    JSON.parse(request.responseText)
                } else {
                    throw Error("HTTP-Error: " + request.status)
                }
            } else {
                throw e
            }
        }
        return ReplyImpl(json)
    }
}

external class XMLHttpRequest {
    fun open(method: String, file: String, async: Boolean)
    fun send(payload: String?)

    val status: Int
    val responseText: String
}