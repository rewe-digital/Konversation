package org.rewedigital.konversation

import kotlin.js.JsName

/**
 * The interface for accessing Konversation.
 *
 * @property name The conversation name you want to load.
 * @param environment The environment of the conversation to load.
 * @constructor Creates a new Konversation object with the given options.
 */
class Konversation(val name: String, environment: Environment) {
    private val answer = Reader().loadReply(name, environment)

    private fun create(data: Map<String, Any>, onlyDisplayText: Boolean): String {
        val sb = StringBuilder()
        answer.parts
            .filter { it.type == PartType.Text || !onlyDisplayText }
            .forEach { part ->
                sb.append(part.variants[random.next(part.variants.size)]).append(" ")
            }
        return applyVariables(sb.toString().trimEnd(), data)
    }

    internal fun applyVariables(input: String, data: Map<String, Any>) =
        regex.replace(input) { matchResult ->
            val needle = matchResult.groups.first()?.value
            val fieldName = matchResult.groups.filterNotNull().last().value
            if (needle?.startsWith("%") == true) {
                Formatter().format(needle.substring(0, needle.indexOf('$')), data[fieldName])
            } else {
                data[fieldName].toString()
            }
        }

    /**
     * Creates a static randomized output for your voice application.
     * The [data] will be applied to the output so that you can customize the output with your values.
     */
    @JsName("createOutput")
    fun createOutput(data: Map<String, Any> = emptyMap()) =
            Output(displayText = create(data, true),
                   ssml = create(data, false),
                   reprompts = answer.reprompts.map { it.key.toInt() to it.value[random.next(it.value.size)] }.toMap(),
                   suggestions = answer.suggestions,
                   extras = emptyMap())

    companion object {
        /** The randomness implementation which can be modified for testing. */
        private val random = Random()
        /** A regular expression to apply the actual values. */
        internal val regex = "(\\$([a-zA-Z_][_a-zA-Z0-9]*)|\\$\\{([a-zA-Z_][a-zA-Z0-9._]+[a-zA-Z_])}|%(\\d+\\.?\\d*)?[bBhHsScCdoxXeEfgGaAtTn]\\$([a-zA-Z_][A-zA-z0-9._]+[a-zA-Z_]|[a-zA-Z_]))".toRegex()
    }
}