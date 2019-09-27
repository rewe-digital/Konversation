package org.rewedigital.konversation

import kotlin.js.JsName

/**
 * The interface for accessing Konversation.
 *
 * @property name The conversation name you want to load.
 * @param environment The environment of the conversation to load.
 * @constructor Creates a new Konversation object with the given options.
 */
@JsName("KonversationKt")
open class Konversation(val name: String, private val environment: Environment) {
    private val answer = Reader().loadReply(name, environment)

    private data class OutputHolder(val ssml: String, val text: String)

    private fun buildOutput(): OutputHolder {
        val text = StringBuilder()
        val ssml = StringBuilder("<speak>")
        answer.parts
            .forEachIterator { part ->
                val randomPart = part.variants[random.next(part.variants.size)]
                if (part.type == PartType.Text) {
                    text.append(randomPart)
                    if (hasNext())
                        text.append(" ")
                }
                ssml.append(randomPart)
                if (hasNext())
                    ssml.append(" ")
            }
        ssml.append("</speak>")
        return OutputHolder(ssml.toString(), text.toString().trimEnd())
    }

    internal fun String.applyVariables(data: Map<String, Any>) =
        regex.replace(this) { matchResult ->
            val needle = matchResult.groups.first()?.value
            val fieldName = matchResult.groups.filterNotNull().last().value
            if (needle?.startsWith("%") == true) {
                Formatter().format(environment.locale, needle.substring(0, needle.indexOf('$')), data[fieldName])
            } else {
                data[fieldName].toString()
            }
        }

    /**
     * Creates a static randomized output for your voice application.
     * The [data] will be applied to the output so that you can customize the output with your values.
     */
    fun createOutput(data: Map<String, Any> = emptyMap()) = buildOutput().run {
        Output(displayText = text.applyVariables(data),
            ssml = ssml.applyVariables(data).escapeForXml(),
            reprompts = answer.reprompts.map { it.key.toInt() to it.value[random.next(it.value.size)].applyVariables(data).escapeForXml() }.toMap(),
            suggestions = answer.suggestions.map { it.applyVariables(data) },
            extras = emptyMap())
    }

    companion object {
        /** The randomness implementation which can be modified for testing. */
        private val random = Random()
        /** A regular expression to apply the actual values. */
        internal val regex = "(\\$([a-zA-Z_][_a-zA-Z0-9]*)|\\$\\{([a-zA-Z_][a-zA-Z0-9._]+[a-zA-Z_])}|%(\\d+\\.?\\d*)?[bBhHsScCdoxXeEfgGaAtTn]\\$([a-zA-Z_][A-zA-z0-9._]+[a-zA-Z_]|[a-zA-Z_]))".toRegex()

        private fun String.escapeForXml() = this.replace("&(?!\\w+;)".toRegex(), "&amp;").replace("'", "&apos;")
    }
}