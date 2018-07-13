package eu.rekisoft.voice.konversation

import eu.rekisoft.voice.konversation.parts.Utterance
import java.io.File



class Validator(args: Array<String>) {
    var intent: String? = null
    val intents = mutableListOf<Intent>()

    init {
        //val t ="RecipeSearchIntent--713998604.tmp"
        //val stream = FileInputStream(File(t))
        ////val stream = Files.newInputStream(Paths.get("C:\\Users\\rene.kilczan\\Programmierung\\konversation-validator\\"+URI.create(t)))
        //var count = 0
        //val buf=ByteArray(1048576000)
        //val start = System.currentTimeMillis()
        //var read = stream.read(buf, 0, buf.size)
        //var total = read
        //val nl = 13.toByte()
        //do {
        //    for(i in 0 until read) {
        //        if(buf[i] == nl) count++
        //    }
        //    read = stream.read(buf, 0, buf.size)
        //    total+=read
        //} while(read > 0)
        //val end = System.currentTimeMillis()
        //println("Found $count lines in $t in (${end-start}ms) -> ${String.format("%.2f",total/(end-start * 1.0)/1.024/1024)}MB/s")




        val input = "C:\\Users\\rene.kilczan\\Programmierung\\REWE-Voice\\alexa-docs\\rewe.grammar"
        //val input = "demo.kvs"
        //val input = "foo.grammar"
        val isGrammarFile = input.endsWith(".grammar")
        val lines = File(input).readLines()
        lines.forEach { line ->
            when {
                line.startsWith("//") || line.isBlank() -> {
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
                line.startsWith("->") -> addTo {
                    // Voice only option

                }
                line.startsWith(">") -> addTo {
                    // Voice only

                }
                line.startsWith("-") -> addTo {
                    // option

                }
                line.startsWith("#switch") -> { // switch

                }
                line.startsWith("#if") -> { // if

                }
                line.startsWith("!") -> addTo {
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
        println("Parsing finished. Found ${intents.size} intents.")
        val all = intents.sumBy {
            val permutations = it.utterances.sumBy { it.permutations.size }
            println("${it.name} has now $permutations sample utterances")
            permutations
        }
        println("This is in total $all")
        //println("- " + all.sorted().joinToString(separator = "\n- "))

        println("Just to test the caching:")
        val check = intents[0].utterances
        //val check = intents.find { it.name == "RecipeSearchIntent" }?.utterances
        val utterances = check?.sumBy { it.permutations.size }
        println("The intent has ${check?.size} sample utterances which generated $utterances permutations")
    }

    class Intent(val name: String) {
        val parts = mutableListOf<Part>()
        val utterances = mutableListOf<Utterance>()

    }

    private fun addUtterance(intent: Intent, utterance: String) {
        intent.utterances.add(Utterance(utterance, intent.name))
    }

    private fun addTo(block: Intent.() -> Unit) = intent?.let {
        intents.find { it.name == intent }?.let(block::invoke)
    } ?: printErr("No intent defined.")

    private fun printErr(errorMsg: String) =
            System.err.println(errorMsg)


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hallo")
            Validator(args)
        }
    }

}