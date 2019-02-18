package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

class DialogflowExporter : Exporter {
    override fun prettyPrinted(printer: Printer, intents: MutableList<Intent>) {
        intents.forEach { intent ->
            println("Dumping ${intent.name}:")
            intent.utterances.forEach { utterance ->
                println(UUID.fromString("${intent.name}:$utterance").toString() + ": " + utterance)
            }
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun minified(printer: Printer, intents: MutableList<Intent>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //data class DialogflowConfig()

    data class DialogflowIntent(val name: String,
                                val action: String,
                                val id: UUID)
}