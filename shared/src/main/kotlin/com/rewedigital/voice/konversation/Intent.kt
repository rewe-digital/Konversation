package com.rewedigital.voice.konversation

data class Intent(val name: String,
                  val utterances: MutableList<Utterance> = mutableListOf(),
                  val prompt: Prompt = Prompt(),
                  val reprompt: MutableMap<Int, Prompt> = mutableMapOf(),
                  var inContext: MutableList<String> = mutableListOf(),
                  var outContext: MutableList<String> = mutableListOf(),
                  val followUp: MutableList<String> = mutableListOf(),
                  val suggestions: MutableList<String> = mutableListOf(),
                  val extras: MutableMap<String, Any> = mutableMapOf())