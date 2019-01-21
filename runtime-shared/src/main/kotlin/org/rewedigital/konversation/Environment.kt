package org.rewedigital.konversation

/**
 * The environment holds data about the environment where is voice application is running.
 */
data class Environment(
    /** The platform for the reply should be optimized. */
    val platform: String,
    /** The locale the for the output. */
    val locale: String)