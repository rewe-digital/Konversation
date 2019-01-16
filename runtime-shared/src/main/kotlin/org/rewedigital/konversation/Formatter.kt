package org.rewedigital.konversation

expect class Formatter() {
    fun format(format: String, vararg args: Any?): String
}