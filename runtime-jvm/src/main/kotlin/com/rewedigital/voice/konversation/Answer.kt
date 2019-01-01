package com.rewedigital.voice.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AnswerImpl(parts: MutableList<Part>, suggestions: List<String>, reprompts: Map<String, List<String>>) : Prompt(parts, suggestions, reprompts)

@JsonClass(generateAdapter = true)
data class PartImpl(override val variants: MutableList<String>,
                    override val type: PartType) : Part

@JsonClass(generateAdapter = true)
data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
