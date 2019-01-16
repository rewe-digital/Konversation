package org.rewedigital.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AnswerImpl(
    override val parts: MutableList<Part> = mutableListOf(),
    override val suggestions: MutableList<String> = mutableListOf(),
    override val reprompts: MutableMap<String, List<String>> = mutableMapOf()) : Prompt(parts, suggestions, reprompts)

@JsonClass(generateAdapter = true)
data class PartImpl(override val variants: MutableList<String>,
                    override val type: PartType) : Part

@JsonClass(generateAdapter = true)
data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
