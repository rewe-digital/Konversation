package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.forEachIterator
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

data class DialogflowUtterance(
    val count: Int,
    val `data`: List<UtterancePart>,
    val id: UUID,
    val isTemplate: Boolean,
    val updated: Long) : NodeExporter {

    override fun prettyPrinted(printer: Printer) {
        printer("  {\n")
        printer("    \"id\": \"$id\",\n")
        printer("    \"data\": [")
        if (`data`.isNotEmpty()) {
            printer("\n")
            data.forEachIterator {
                it.prettyPrinted(printer)
                if (hasNext()) {
                    printer(",")
                }
                printer("\n")
            }
            printer("    ")
        }
        printer("],\n")
        printer("    \"isTemplate\": $isTemplate,\n")
        //printer("    \"updated\": $updated\n")
        printer("    \"count\": $count\n")
        printer("  }")
    }

    override fun minified(printer: Printer) {
        printer("""{"id":"$id","data":[""")
        data.forEachIterator {
            it.minified(printer)
            if (hasNext()) {
                printer(",")
            }
        }
        printer("""],"isTemplate":$isTemplate,"count":$count}""")
    }

    data class UtterancePart(
        val text: String,
        val alias: String? = null,
        val meta: String? = null,
        val userDefined: Boolean) : NodeExporter {

        override fun prettyPrinted(printer: Printer) {
            printer("      {\n")
            printer("        \"text\": \"$text\",\n")
            alias?.let {
                printer("        \"alias\": \"$alias\",\n")
            }
            meta?.let {
                printer("        \"meta\": \"$meta\",\n")
            }
            printer("        \"userDefined\": $userDefined\n")
            printer("      }")
        }

        override fun minified(printer: Printer) {
            printer("{\"text\":\"$text\"")
            alias?.let {
                printer(",\"alias\":\"$alias\"")
            }
            meta?.let {
                printer(",\"meta\":\"$meta\"")
            }
            printer(",\"userDefined\":$userDefined}")
        }
    }
}