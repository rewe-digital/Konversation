package org.rewedigital.konversation

/**
 * Abstraction layer for a random number generator.
 */
expect class Random() {
    /**
     * Returns a random number between 0 and [max].
     */
    fun next(max: Int): Int
}