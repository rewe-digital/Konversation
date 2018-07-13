package eu.rekisoft.voice.konversation

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.naming.OperationNotSupportedException
import kotlin.collections.HashSet


class SwapingHashedList(prefix: String) : HashSet<String>() {
    private var isSwapping = false
    private val smallList = HashSet<String>()
    private var bigListSize = 0
        get() = guessedSize ?: hashList.size
    private var guessedSize : Int? = null
    private val hashList : HashSet<Int> by lazy { loadHashes() }
    private val cacheFile = File("$prefix.cache")
    private val hashFile = File("$prefix.hash")

    private val writer by lazy {
        cacheFile.bufferedWriter()
    }
    private val hashWriter by lazy {
        hashFile.outputStream()
    }
    var forceCaching: Boolean = true
    private val shouldCache: Boolean
        get() = if (forceCaching) true else hashList.size > 50000

    init {
        isSwapping = cacheFile.exists()
        if(!hashFile.exists()) {
            println("Holding ${hashList.size} hashes now.")
        } else {
            guessedSize = (hashFile.length() / 4).toInt()
        }
    }

    // load the hashes just when required
    private fun loadHashes() : HashSet<Int> {
        val hashList = HashSet<Int>()
        //println("FYI using temp file $cacheFile")
        if (hashFile.exists() && cacheFile.exists()) {
            println("Reading hash file...")
            val stream = FileInputStream(hashFile)
            val buf = try {
                ByteArray(Math.min(hashFile.length(), 1073741824).toInt()) // 1GB or less
            } catch (e: OutOfMemoryError) {
                // Okay we are less greedy
                println("Low memory detected.")
                ByteArray(104857600) // 100MB
            }
            var read = stream.read(buf, 0, buf.size)
            do {
                for (i in 0 until read step 4) {
                    hashList.add(buf.getIntAt(i))
                }
                println("size: ${hashList.size}")
                read = stream.read(buf, 0, buf.size)
            } while (read > 0)
        } else if (cacheFile.exists()) {
            println("Warning: Hash file is missing, rebuilding it.")
            val inputStream = FileInputStream(cacheFile)
            val outputStream = FileOutputStream(hashFile)
            val sc = Scanner(inputStream, "UTF-8")
            while (sc.hasNextLine()) {
                bigListSize++
                if ((bigListSize % 100000) == 0) {
                    println(">> $bigListSize")
                }
                val hash = sc.nextLine().hashCode()
                hashList.add(hash)
                outputStream.writeInt(hash)
            }
            inputStream.close()
            outputStream.close()
        }
        // reset the guessed size and take the actual confirmed size
        guessedSize = null
        return hashList
    }

    override val size: Int
        get() = if (isSwapping) bigListSize else smallList.size

    override fun contains(element: String): Boolean = if (isSwapping) {
        hashList.contains(element.hashCode())
    } else {
        smallList.contains(element)
    }

    override fun containsAll(elements: Collection<String>): Boolean = if (isSwapping) {
        hashList.containsAll(elements.map { it.hashCode() })
    } else {
        smallList.containsAll(elements)
    }

    override fun isEmpty(): Boolean = if (isSwapping) {
        bigListSize == 0
    } else {
        smallList.isEmpty()
    }

    override fun iterator(): MutableIterator<String> = if (isSwapping) {
        FileLineIterator(cacheFile)
    } else {
        smallList.iterator()
    }

    override fun add(element: String): Boolean = if (isSwapping) {
        try {
            synchronized(writer) {
                if (!hashList.contains(element.hashCode())) {
                    hashList.add(element.hashCode())
                    hashWriter.writeInt(element.hashCode())
                    writer.write(element)
                    writer.newLine()
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    } else {
        if (shouldCache) {
            try {
                swapIt(listOf(element))
            } catch (e: IOException) {
                false
            }
        } else {
            smallList.add(element)
        }
    }

    override fun addAll(elements: Collection<String>): Boolean = if (isSwapping) {
        try {
            elements.map { element ->
                synchronized(writer) {
                    if (!hashList.contains(element.hashCode())) {
                        hashList.add(element.hashCode())
                        hashWriter.writeInt(element.hashCode())

                        writer.write(element)
                        writer.newLine()
                    }
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    } else if (shouldCache) { // if (smallList.size + elements.size > 50000) {
        try {
            swapIt(elements)
        } catch (e: IOException) {
            false
        }
    } else {
        smallList.addAll(elements)
    }


    override fun clear() = if (isSwapping) {
        cacheFile.delete()
        hashFile.delete()
        bigListSize = 0
        isSwapping = false
        smallList.clear()
    } else {
        smallList.clear()
    }

    override fun remove(element: String): Boolean = if (isSwapping) {
        throw OperationNotSupportedException()
    } else {
        smallList.remove(element)
    }

    override fun removeAll(elements: Collection<String>): Boolean = if (isSwapping) {
        throw OperationNotSupportedException()
    } else {
        smallList.removeAll(elements)
    }

    override fun retainAll(elements: Collection<String>): Boolean = if (isSwapping) {
        throw OperationNotSupportedException()
    } else {
        smallList.retainAll(elements)
    }

    fun flush() {
        if (isSwapping) {
            synchronized(writer) {
                writer.flush()
                hashWriter.flush()
            }
        }
    }

    private fun swapIt(elements: Collection<String>): Boolean {
        isSwapping = true
        //println("##################### STARTED SWAPPING #######################")
        isSwapping = true
        synchronized(writer) {
            smallList.map { element ->
                if (!hashList.contains(element.hashCode())) {
                    hashList.add(element.hashCode())
                    writer.write(element)
                    writer.newLine()
                    hashWriter.writeInt(elements.hashCode())
                }
            }
            smallList.clear()
            elements.map { element ->
                if (!hashList.contains(element.hashCode())) {
                    hashList.add(element.hashCode())
                    writer.write(element)
                    writer.newLine()
                    hashWriter.writeInt(element.hashCode())
                }
            }
        }
        return true
    }

    private fun FileOutputStream.writeInt(int: Int) {
        write(int.shr(24))
        write(int.shr(16).and(0xff))
        write(int.shr(8).and(0xff))
        write(int.and(0xff))
    }

    private fun ByteArray.getIntAt(offset: Int) =
            this[offset].toInt().shl(24) + this[offset + 1].toInt().shl(16) + this[offset + 2].toInt().shl(8) + this[offset + 3].toInt()

    fun isCached() = cacheFile.exists()

    private class FileLineIterator(file: File) : MutableIterator<String> {
        private val reader = file.bufferedReader() // might been replaced by a scanner, not sure.

        override fun hasNext() = reader.ready()

        override fun next(): String = reader.readLine()

        override fun remove() {
            NotImplementedError("Not supported in this mode")
        }
    }
}