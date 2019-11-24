package org.rewedigital.konversation

import org.rewedigital.konversation.generator.Exporter
import org.rewedigital.konversation.generator.Printer
import org.rewedigital.konversation.generator.alexa.AlexaExporter
import org.rewedigital.konversation.generator.alexa.AmazonApi
import org.rewedigital.konversation.generator.dialogflow.DialogflowApi
import org.rewedigital.konversation.generator.dialogflow.DialogflowExporter
import org.rewedigital.konversation.generator.kson.KsonExporter
import org.rewedigital.konversation.parser.Parser
import java.io.File
import java.io.FileOutputStream

class KonversationApi(private val amazonClientId: String, private val amazonClientSecret: String) {
    private var inputFileCount = 0

    val intentDb by lazy { cache.first }
    private val entityDb by lazy { cache.second }

    private val cache: Pair<Map<String, List<Intent>>, Map<String, List<Entities>>> by lazy {
        val intents = mutableMapOf<String, MutableList<Intent>>()
        val entities = mutableMapOf<String, MutableList<Entities>>()
        inputFiles.forEach { inputFile ->
            when {
                inputFile.isFile -> {
                    inputFileCount++
                    val parser = parseFile(inputFile)
                    intents.getOrPut("") { mutableListOf() } += parser.intents
                    parser.entities?.let { it: Entities -> entities.getOrPut("") { mutableListOf() } += it }
                }
                inputFile.isDirectory -> inputFile
                    .listFiles { dir: File?, name: String? ->
                        File(dir, name).isDirectory && (name == "konversation" || name?.startsWith("konversation-") == true)
                    }.toList()
                    .flatMap { it.listFiles { _, name -> name.endsWith(".kvs") || name.endsWith(".grammar") || name.endsWith(".values") }.toList() }
                    .also {
                        inputFileCount += it.size
                    }
                    .forEach { file ->
                        val prefix = file.parentFile.absolutePath.substring(inputFile.absolutePath.length + 13).trimStart('-')
                        val parser = parseFile(file)
                        intents.getOrPut(prefix) { mutableListOf() } += parser.intents
                        parser.entities?.let { entities.getOrPut(prefix) { mutableListOf() } += it }
                    }
            }
        }
        // merge utterances (for the case that they are defined in multiple files)
        intents.forEach { (_, intents) ->
            intents.groupingBy { it.name }.eachCount().filter { it.value > 1 }.forEach { (intent, _) ->
                val all = intents.filter { it.name == intent }
                all.first().let { master ->
                    all.drop(1).forEach {
                        master.utterances += it.utterances
                        intents.remove(it)
                    }
                }
            }
        }
        Pair(intents, entities)
    }

    // shared fields
    var invocationName: String? = null
    val inputFiles = mutableListOf<File>()
    var logger: LoggerFacade? = null

    // use cases
    fun validateInputFiles() {}

    fun exportPlain(targetDirectory: File) = intentDb[""]?.let { intents ->
        intents.forEach { intent ->
            if (intent.utterances.isEmpty()) {
                logger?.info("Skipping empty intent ${intent.name}...")
            } else {
                logger?.log("Dumping ${intent.name}...")
                val stream = File(targetDirectory, "${intent.name}.txt").outputStream()
                intent.utterances.forEach { utterance ->
                    utterance.permutations.forEach { permutation ->
                        stream.write(permutation.toByteArray())
                        stream.write(13)
                        stream.write(10)
                    }
                }
                stream.close()
            }
        }
    }

    // FIXME skillName is redundant
    fun exportAlexaSchema(targetDirectory: File, skillName: String, prettyPrint: Boolean = false) = intentDb.forEach { (config, intents) ->
        //targetDirectory.absoluteFile.parentFile.mkdirs()
        val exporter = AlexaExporter(skillName)
        val stream = targetDirectory.outputStream()
        exportToStream(stream, prettyPrint, exporter, intents, config)
    }

    private fun exportToStream(stream: FileOutputStream, prettyPrint: Boolean, exporter: Exporter, intents: List<Intent>, config: String) {
        val printer: Printer = { line ->
            stream.write(line.toByteArray())
        }
        if (prettyPrint) {
            exporter.prettyPrinted(printer, intents, entityDb[config])
        } else {
            exporter.minified(printer, intents, entityDb[config])
        }
        stream.close()
    }

    fun exportDialogflow(targetDirectory: File, invocationName: String, prettyPrint: Boolean = false) = intentDb.forEach { (config, intents) ->
        val exporter = DialogflowExporter(invocationName)
        val stream = File(targetDirectory, "dialogflow-$config.zip").outputStream()
        if (prettyPrint) {
            exporter.prettyPrinted(stream, intents, entityDb[config])
        } else {
            exporter.minified(stream, intents, entityDb[config])
        }
        stream.close()
    }

    fun exportKson(targetDirectory: File, prettyPrint: Boolean = false) = intentDb.forEach { (config, intents) ->
        val targetDir = File(targetDirectory.absolutePath + File.separator + "konversation".join("-", config))
        intents.forEach { intent ->
            val exporter = KsonExporter(intent.name)
            targetDir.mkdirs()
            val stream = File(targetDir, "${intent.name}.kson").outputStream()
            exportToStream(stream, prettyPrint, exporter, intents, config)
        }
    }

    fun authorizeAmazon(serverPort: Int) =
        AmazonApi(amazonClientId, amazonClientSecret).login(serverPort)

    fun updateAlexaSchema(refreshToken: String, skillName: String, skillId: String, stage: String = "development") {
        intentDb[""]?.let { intents ->
            AmazonApi(amazonClientId, amazonClientSecret, refreshToken)
                .uploadSchema(skillName, "de-DE", intents, entityDb[""], skillId)
        }
    }

    fun updateDialogflowProject(serviceAccount: File, project: String, invocationName: String) {
        intentDb[""]?.let { intents ->
            DialogflowApi(serviceAccount)
                .uploadIntents(invocationName, project, intents, entityDb[""])
        }
    }

    private fun parseFile(file: File) = Parser(file)
}