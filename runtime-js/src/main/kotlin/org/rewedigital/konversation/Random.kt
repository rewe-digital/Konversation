package org.rewedigital.konversation

/**
 * JavaScript implementation for a random number generator.
 */
actual class Random {
    /**
     * Returns a random number between 0 and [max].
     */
    actual fun next(max: Int): Int = (Math.random() * max).toInt()
}

internal external class Math {
    companion object {
        fun random(): Double
    }
}