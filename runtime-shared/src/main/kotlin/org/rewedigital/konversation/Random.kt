package org.rewedigital.konversation

expect class Random() {
    fun next(max: Int): Int
}