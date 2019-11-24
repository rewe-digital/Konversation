package org.rewedigital.konversation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.operations.DefaultBuildOperationIdFactory
import org.gradle.internal.time.Time
import org.gradle.tooling.internal.consumer.SynchronizedLogging
import org.rewedigital.konversation.Cli
import org.rewedigital.konversation.KonversationExtension
import org.rewedigital.konversation.createLoggingFacade
import org.rewedigital.konversation.parser.Utterance
import org.slf4j.LoggerFactory
import java.io.File

@CacheableTask
open class CompileTask : DefaultTask() {
    private val progressLoggerFactory = SynchronizedLogging(Time.clock(), DefaultBuildOperationIdFactory()).progressLoggerFactory
    private val LOGGER = LoggerFactory.getLogger(CompileTask::class.java)

    init {
        Cli.L = createLoggingFacade(LOGGER)
    }

    @InputFiles
    val inputFiles = mutableListOf<File>()

    @OutputDirectories
    val outputDirectories = mutableListOf<File>()

    @TaskAction
    fun compile() {
        val cli = Cli()
        val config = project.extensions.getByName("konversation") as? KonversationExtension
        Utterance.cacheDir = config?.cacheDir ?: project.buildDir.path + "/konversation/cache"
        val op = progressLoggerFactory.newOperation(CompileTask::class.java)
        //op.loggingHeader = "header"
        op.description = "description"
        //org.gradle.api.logging.Logger().lifecycle("blah")
        //op.setShortDescription("description")
        //val foo = op.start("description", "description")
        LOGGER.info("Processing ${inputFiles.size} files...")
        op.started()
        inputFiles.forEach { file ->
            //Thread.sleep(5000)
            op.progress("${op.description}: ${file.path}")
            LOGGER.debug("${op.description}: ${file.path}")
            val start = file.path.indexOf("src" + File.separator) + 4
            val end = file.path.indexOf(File.separatorChar, start)
            val sourceSet = file.path.substring(start, end)
            cli.parseArgs(arrayOf("--export-kson", project.buildDir.path + "/konversation/res/$sourceSet/", file.path))
        }
        //Thread.sleep(3000)
        op.completed()
    }
}