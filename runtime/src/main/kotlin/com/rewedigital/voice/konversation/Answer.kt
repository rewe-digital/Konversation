package com.rewedigital.voice.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AnswerImpl(parts: MutableList<Part>) : Prompt(parts)

@JsonClass(generateAdapter = true)
data class PartImpl(override val variant: MutableList<String>,
                    override val type: PartType) : Part

@JsonClass(generateAdapter = true)
data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
