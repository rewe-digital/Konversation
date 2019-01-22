package org.rewedigital.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class AnswerImpl(
    override val parts: MutableList<Part> = mutableListOf(),
    override val suggestions: MutableList<String> = mutableListOf(),
    override val reprompts: MutableMap<String, List<String>> = mutableMapOf()) : Reply(parts, suggestions, reprompts)

@JsonClass(generateAdapter = true)
// TODO find a trick to make it internal and visible for the parser
data class PartImpl(override val variants: MutableList<String>,
                    override val type: PartType) : Part