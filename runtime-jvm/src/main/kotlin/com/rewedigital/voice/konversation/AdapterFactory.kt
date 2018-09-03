package com.rewedigital.voice.konversation

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class AdapterFactory : Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? =
        when (type) {
            Answer::class.java -> AnswerImplJsonAdapter(moshi)
            Part::class.java -> PartImplJsonAdapter(moshi)
            Suggestion::class.java -> SuggestionImplJsonAdapter(moshi)
            else -> null
        }
}