package org.rewedigital.konversation.parser

import java.text.ParseException
import java.util.*

object Permutator {
    fun generate(line: String): MutableList<String> {
        val variants = validate(line)
        val result = mutableListOf<String>()
        insertPermutations(line, variants, 0, result)
        return result
    }

    private fun validate(line: String): MutableList<String> {
        // Parse the line to make sure that there is no syntax error. Regex would not work for cases like {{Foo}|{Bar}}
        var start = 0
        var counter = 0
        val slots = mutableListOf<String>()
        var lastWasMasked = false
        var dontProcess = false
        line.forEachIndexed { i, c ->
            when (c) {
                '\\' -> lastWasMasked = true
                '{' -> {
                    if (!lastWasMasked) {
                        when (counter) {
                            0 -> start = i + 1
                            1 -> {
                                // we found a slot type, that is fine
                            }
                            else -> throw ParseException("This line has a syntax error: $line", i)
                        }
                        counter++
                    }
                    lastWasMasked = false
                }
                '}' -> {
                    if (!lastWasMasked) {
                        when (counter) {
                            1 -> {
                                // we found the end of the slot
                                if (!dontProcess) {
                                    slots.add(line.substring(start, i))
                                }
                            }
                            //2 -> {
                            //    // we found the end of a slot type, that is fine
                            //}
                            else -> throw ParseException("This line has a syntax error: $line", i)
                        }
                        counter--
                    }
                    lastWasMasked = false
                    dontProcess = false
                }
                '$' -> dontProcess = true
                //'%'-> {

                //}
                else -> lastWasMasked = false
            }
        }
        if (counter != 0) throw ParseException("This line has a syntax error: $line", line.length)

        return slots
    }

    private fun insertPermutations(line: String, slots: MutableList<String>, offset: Int, storage: MutableList<String>) {
        if (slots.size == offset) {
            //val count = Utterance.counter.incrementAndGet()
            //if ((count % 100000) == 0) println(count)

            storage.add(line.trim().replace(" +".toRegex(), " "))
            return
        }
        val placeholder = UUID.randomUUID().toString() // could be replaced by something faster
        val replacement = line.replaceFirst(slots[offset], placeholder)
        slots[offset].split("|").map {
            insertPermutations(replacement.replace("{$placeholder}", it), slots, offset + 1, storage)
        }
    }
}
