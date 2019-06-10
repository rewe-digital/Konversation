package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Message(
    val lang: String,
    val speech: List<String>
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""
        {
          "type": 0,
          "lang": "$lang",
          "speech": [""")
        printer(speech.joinToString(separator = ",", postfix = "\n") { "\n            \"${it.escape()}\"" })
        printer("          ]\n        }")
    }

    override fun minified(printer: Printer) {
        printer("""{"type":0,"lang":"$lang","speech":[""")
        printer(speech.joinToString(separator = ",", postfix = "]}") { "\"${it.escape()}\"" })
    }

    private fun String.escape() =
        replace("\\", "\\\\")
            .replace("\r", "\\r")
            .replace("\n", "\\n")
            .replace("\"", "\\")
}