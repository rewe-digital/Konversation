package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.generator.NodeExporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

data class EntityMetaData(
    val automatedExpansion: Boolean,
    val id: UUID,
    val isEnum: Boolean,
    val isOverridable: Boolean,
    val name: String): NodeExporter {
    override fun prettyPrinted(printer: Printer) {
        printer("""{
  "id": "$id",
  "name": "$name",
  "isOverridable": $isOverridable,
  "isEnum": $isEnum,
  "automatedExpansion": $automatedExpansion
}""")
    }

    override fun minified(printer: Printer) {
        printer("""{"id":"$id","name":"$name","isOverridable":$isOverridable,"isEnum":$isEnum,"automatedExpansion":$automatedExpansion}""")
    }
}