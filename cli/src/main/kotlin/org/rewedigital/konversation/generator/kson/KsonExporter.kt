package org.rewedigital.konversation.generator.kson

import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachIterator
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer

class KsonExporter(private val filter: String) : Exporter {

    override fun prettyPrinted(printer: Printer, intents: MutableList<Intent>) {
        printer("{\n")
        printer("  \"parts\": [\n")
        val intent = intents.first { it.name == filter }
        intent.prompt.forEachIterator { part ->
            printer("    {\n")
            printer("      \"type\": \"${part.type}\",\n")
            printer("      \"variants\": [")
            if(part.variants.isEmpty()) {
                printer("]")
            }
            printer("\n")
            printer(part.variants.joinToString(separator = ",\n        ", prefix = "        ", postfix = "\n      ]\n") {
                "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
            })
            printer("    }")
            if (hasNext()) {
                printer(",")
            }
            printer("\n")
        }
        printer("  ],\n")
        printer("  \"suggestions\": [")
        if(intent.suggestions.isEmpty()) {
            printer("]")
        }
        printer("\n")
        printer(intent.suggestions.joinToString(separator = ",\n    ", prefix = "    ", postfix = "\n  ],\n") {
            "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
        })
        printer("  \"reprompts\": {")
        if(intent.suggestions.isEmpty()) {
            printer("}")
        }
        printer("\n")
        printer(intent.reprompt.entries.joinToString(separator = ",\n", postfix = "\n  }\n") { entry ->
            entry.value.first().variants.joinToString(separator = ",\n      ", prefix = "    \"${entry.key}\": [\n      ", postfix = "\n    ]") {
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