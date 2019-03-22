package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Message(
    val lang: String,
    val speech: List<String>,
    val type: Int
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""
        {
          "type": $type,
          "lang": "$lang",
          "speech": [""")
        printer(speech.joinToString(separator = ",", postfix = "\n") { "\n            \"$it\"" })
        printer("          ]\n        }")
    }

    override fun minified(printer: Printer) {
        printer("""{"type":$type,"lang":"$lang","speech":[""")
        printer(speech.joinToString(separator = ",", postfix = "]}") { "\"$it\"" })
    }
}