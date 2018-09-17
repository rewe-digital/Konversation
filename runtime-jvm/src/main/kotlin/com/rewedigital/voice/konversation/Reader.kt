package com.rewedigital.voice.konversation

import com.squareup.moshi.Moshi

actual class Reader {
    actual fun loadAnswer(name: String) : Prompt {
        val adapter = Moshi.Builder()
            .add(AdapterFactory())
            .build()
            .adapter(AnswerImpl::class.java)
        return adapter.fromJson(javaClass.getResource("/$name.kson").readText())!!
    }
}