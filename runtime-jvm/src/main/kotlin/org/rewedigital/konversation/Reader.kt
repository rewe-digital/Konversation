package org.rewedigital.konversation

import com.squareup.moshi.Moshi

actual class Reader {
    actual fun loadReply(name: String, environment: Environment) : Reply {
        val adapter = Moshi.Builder()
            .add(AdapterFactory())
            .build()
            .adapter(AnswerImpl::class.java)
        return adapter.fromJson(javaClass.getResource("/$name.kson").readText())!!
    }
}