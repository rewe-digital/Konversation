package org.rewedigital.konversation

/**
 * Abstraction layer to format a string. This function just provides a platform specific `sprintf()` implementation.
 */
expect class Formatter() {
    /**
     * Uses the provided [format] as a format string and returns a string obtained by substituting the specified arguments,
     * using the locale of the environment.
     *
     * @param format The string to format
     * @param args The arguments you want to apply.
     */
    fun format(locale: String, format: String, vararg args: Any?): String
}