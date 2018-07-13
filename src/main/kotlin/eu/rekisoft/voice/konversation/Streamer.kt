package eu.rekisoft.voice.konversation

import java.io.File

class Streamer(args: Array<String>) {

    init {
        val out = File("test.txt").outputStream()
        val files = File("cache/").listFiles { _, name -> name.startsWith("RecipeSearchIntent") && name.endsWith(".cache") }
        out.write(prefix)
        var firstElementOfFile: Boolean
        files.forEach { file ->
            println("Using $file...")
            firstElementOfFile = true
            val lines = SwapingHashedList(file.absolutePath.removeSuffix(".cache"))
            lines.forEach { line ->
                if(firstElementOfFile) {
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
            println("Stiching stuff...")
            Streamer(args)
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