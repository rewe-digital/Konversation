package com.rewedigital.voice.konversation

data class Intent(val name: String,
                  val utterances: MutableList<Utterance> = mutableListOf(),
                  val prompt: Prompt = Prompt(),
                  val reprompt: MutableMap<Int, Prompt> = mutableMapOf(),
                  var inContext: String? = null,
                  var outContext: String? = null,
                  val followUp: MutableList<String> = mutableListOf(),
                  val suggestion: MutableList<Suggestion> = mutableListOf(),
                  val extras: MutableMap<String, Any> = mutableMapOf())