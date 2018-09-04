package com.rewedigital.voice.konversation.parser

import com.rewedigital.voice.konversation.Intent
import java.io.File

class Parser(input: String) {
    private var intent: String? = null
    val intents = mutableListOf<Intent>()

    init {
        val isGrammarFile = input.endsWith(".grammar")
        val lines = File(input).readLines()
        lines.forEach { line ->
            when {
                line.startsWith("//") ||line.startsWith("#") || line.isBlank() -> {
                    // ignore comments and blank lines
                }
                line.endsWith(":") -> { // intent found
                    intent = line.substring(0, line.length - 1)
                    if (intents.find { it.name.equals(intent, true) } != null) {
                        printErr("Intent \"$intent\" already defined. Appending new parts. You have been warned.")
                    } else {
                        intents.add(Intent(intent as String))
                    }
                }
                line.startsWith("~") -> addTo {
                    // Voice only


                }
                line.startsWith("-") -> addTo {
                    // variant

                }
                line.startsWith("!") -> addTo { // reprompts
                    addUtterance(this, line.substring(2))
                }
                else -> addTo {
                    if (isGrammarFile) {
                        // handle as sample utterance since this is a grammar file
                        addUtterance(this, line)
                    } else {
                        // static part
                    }
                }
            }
        }
    }
    private fun addUtterance(intent: Intent, utterance: String) {
        intent.utterances.add(Utterance(utterance, intent.name))
    }

    private fun printErr(errorMsg: String) =
        System.err.println(errorMsg)

    private fun addTo(block: Intent.() -> Unit) = intent?.let { intent ->
        intents.find { it.name == intent }?.let(block::invoke)
    } ?: printErr("No intent defined.")

}