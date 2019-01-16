package org.rewedigital.konversation

actual class Formatter {
    actual fun format(format: String, vararg args: Any?) = String.format(format, *args)
}