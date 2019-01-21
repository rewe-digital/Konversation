package org.rewedigital.konversation

/**
 * One part of the [Reply] for the user. There can be two types: Text and VoiceOnly. The runtime will contact the parts and add separators (spaces).
 */
interface Part {
    /** The variants for this part. Please make sure that there are no grammatical issues when you use another alternative. */
    val variants: MutableList<String>
    /** The type of this part can be Text and VoiceOnly. */
    val type: PartType
}