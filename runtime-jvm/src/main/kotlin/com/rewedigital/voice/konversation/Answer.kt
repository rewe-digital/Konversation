package com.rewedigital.voice.konversation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnswerImpl(override val parts: List<Part>,
                      override val suggestions: List<Suggestion>) : Prompt

@JsonClass(generateAdapter = true)
data class PartImpl(override val variant: List<String>,
                    override val type: PartType) : Part

@JsonClass(generateAdapter = true)
data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
