package org.rewedigital.konversation.generator.kson

import org.rewedigital.konversation.Entities
import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachIterator
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer

class KsonExporter(private val filter: String) : Exporter {

    override fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        printer("{\n")
        printer("  \"parts\": [\n")
        val intent = intents.first { it.name == filter }
        intent.prompt.forEachIterator { part ->
            printer("    {\n")
            printer("      \"type\": \"${part.type}\",\n")
            printer("      \"variants\": [\n")
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
        if (intent.suggestions.isEmpty()) {
            printer("],\n")
        } else {
            printer("\n")
            printer(intent.suggestions.joinToString(separator = ",\n    ", prefix = "    ", postfix = "\n  ],\n") {
                "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
            })
        }
        printer("  \"reprompts\": {")
        if (intent.reprompt.isEmpty()) {
            printer("}\n")
        } else {
            printer("\n")
            printer(intent.reprompt.entries.joinToString(separator = ",\n", postfix = "\n  }\n") { entry ->
                entry.value.first().variants.joinToString(separator = ",\n      ", prefix = "    \"${entry.key}\": [\n      ", postfix = "\n    ]") {
                    "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
                }
            })
        }

        printer("}")
    }

    override fun minified(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        printer("{\"parts\":[")
        val intent = intents.first { it.name == filter }
        intent.prompt.forEachIterator { part ->
            printer("{\"type\":\"${part.type}\",\"variants\":[")
            printer(part.variants.joinToString(separator = ",", postfix = "]") {
                "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
            })
            printer("}")
            if (hasNext()) {
                printer(",")
            }
        }
        printer("],\"suggestions\":[")
        printer(intent.suggestions.joinToString(separator = ",", postfix = "],") {
            "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
        })
        printer("\"reprompts\":{")
        printer(intent.reprompt.entries.joinToString(separator = ",", postfix = "}") { entry ->
            entry.value.first().variants.joinToString(separator = ",", prefix = "\"${entry.key}\":[", postfix = "]") {
                "\"${it.replace("\n", "\\n").replace("\r", "\\r").replace("\"", "\\\" ")}\""
            }
        })

        printer("}")
    }
}