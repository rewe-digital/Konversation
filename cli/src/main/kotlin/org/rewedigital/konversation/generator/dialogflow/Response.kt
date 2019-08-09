package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Response(
    val action: String,
    val messages: List<NodeExporter>,
    val parameters: List<ResponseParameter>,
    val resetContexts: Boolean = false
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""    {
      "resetContexts": $resetContexts,
      "action": "$action",
      "affectedContexts": [],
      "parameters": [""")
        if(parameters.isNotEmpty()) {
            printer("\n")
            parameters.forEachBreakable {
                it.prettyPrinted(printer)
                if (hasNext()) printer(",")
                printer("\n")
            }
            printer("      ")
        }
        printer("""],
      "messages": [""")
        messages.forEachBreakable {
            it.prettyPrinted(printer)
            if (hasNext()) printer(",")
            printer("\n")
        }
        printer("""      ],
      "defaultResponsePlatforms": {},
      "speech": []
    }""")
    }

    override fun minified(printer: Printer) {
        printer("""{"resetContexts":$resetContexts,"action":"$action","affectedContexts":[],"parameters":[""")
        parameters.forEachBreakable {
            it.minified(printer)
            if (hasNext()) printer(",")
        }
        printer("""],"messages":[""")
        messages.forEachBreakable {
            it.minified(printer)
            if (hasNext()) printer(",")
        }
        printer("""],"defaultResponsePlatforms":{},"speech":[]}""")
    }
}