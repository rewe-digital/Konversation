package com.rewedigital.voice.konversation

class Konversation(val name: String, environment: Environment) {
    private val answer = Reader().loadAnswer(name, environment)

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

    fun createOutput(data: Map<String, Any> = emptyMap()) =
            Output(displayText = create(data, true),
                   ssml = create(data, false),
                   reprompts = answer.reprompts.map { it.key.toInt() to it.value[random.next(it.value.size)] }.toMap(),
                   suggestions = answer.suggestions,
                   extras = emptyMap())

    companion object {
        val random = Random()
        val regex = "(\\$([a-zA-Z_][_a-zA-Z0-9]*)|\\$\\{([a-zA-Z_][a-zA-Z0-9._]+[a-zA-Z_])}|%(\\d+\\.?\\d*)?[bBhHsScCdoxXeEfgGaAtTn]\\$([a-zA-Z_][A-zA-z0-9._]+[a-zA-Z_]|[a-zA-Z_]))".toRegex()
    }
}