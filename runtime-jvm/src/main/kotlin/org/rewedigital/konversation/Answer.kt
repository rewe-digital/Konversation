package org.rewedigital.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class AnswerImpl(
    override val parts: MutableList<Part> = mutableListOf(),
    override val suggestions: MutableList<String> = mutableListOf(),
    override val reprompts: MutableMap<String, List<String>> = mutableMapOf()) : Reply(parts, suggestions, reprompts)

@JsonClass(generateAdapter = true)
internal data class PartImpl(override val variants: MutableList<String>,
                    override val type: PartType) : Part