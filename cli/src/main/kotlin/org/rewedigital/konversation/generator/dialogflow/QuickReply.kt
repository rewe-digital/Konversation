package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class QuickReply(
    val lang: String,
    val replies: List<String>
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""
        {
          "type": 2,
          "lang": "$lang",
          "replies": [""")
        printer(replies.joinToString(separator = ",", postfix = "\n") { "\n            \"${it.escape()}\"" })
        printer("          ]\n        }")
    }

    override fun minified(printer: Printer) {
        printer("""{"type":2,"lang":"$lang","replies":[""")
        printer(replies.joinToString(separator = ",", postfix = "]}") { "\"${it.escape()}\"" })
    }

    private fun String.escape() =
        replace("\\", "\\\\")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\"", "\\")
}