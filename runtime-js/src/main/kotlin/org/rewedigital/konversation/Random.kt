package org.rewedigital.konversation

actual class Random {
    actual fun next(max: Int): Int = (Math.random() * max).toInt()
}

external class Math {
    companion object {
        fun random(): Double
    }
}