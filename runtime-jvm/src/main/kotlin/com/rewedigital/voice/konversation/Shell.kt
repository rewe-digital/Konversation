package com.rewedigital.voice.konversation

class Shell(conversation: String) {
    init {
        val konversation = Konversation(conversation)
        for (i in 0..10) {
            println(konversation.create())
        }
    }

    // Entry point to test the jvm implementation
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isNotEmpty()) {
                Shell(args[0])
            }
        }
    }
}