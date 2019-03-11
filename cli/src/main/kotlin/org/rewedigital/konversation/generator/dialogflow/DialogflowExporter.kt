package org.rewedigital.konversation.generator.dialogflow

import org.rewedigital.konversation.Cli
import org.rewedigital.konversation.Entities
import org.rewedigital.konversation.Intent
import org.rewedigital.konversation.forEachBreakable
import org.rewedigital.konversation.generator.StreamExporter
import java.io.OutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DialogflowExporter(private val invocationName: String) : StreamExporter {
    override fun prettyPrinted(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
        val zipStream = ZipOutputStream(outputStream)
        // Test to create the directories first
        zipStream.putNextEntry(ZipEntry("entities/"))
        zipStream.closeEntry()
        zipStream.putNextEntry(ZipEntry("intents/"))
        zipStream.closeEntry()

        val json = StringBuilder()
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
                json.clear()
                val meta = EntityMetaData(automatedExpansion = false,
                    id = UUID.nameUUIDFromBytes("$invocationName:$slotType".toByteArray()),
                    isEnum = false,
                    isOverridable = false,
                    name = slotType.name)

                meta.prettyPrinted { s -> json.append(s) }
                zipStream.add("entities/${slotType.name}.json", json)
                println("entities/${slotType.name}.json:\n$json")
                json.clear()
                val entries = slotType.values.map { entry ->
                    Entity(value = entry.master, synonyms = entry.synonyms)
                }
                println("\nentities/${slotType.name}_entries_<LANG>.json:")
                json.append("[\n")
                entries.forEachBreakable {
                    it.prettyPrinted { s -> json.append(s) }
                    if (hasNext()) json.append(",\n")
                }
                json.append("\n]")
                zipStream.add("entities/${slotType.name}_entries_DE.json", json)
                println(json)
            }
        intents.forEachIndexed { i, intent ->
            json.clear()
            println("intents/${intent.name}_usersays_<LANG>.json:")
            json.append("[\n")
            //println("Dumping ${intent.name} (${intent.utterances.sumByLong { it.permutationCount }} utterances):")
            val slots = intent.utterances.flatMap { it.slotTypes }.map {
                val parts = it.split(":")
                parts.first() to parts.last()
            }.toMap()
            intent.utterances.forEachBreakable { utterance ->
                utterance.permutations.forEach { sentence ->
                    val data = if (sentence.contains("{") && sentence.contains("}")) {
                        sentence.split("{", "}").filter { it.isNotEmpty() }.map { part ->
                            val type = slots[part]
                            type?.let {
                                val values = entities?.firstOrNull { it.name == type }?.values?.flatMap { it.synonyms }
                                val sample = values?.get(i % values.size) ?: "foo"

                                DialogflowUtterance.UtterancePart(text = sample, alias = part, meta = "@$type", userDefined = false)
                            } ?: DialogflowUtterance.UtterancePart(text = part, userDefined = false)

                        }
                    } else {
                        listOf(DialogflowUtterance.UtterancePart(text = sentence, userDefined = false))
                    }
                    DialogflowUtterance(
                        count = 0,
                        data = data,
                        id = UUID.nameUUIDFromBytes("${intent.name}:$sentence".toByteArray()),
                        isTemplate = false,
                        updated = System.currentTimeMillis() / 1000)
                        .prettyPrinted {s->json.append(s)}
                    //println(sentence + ": " + UUID.nameUUIDFromBytes("${intent.name}:$sentence".toByteArray()))
                    if (hasNext()) json.append(",\n")
                }
            }
            json.append("]")
            zipStream.add("intents/${intent.name}_usersays_DE.json", json)
            println(json)
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun minified(outputStream: OutputStream, intents: List<Intent>, entities: List<Entities>?) {
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

    fun ZipOutputStream.add(fileName: String, content: StringBuilder) {
        val file = ZipEntry(fileName)
        putNextEntry(file)
        write(content.toString().toByteArray())
        closeEntry()
    }
}