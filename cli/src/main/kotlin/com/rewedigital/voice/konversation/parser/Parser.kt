package com.rewedigital.voice.konversation.parser

import com.rewedigital.voice.konversation.*
import java.io.File
import java.util.*

class Parser(input: String) {
    val intents = mutableListOf<Intent>()
    private var lastIntent : Intent? = null

    init {
        val isGrammarFile = input.endsWith(".grammar")
        val lines = File(input).readLines()
        var lastPart: Part? = null
        var lastIntentName = UUID.randomUUID().toString()
        lines.forEach { line ->
            when {
                line.startsWith("//") || line.startsWith("#") || line.isBlank() -> {
                    // ignore comments and blank lines
                }
                line.trim() == "+" -> addTo {
                    // just let the block end
                    lastPart = null
                    //prompt.parts.add(PartImpl(type = PartType.Text, variant = mutableListOf("")))
                }
                line.trim() == "-" -> addTo {
                    // add a line break
                    lastPart = null
                    prompt.parts.add(PartImpl(type = PartType.Text, variant = mutableListOf(" \n")))
                }
                line.startsWith("~") -> addTo {
                    // Voice only
                    val text = line.substring(1).trim()
                    if (lastPart?.type ?: PartType.Text == PartType.Text) {
                        lastPart = PartImpl(type = PartType.VoiceOnly, variant = mutableListOf())
                        prompt.parts.add(lastPart!!)
                    }
                    lastPart?.variant?.add(text)
                }
                line.startsWith("-") -> addTo {
                    // variant
                    val text = line.substring(1).trim()
                    if (lastPart?.type ?: PartType.VoiceOnly == PartType.VoiceOnly) {
                        lastPart = PartImpl(type = PartType.Text, variant = mutableListOf())
                        prompt.parts.add(lastPart!!)
                    }
                    lastPart?.variant?.add(text)
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
                line.contains('=') && line.startsWith(lastIntentName) -> {
                    lastPart = null
                    if (intents.find { it.name.equals(line, true) } != null) {
                        printErr("Intent \"$line\" already defined. Appending new parts. You have been warned.")
                    } else {
                        lastIntent = Intent(line).also {
                            intents.add(it)
                        }
                    }
                }
                line.endsWith(":") -> { // intent found
                    lastPart = null
                    lastIntentName = line.substring(0, line.length - 1)
                    if (intents.find { it.name.equals(lastIntentName, true) } != null) {
                        printErr("Intent \"${lastIntent?.name}\" already defined. Appending new parts. You have been warned.")
                    } else {
                        lastIntent = Intent(lastIntentName).also {
                            intents.add(it)
                        }
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

    private fun addTo(block: Intent.() -> Unit) = lastIntent?.let(block::invoke) ?: printErr("No intent defined.")
}