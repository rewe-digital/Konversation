package com.rewedigital.voice.konversation

class Konversation(val name: String, private val environment: Environment) {
    private val answer = Reader().loadAnswer(name, environment)
    fun create(data: MutableMap<String, Any>): String {
        val sb = StringBuilder()
        answer.parts
            .filter { it.type == PartType.Text || environment.voiceOnly }
            .forEach { part ->
                sb.append(part.variants[random.next(part.variants.size)])
            }
        return sb.toString()
    }

    internal fun applyVariables(input: String, data: MutableMap<String, Any>) {
        val regex = "(\\$([a-zA-Z_][_a-zA-Z0-9]*)|\\$\\{([a-zA-Z_][a-zA-Z0-9._]+[a-zA-Z_])}|%(\\d+\\.?\\d*)?[bBhHsScCdoxXeEfgGaAtTn]\\$([a-zA-Z_][A-zA-z0-9._]+[a-zA-Z_]|[a-zA-Z_]))".toRegex()

        val result = regex.replace(input) { matchResult ->
            val needle = matchResult.groups.first()?.value
            val fieldName = matchResult.groups.filterNotNull().last().value
            if(needle?.startsWith("%") == true) {
                Formatter().format(needle.substring(0, needle.indexOf('$')), data[fieldName])
            } else {
                data[fieldName].toString()
            }
        }
    }

    companion object {
        val random = Random()
    }
}