package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Entity(
    val value: String,
    val synonyms: List<String>) : NodeExporter {

    override fun prettyPrinted(printer: Printer) {
        printer("  {\n")
        printer("    \"value\": \"$value\",\n")
        printer("    \"synonyms\": [")
        if (synonyms.isNotEmpty()) {
            printer("\n")
            printer(synonyms.joinToString(separator = ",\n", postfix = "\n    ]") { "      \"$it\"" })
        } else {
            printer("]")
        }
        printer("\n  }\n")
    }

    override fun minified(printer: Printer) {
        printer("{\"value\":\"$value\",\"synonyms\":")
        printer(synonyms.joinToString(separator = ",", prefix = "[", postfix = "]}") { "\"$it\"" })
    }
}