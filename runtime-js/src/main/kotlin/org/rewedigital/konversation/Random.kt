package org.rewedigital.konversation

/**
 * JavaScript implementation for a random number generator.
 */
actual class Random {
    /**
     * Returns a random number between 0 and [max].
     */
    actual fun next(max: Int): Int = forcedValue ?: (Math.random() * max).toInt()

    companion object {
        // Just for testing!
        @JsName("forcedValue")
        internal var forcedValue: Int? = null
    }
}

internal external class Math {
    companion object {
        fun random(): Double
    }
}