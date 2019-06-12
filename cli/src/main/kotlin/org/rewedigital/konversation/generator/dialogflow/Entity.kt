package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Entity(
    val value: String,
    val synonyms: List<String>) : NodeExporter {

    override fun prettyPrinted(printer: Printer) {
        printer("  {\n")
        printer("    \"value\": \"$value\",\n")
        printer("    \"synonyms\": [\n")
        printer((listOf(value) + synonyms).joinToString(separator = ",\n", postfix = "\n    ]") { "      \"$it\"" })
        printer("\n  }")
    }

    override fun minified(printer: Printer) {
        printer("{\"value\":\"$value\",\"synonyms\":[")
        printer((listOf(value) + synonyms).joinToString(separator = ",", postfix = "]}") { "\"$it\"" })
    }
}