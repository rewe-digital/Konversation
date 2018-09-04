package com.rewedigital.voice.konversation

data class AnswerImpl(override val parts: List<Part>,
                      override val suggestions: List<Suggestion>) : Prompt {
    // small hack since an array cannot be converted to a list automatically
    constructor(json: dynamic) : this(
        (json.parts as Array<dynamic>).map { PartImpl(it) },
        (json.suggestions as Array<Suggestion>).toList()
                                     )
}

data class PartImpl(override val option: List<String>,
                    override val type: PartType) : Part {
    // small hack since an array cannot be converted to a list automatically
    constructor(json: dynamic) : this((json.option as Array<String>).toList(), PartType.valueOf(json.type as String))
}

data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
