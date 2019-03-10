package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.Cli
import org.rewedigital.konversation.Entities
import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import java.util.*

class DialogflowExporter(private val invocationName: String) : Exporter {
    override fun prettyPrinted(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        intents
            .flatMap {
                it.utterances.flatMap { utterance ->
                    utterance.slotTypes
                }
            }.toHashSet()
            .mapNotNull {
                if (it.startsWith("AMAZON.")) {
                    Cli.L.warn("No definition for slot type \"$it\" found")
                }
                val type = it.split(':').last()
                entities?.firstOrNull { entity -> entity.name == type }
            }.forEach { slotType ->
                val meta = EntityMetaData(automatedExpansion = false,
                    id = UUID.nameUUIDFromBytes("$invocationName:$slotType".toByteArray()),
                    isEnum = false,
                    isOverridable = false,
                    name = slotType.name)
                println("entities/${slotType.name}.json:")
                meta.prettyPrinted(::print)
                val entries = slotType.values.map { entry ->
                    Entity(value = entry.master, synonyms = entry.synonyms)
                }
                println("\nentities/${slotType.name}_entries_<LANG>.json:\n[")
                entries.forEachBreakable {
                    it.prettyPrinted(::print)
                    if (hasNext()) println(",")
                }
                println("\n]")
            }
        intents.forEach { intent ->
            println("Dumping ${intent.name} (${intent.utterances.sumByLong { it.permutationCount }} utterances):")
            intent.utterances.forEach { utterance ->
                utterance.permutations.forEach { sentence ->
                    println(sentence + ": " + UUID.nameUUIDFromBytes("${intent.name}:$sentence".toByteArray()))
                }
            }
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun minified(printer: Printer, intents: List<Intent>, entities: List<Entities>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //data class DialogflowConfig()

    data class DialogflowIntent(val name: String,
        val action: String,
        val id: UUID)

    public inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
        var sum: Long = 0
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }
}