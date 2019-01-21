package org.rewedigital.konversation

import java.util.Random

/**
 * Java implementation for a random number generator.
 */
actual class Random actual constructor() {
    /**
     * Returns a random number between 0 and [max].
     */
    actual fun next(max: Int): Int = forcedValue ?: Random().nextInt(max)

    companion object {
        // Just for testing!
        internal var forcedValue: Int? = null
    }
}