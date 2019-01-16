package org.rewedigital.konversation.generator

import org.rewedigital.konversation.Intent

typealias Printer  = (output: String) -> Unit
interface Exporter {
    fun prettyPrinted(printer: Printer, intents: MutableList<Intent>)
    fun minified(printer: Printer, intents: MutableList<Intent>)
}