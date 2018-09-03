package com.rewedigital.voice.konversation

import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*


class Streamer {

    init {
        val test = ByteBuffer.wrap("\",\"".toByteArray())

        val out = Paths.get("test2.txt")
        val outChannel = FileChannel.open(out, HashSet(Arrays.asList(StandardOpenOption.CREATE, StandardOpenOption.WRITE)))
        val start = System.nanoTime()

        val stream = Files.newDirectoryStream(FileSystems.getDefault().getPath("cache"), "RecipeSearchIntent-3*.cache")
        val buffer = ByteBuffer.allocate(4096 * 1024)
        val offsetBuffer = ByteBuffer.allocate(4096 * 1024)
        var offset = 0L
        stream.forEach { file ->
            val offsetFile = Paths.get(file.toFile().absolutePath.removeSuffix("cache") + "offset")
            println("Using $file...")
            val inChannel = FileChannel.open(file, HashSet(Arrays.asList(StandardOpenOption.READ)))
            val offsetChannel = FileChannel.open(offsetFile, HashSet(Arrays.asList(StandardOpenOption.READ)))
            offsetChannel.position(8) // the start is always zero, skip it

            while (offsetChannel.read(offsetBuffer) > 0) {
                offsetBuffer.flip()
                while (offsetBuffer.hasRemaining()) {
                    val end = offsetBuffer.long
                    val size = (end - offset).toInt()
                    buffer.limit(size)
                    inChannel.read(buffer)
                    buffer.limit(size - 2) // avoid that the line break will be written out
                    buffer.flip()
                    outChannel.write(buffer)
                    outChannel.write(test)
                    test.clear()
                    buffer.flip()
                    offset = end
                }
                offsetBuffer.clear()
            }
            buffer.clear()
            offset = 0L
            inChannel.close()
            buffer.clear()
        }
        stream.close()


        val elapsedTime = System.nanoTime() - start
        val seconds = elapsedTime.toDouble() / 1000000000.0
        println("seconds $seconds")
    }

    fun alt() {
        val out = File("test.txt").outputStream()
        val files = File("cache/").listFiles { _, name -> name.startsWith("RecipeSearchIntent") && name.endsWith(".cache") }
        out.write(prefix)
        var firstElementOfFile: Boolean
        files.forEach { file ->
            println("Using $file...")
            firstElementOfFile = true
            val lines = SwapingHashedList(file.absolutePath.removeSuffix(".cache"))
            lines.forEach { line ->
                if (firstElementOfFile) {
                    firstElementOfFile = false
                } else {
                    out.write(','.toInt())
                }
                out.write(lineStart)
                out.write(line.toByteArray())
                out.write('"'.toInt())

            }
        }
        out.write(suffix)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            //println("Stiching stuff...")
            Streamer()
        }

        val prefix = ("{\n" +
                "  \"interactionModel\" : {\n" +
                "    \"languageModel\" : {\n" +
                "      \"invocationName\" : \"rewe\",\n" +
                "      \"intents\" : [\n" +
                "        {\n" +
                "          \"name\" : \"RecipeDetailStepsIntent\",\n" +
                "          \"slots\" : [\n" +
                "            {\n" +
                "              \"name\" : \"number\",\n" +
                "              \"type\" : \"number\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"samples\" : [").toByteArray()
        val lineStart = "\n            \"".toByteArray()
        val suffix = ("\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}").toByteArray()
    }
}