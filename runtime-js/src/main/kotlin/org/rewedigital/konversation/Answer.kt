package org.rewedigital.konversation

/**
 * The internal implementation of a concrete reply.
 */
internal class ReplyImpl(json: dynamic) : org.rewedigital.konversation.Reply((json.parts as Array<dynamic>).map { PartImpl(it) }.toMutableList())

/**
 * The internal implementation of a concrete part.
 */
internal data class PartImpl(override val variants: MutableList<String>,
                    override val type: PartType) : Part {
    // small hack since an array cannot be converted to a list automatically
    constructor(json: dynamic) : this((json.variants as Array<String>).toMutableList(), PartType.valueOf(json.type as String))
}