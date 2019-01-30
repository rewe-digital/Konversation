package org.rewedigital.konversation

import com.squareup.moshi.Moshi

/**
 * Helper class to read files.
 */
actual class Reader {
    /**
     * Loads a reply for a given environment.
     * @param name The name of the reply.
     * @param environment The environment of the reply.
     */
    actual fun loadReply(name: String, environment: Environment) : Reply {
        val adapter = Moshi.Builder()
            .add(AdapterFactory())
            .build()
            .adapter(AnswerImpl::class.java)
        return adapter.fromJson(javaClass.getResource("/" + locator.locate(name, environment)).readText())!!
    }

    companion object {
        private val locator = ResourceLocator()
    }
}