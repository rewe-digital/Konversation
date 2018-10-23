package com.rewedigital.voice.konversation

actual class Formatter {
    actual fun format(format: String, vararg args: Any?): String {
        val sprintf = require("sprintf-js").sprintf
        return sprintf.format(format, args)
    }
}