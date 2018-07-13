package eu.rekisoft.voice.konversation

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*


class SwapingList(private val tempFile: File) : HashSet<String>() {
    private var isSwapping = false
    private val smallList = HashSet<String>()
    private var bigListSize = 0
    private val hashList = HashSet<Int>()

    private val writer by lazy {
        tempFile.bufferedWriter()
    }

    init {
        //println("FYI using temp file $tempFile")
        if (tempFile.exists()) {
            isSwapping = true
            //val stream = BufferedInputStream(tempFile.inputStream())
            //while (stream.available() > 0) {
            //    bigListSize += stream.readBytes().count { b -> b == 13.toByte() }
            //}
            val inputStream = FileInputStream(tempFile)
            val sc = Scanner(inputStream, "UTF-8")
            while (sc.hasNextLine()) {
                bigListSize++
                if((bigListSize % 100000) == 0) {
                    println(">> $bigListSize")
                }
                sc.nextLine()
            }
            inputStream.close()
        }
    }

    override val size: Int
        get() = if (isSwapping) bigListSize else smallList.size

    override fun contains(element: String): Boolean = if (isSwapping) {
        TODO("not implemented")
    } else {
        smallList.contains(element)
    }

    override fun containsAll(elements: Collection<String>): Boolean = if (isSwapping) {
        TODO("not implemented")
    } else {
        smallList.containsAll(elements)
    }

    override fun isEmpty(): Boolean = if (isSwapping) {
        bigListSize == 0
    } else {
        smallList.isEmpty()
    }

    override fun iterator(): MutableIterator<String> = if (isSwapping) {
        FileLineIterator(tempFile)
    } else {
        smallList.iterator()
    }

    override fun add(element: String): Boolean = if (isSwapping) {
        try {
            synchronized(writer) {
                if (!hashList.contains(element.hashCode())) {
                    hashList.add(element.hashCode())
                    writer.write(element)
                    writer.newLine()
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    } else {
        if (smallList.size + 1 > 50000) {
            try {
                isSwapping = true
                println("##################### STARTED SWAPPING #######################")
                synchronized(writer) {
                    smallList.map { element2 ->
                        if (!hashList.contains(element2.hashCode())) {
                            hashList.add(element2.hashCode())
                            writer.write(element2)
                            writer.newLine()
                        }
                    }
                    smallList.clear()
                    if (!hashList.contains(element.hashCode())) {
                        hashList.add(element.hashCode())
                        writer.write(element)
                        writer.newLine()
                    }
                    true
                }
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

                        writer.write(element)
                        writer.newLine()
                    }
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    } else {
        if (smallList.size + elements.size > 50000) {
            try {
                isSwapping = true
                synchronized(writer) {
                    smallList.map { element ->
                        if (!hashList.contains(element.hashCode())) {
                            hashList.add(element.hashCode())
                            writer.write(element)
                            writer.newLine()
                        }
                    }
                    smallList.clear()
                    elements.map { element ->
                        if (!hashList.contains(element.hashCode())) {
                            hashList.add(element.hashCode())
                            writer.write(element)
                            writer.newLine()
                        }
                    }
                }
                true
            } catch (e: IOException) {
                false
            }
        } else {
            smallList.addAll(elements)
        }
    }

    override fun clear() = if (isSwapping) {
        tempFile.delete()
        bigListSize = 0
        isSwapping = false
        smallList.clear()
    } else {
        smallList.clear()
    }

    override fun remove(element: String): Boolean = if (isSwapping) {
        TODO("not implemented")
    } else {
        smallList.remove(element)
    }

    override fun removeAll(elements: Collection<String>): Boolean = if (isSwapping) {
        TODO("not implemented")
    } else {
        smallList.removeAll(elements)
    }

    override fun retainAll(elements: Collection<String>): Boolean = if (isSwapping) {
        TODO("not implemented")
    } else {
        smallList.retainAll(elements)
    }

    private class FileLineIterator(file: File) : MutableIterator<String> {
        private val reader = file.bufferedReader() // might been replaced by a scanner, not sure.

        override fun hasNext() = reader.ready()

        override fun next(): String = reader.readLine()

        override fun remove() {
            NotImplementedError("Not supported in this mode")
        }
    }
}