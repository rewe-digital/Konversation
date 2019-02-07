package org.rewedigital.konversation

internal external fun require(module: String): dynamic

/**
 * Helper class to read files.
 */
actual class Reader {
    /**
     * Loads a reply for a given environment.
     * @param name The name of the reply.
     * @param environment The environment of the reply.
     */
    actual fun loadReply(name: String, environment: Environment): Reply {
        val json = try {
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

internal external class XMLHttpRequest {
    fun open(method: String, file: String, async: Boolean)
    fun send(payload: String?)

    val status: Int
    val responseText: String
}