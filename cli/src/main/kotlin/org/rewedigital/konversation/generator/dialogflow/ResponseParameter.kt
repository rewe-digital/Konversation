package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

data class ResponseParameter(
    val id: UUID,
    val dataType: String,
    val name: String,
    val value: String,
    val isList: Boolean
) : NodeExporter {
    constructor(slot: String) : this(
        id = UUID.nameUUIDFromBytes(slot.toByteArray()),
        dataType = "@${slot.slotName}",
        name = slot.slotType,
        value = "$${slot.slotType}",
        isList = false
    )

    override fun prettyPrinted(printer: Printer) =
        printer("""        {
          "id": "$id",
          "dataType": "$dataType",
          "name": "$name",
          "value": "$value",
          "isList": $isList
        }""")

    override fun minified(printer: Printer) =
        printer("""{"id":"$id","dataType":"$dataType","name":"$name","value":"$value","isList":$isList}""")
}

private val String.slotName: String
    get() = if (contains(':')) {
        substringAfter(':')
    } else {
        this
    }

private val String.slotType: String
    get() = if (contains(':')) {
        substringBefore(':')
    } else {
        this
    }