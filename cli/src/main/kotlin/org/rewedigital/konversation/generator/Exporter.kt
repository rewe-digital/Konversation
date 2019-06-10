package org.rewedigital.konversation.generator

import org.rewedigital.konversation.Entities
import org.rewedigital.konversation.Intent
import java.io.OutputStream

typealias Printer = (output: String) -> Unit

interface Exporter {
    fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?)
    fun minified(printer: Printer, intents: List<Intent>, entities: List<Entities>?)
}

interface StreamExporter {
    fun prettyPrinted(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?)
    fun minified(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?)
}

interface NodeExporter {
    fun prettyPrinted(printer: Printer)
    fun minified(printer: Printer)
}