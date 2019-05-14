package org.rewedigital.konversation

import kotlin.test.Test

class KonversationJsTest {
    @Test
    fun bla() {
        val konversation = KonversationJs("Help", Environment(platform = "", locale = ""))
        val output = konversation.createJavaScriptOutput(JSON.parse("""{"a":1, "b":"bb"}"""))
        println(output)
    }
}