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
    constructor(slot: String, isList: Boolean) : this(
        id = UUID.nameUUIDFromBytes(slot.toByteArray()),
        dataType = "@${slot.slotType}",
        name = slot.slotName,
        value = "$${slot.slotName}",
        isList = isList
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

private val String.slotType: String
    get() = if (contains(':')) {
        substringAfter(':')
    } else {
        this
    }.removePrefix("AMAZON.")
        .asSystemType()

private val String.slotName: String
    get() = if (contains(':')) {
        substringBefore(':')
    } else {
        this
    }

private fun String.asSystemType() =
    when (this) {
        "any" -> "sys.any"
        "number" -> "sys.number"
        "ordinal" -> "sys.ordinal"
        "color" -> "sys.color"
        "de-city" -> "sys.geo-city"
        "at-city" -> "sys.geo-city"
        "eu-city" -> "sys.geo-city"
        "us-city" -> "sys.geo-city"
        "gb-city" -> "sys.geo-city"
        else -> this
    }