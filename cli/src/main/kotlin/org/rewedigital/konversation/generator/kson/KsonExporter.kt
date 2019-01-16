package org.rewedigital.konversation.generator.kson

import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachIterator
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer

class KsonExporter(private val filter: String) : Exporter {

    override fun prettyPrinted(printer: Printer, intents: MutableList<Intent>) {
        printer("{\r\n")
        printer("  \"parts\": [\r\n")
        val intent = intents.first { it.name == filter }
        intent.prompt.parts.forEachIterator { part ->
            printer("    {\r\n")
            printer("      \"type\": \"${part.type}\",\r\n")
            printer("      \"variants\": [\r\n")
            printer(part.variants.joinToString(separator = ",\r\n        ", prefix = "        ", postfix = "\r\n      ]\r\n") {
                "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
            })
            printer("    }")
            if (hasNext()) {
                printer(",")
            }
            printer("\r\n")
        }
        printer("  ],\r\n")
        printer("  \"suggestions\": [\r\n")
        printer(intent.suggestions.joinToString(separator = ",\r\n    ", prefix = "    ", postfix = "\r\n  ],\r\n") {
            "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
        })
        printer("  \"reprompts\": {\r\n")
        printer(intent.reprompt.entries.joinToString(separator = ",\r\n", postfix = "\r\n  }\r\n") { entry ->
            entry.value.parts.first().variants.joinToString(separator = ",\r\n      ", prefix = "    \"${entry.key}\": [\r\n      ", postfix = "\r\n    ]") {
            "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
        }
        })

        printer("}")
    }

    override fun minified(printer: Printer, intents: MutableList<Intent>) {
        // TODO
        prettyPrinted(printer, intents)
    }
}