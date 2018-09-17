package com.rewedigital.voice.konversation

class PromptImpl(json: dynamic) : com.rewedigital.voice.konversation.Prompt((json.parts as Array<dynamic>).map { PartImpl(it) }.toMutableList<Part>())

data class PartImpl(override val variant: MutableList<String>,
                    override val type: PartType) : Part {
    // small hack since an array cannot be converted to a list automatically
    constructor(json: dynamic) : this((json.variants as Array<String>).toMutableList(), PartType.valueOf(json.type as String))
}

data class SuggestionImpl(override val label: String,
                          override val data: String) : Suggestion
