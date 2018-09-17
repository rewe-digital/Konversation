package com.rewedigital.voice.konversation.generator.kson

import com.rewedigital.voice.konversation.Intent
import com.rewedigital.voice.konversation.forEachIterator
import com.rewedigital.voice.konversation.generator.Exporter
import com.rewedigital.voice.konversation.generator.Printer

class KsonExporter(private val filter: String) : Exporter {

    override fun prettyPrinted(printer: Printer, intents: MutableList<Intent>) {
        printer("{\r\n")
        printer("  \"parts\": [\r\n")
        intents.first { it.name == filter }.prompt.parts.forEachIterator { part ->
            printer("    {\r\n")
            printer("      \"type\": \"${part.type}\",\r\n")
            printer("      \"variants\": [\r\n")
            printer(part.variant.joinToString(separator = ",\n        ", prefix = "        ", postfix = "\r\n      ]\r\n") {
                "\"${it.replace("\n","\\n").replace("\r","\\r")}\""
            })
            printer("    }")
            if(hasNext()) {
                printer(",")
            }
            printer("\r\n")
        }
        printer("  ]\r\n")
        printer("}")
    }

    override fun minified(printer: Printer, intents: MutableList<Intent>) {
        // TODO
        prettyPrinted(printer, intents)
    }
}