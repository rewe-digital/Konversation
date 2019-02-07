package org.rewedigital.konversation.generator

import org.rewedigital.konversation.Intent

typealias Printer  = (output: String) -> Unit
interface Exporter {
    fun prettyPrinted(printer: Printer, intents: MutableList<Intent>)
    fun minified(printer: Printer, intents: MutableList<Intent>)
}
interface NodeExporter {
    fun prettyPrinted(printer: Printer)
    fun minified(printer: Printer)
}