package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer

data class Response(
    val action: String,
    //val affectedContexts: List<Any>,
    //val defaultResponsePlatforms: DefaultResponsePlatforms,
    val messages: List<Message>,
    //val parameters: List<Any>,
    val resetContexts: Boolean = false
    //val speech: List<Any> = emptyList()
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""    {
      "resetContexts": $resetContexts,
      "action": "$action",
      "affectedContexts": [],
      "parameters": [],
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
        printer("""{"resetContexts":$resetContexts,"action":"$action","affectedContexts":[],"parameters":[],"messages":[""")
        messages.forEachBreakable {
            it.minified(printer)
            if (hasNext()) printer(",")
        }
        printer("""],"defaultResponsePlatforms":{},"speech":[]}""")
    }
}