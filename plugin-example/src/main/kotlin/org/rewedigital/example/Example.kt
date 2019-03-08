package org.rewedigital.example

import org.rewedigital.konversation.Environment
import org.rewedigital.konversation.Konversation

class Example {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Konversation("ExampleIntent", Environment("example", "DE-de")).createOutput().displayText)
        }
    }
}