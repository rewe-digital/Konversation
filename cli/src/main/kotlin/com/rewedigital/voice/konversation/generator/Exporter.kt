package com.rewedigital.voice.konversation.generator

import com.rewedigital.voice.konversation.Intent

typealias Printer  = (output: String) -> Unit
interface Exporter {
    fun prettyPrinted(printer: Printer, intents: MutableList<Intent>)
    fun minified(printer: Printer, intents: MutableList<Intent>)
}