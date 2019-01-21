package org.rewedigital.konversation

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonAdapter.Factory
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

/**
 * [Factory] for [Moshi] to create instances of [Reply] and [Part].
 */
internal class AdapterFactory : Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi): JsonAdapter<*>? =
        when (type) {
            Reply::class.java -> AnswerImplJsonAdapter(moshi)
            Part::class.java -> PartImplJsonAdapter(moshi)
            else -> null
        }
}