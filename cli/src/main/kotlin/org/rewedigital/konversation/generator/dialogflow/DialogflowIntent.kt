package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

data class DialogflowIntent(
    val auto: Boolean = true,
    val contexts: List<Any> = emptyList(),
    val events: List<String> = emptyList(),
    val fallbackIntent: Boolean = false,
    val id: UUID,
    val lastUpdate: Long,
    val name: String,
    val priority: Int = 500000,
    val responses: List<Response> = emptyList(),
    val webhookForSlotFilling: Boolean = false,
    val webhookUsed: Boolean = true
) : NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""{
  "id": "$id",
  "name": "$name",
  "auto": $auto,
  "contexts": [],
  "responses": [""")
        responses.forEachBreakable {
            printer("\n")
            it.prettyPrinted(printer)
            if (hasNext()) printer(",")
        }
        if (responses.isNotEmpty()) printer("\n  ")
        printer("""],
  "priority": $priority,
  "webhookUsed": $webhookUsed,
  "webhookForSlotFilling": $webhookForSlotFilling,
  "lastUpdate": $lastUpdate,
  "fallbackIntent": $fallbackIntent,
  "events": [""")
        printer(events.joinToString(separator = ",") { "\n    \"$it\"" })
        if (events.isNotEmpty()) printer("\n  ")
        printer("]\n}")
    }

    override fun minified(printer: Printer) {
        printer("""{"id":"$id","name":"$name","auto":$auto,"contexts":[],"responses":[""")
        responses.forEachBreakable {
            it.minified(printer)
            if (hasNext()) printer(",")
        }
        printer("""],"priority":$priority,"webhookUsed":$webhookUsed,"webhookForSlotFilling":$webhookForSlotFilling,"lastUpdate":$lastUpdate,"fallbackIntent":$fallbackIntent,"events":[""")
        printer(events.joinToString(separator = ",") { "\"$it\"" })
        printer("]}")
    }
}