package com.rewedigital.voice.konversation.parser

import com.rewedigital.voice.konversation.Intent
import com.rewedigital.voice.konversation.PartImpl
import com.rewedigital.voice.konversation.PartType
import com.rewedigital.voice.konversation.Prompt
import java.io.File

class Parser(input: String) {
    private var intent: String? = null
    val intents = mutableListOf<Intent>()

    init {
        val isGrammarFile = input.endsWith(".grammar")
        val lines = File(input).readLines()
        lines.forEach { line ->
            when {
                line.startsWith("//") || line.startsWith("#") || line.isBlank() -> {
                    // ignore comments and blank lines
                }
                line.trim() == "+" -> addTo {
                    // just let the block end
                }
                line.trim() == "-" -> addTo {
                    // add a line break
                    var lastPart = prompt.parts.lastOrNull()
                    if (lastPart == null || lastPart.type != PartType.Text) {
                        lastPart = PartImpl(type = PartType.VoiceOnly, variant = mutableListOf())
                        prompt.parts.add(lastPart)
                    }
                    lastPart.variant.add(" \n")
                }
                line.startsWith("~") -> addTo {
                    // Voice only
                    val text = line.substring(1)
                    var textPart = prompt.parts.lastOrNull()
                    if (textPart == null || textPart.type != PartType.Text) {
                        textPart = PartImpl(type = PartType.VoiceOnly, variant = mutableListOf())
                        prompt.parts.add(textPart)
                    }
                    textPart.variant.add(text)
                }
                line.startsWith("-") -> addTo {
                    // variant
                    val text = line.substring(1)
                    var voicePart = prompt.parts.lastOrNull()
                    if (voicePart == null || voicePart.type != PartType.VoiceOnly) {
                        voicePart = PartImpl(type = PartType.Text, variant = mutableListOf())
                        prompt.parts.add(voicePart)
                    }
                    voicePart.variant.add(text)
                }
                line.startsWith("!") -> addTo {
                    // reprompt
                    val level = line.substring(1, line.indexOf(" ") - 1).toIntOrNull() ?: 1
                    val text = line.substring(line.indexOf(" "))
                    val prompt = reprompt.getOrPut(level) { Prompt(PartImpl(type = PartType.VoiceOnly, variant = mutableListOf())) }
                    prompt.parts.first().variant.addAll(Permutator.generate(text))
                }
                line.startsWith("[") && line.endsWith("]") -> addTo {
                    // suggestions
                    suggestion.addAll(line.substring(1, line.length - 2).split("]\\w+\\[".toRegex()))
                }
                line.startsWith("@") -> addTo {
                    followUp.add(line.substring(1).trim())
                }
                line.startsWith(">") -> addTo {
                    inContext.add(line.substring(1).trim())
                }
                line.startsWith("<") -> addTo {
                    outContext.add(line.substring(1).trim())
                }
                line.startsWith("?") || line.startsWith("&") -> addTo {
                    // TODO handle
                }
                line.endsWith(":") -> { // intent found
                    intent = line.substring(0, line.length - 1)
                    if (intents.find { it.name.equals(intent, true) } != null) {
                        printErr("Intent \"$intent\" already defined. Appending new parts. You have been warned.")
                    } else {
                        intents.add(Intent(intent as String))
                    }
                }
                else -> addTo {
                    addUtterance(this, line)
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