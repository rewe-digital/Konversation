package org.rewedigital.konversation.parser

import kotlinx.coroutines.runBlocking
import org.rewedigital.konversation.SwapingHashedList
import java.io.File
import java.text.ParseException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Utterance(val source: String, val name: String) {

    val permutations: SwapingHashedList by lazy {
        //println("Generating about $permutationCount permutations for ${slotTypes.size} slots")

        // we know now all slots, let's fill them up with content
        File(cacheDir).run {
            if (!exists()) mkdirs()
        }
        val cacheFile = "$cacheDir/$name-${String.format("%08x", source.hashCode())}"
        SwapingHashedList(cacheFile).also { storage ->
            if (!storage.isCached()) {
                runBlocking {
                    insertPermutations(source, variableParts, 0, storage)
                }
                storage.flush()
            }
        }
    }

    val slotTypes: List<String> by lazy {
        variableParts.flatMap {
            it.split("|")
        }.mapNotNull {
            val start = it.indexOf('{')
            val end = it.indexOf('}')
            if (start in 0 until end) {
                it.substring(start + 1, end)
            } else null
        }
    }

    val permutationCount: Long by lazy {
        var total: Long = 1
        variableParts.flatMap { line ->
            line.split("|").also { parts ->
                total *= parts.size
            }
        }
        total
    }

    private val variableParts by lazy {
        // Parse the line to make sure that there is no syntax error. Regex would not work for cases like {{Foo}|{Bar}}
        var start = 0
        var counter = 0
        val variableParts = mutableListOf<String>()
        var lastWasMasked = false
        source.forEachIndexed { i, c ->
            when (c) {
                '\\' -> lastWasMasked = true
                '{' -> {
                    if (!lastWasMasked) {
                        when (counter) {
                            0 -> start = i + 1
                            1 -> {
                                // we found a slot type, that is fine
                            }
                            else -> throw ParseException("This line has a syntax error: $source", i)
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
                                variableParts.add(source.substring(start, i))
                            }
                            2 -> {
                                // we found the end of a slot type, that is fine
                            }
                            else -> throw ParseException("This line has a syntax error: $source", i)
                        }
                        counter--
                    }
                    lastWasMasked = false
                }
                //'%',
                //'$'-> {

                //}
                else -> lastWasMasked = false
            }
        }
        if (counter != 0) throw ParseException("This line has a syntax error: $source", source.length)

        variableParts
    }

    private suspend fun insertPermutations(line: String, slots: MutableList<String>, offset: Int, storage: SwapingHashedList) {
        if (slots.size == offset) {
            val count = counter.incrementAndGet()
            if ((count % 100000) == 0) println(count)

            line.replace(" +".toRegex(), " ").trim().let { cleanLine ->
                if (cleanLine.isNotEmpty()) storage.add(cleanLine)
            }
            return
        }
        val placeholder = UUID.randomUUID().toString() // could be replaced by something faster
        val replacement = line.replaceFirst(slots[offset], placeholder)
        slots[offset].split("|").map {
            var value = it
            // strip out the type from the slot type name if any
            val delimiter = value.indexOf(':')
            // check if there is a type delimiter
            if (delimiter >= 0) {
                val open = it.indexOf('{')
                val close = it.indexOf('}')
                // check that there is a opening and closing bracket together with the delimiter in the correct order
                if (open in 0 until close && delimiter in open until close) {
                    value = value.substring(0, delimiter) + value.substring(close)
                }
            }
            insertPermutations(replacement.replace("{$placeholder}", value), slots, offset + 1, storage)
        }
    }

    override fun toString() = "Utterance(source='$source', name='$name', permutations:$permutations, slotTypes=$slotTypes)"

    companion object {
        val counter = AtomicInteger(0)
        var cacheDir = "cachedPermutations"
    }
}