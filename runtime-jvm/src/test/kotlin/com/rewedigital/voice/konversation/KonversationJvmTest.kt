package com.rewedigital.voice.konversation

import org.junit.Test

class KonversationJvmTest {
    @Test
    fun matching() {
        val regex = "(\\$([a-zA-Z][a-zA-Z0-9]+)|\\$\\{([a-zA-Z][a-zA-Z0-9.]+)}|%(\\d+\\.?\\d*)?[bBhHsScCdoxXeEfgGaAtTn]\\$([a-zA-Z][A-zA-z0-9.]+))".toRegex()
        val input = "Hello \$planet \${user.name} %1.2f\$age"
        val data = mapOf("planet" to "world", "user.name" to "René", "age" to 12.34)

        println("Taking the input \"$input\" and injecting $data:")

        //println(regex.findAll(input).joinToString(separator = "\n") {
        //    "Replace " + it.groups.first()?.value + " with value of " + it.groups.filterNotNull().last().value
        //    // DEBUG:
        //    //it.groups.joinToString()
        //})

        val result = regex.replace(input) { matchResult ->
            val needle = matchResult.groups.first()?.value
            val fieldName = matchResult.groups.filterNotNull().last().value
            if (needle?.startsWith("%") == true) {
                String.format(needle.substring(0, needle.indexOf('$')), data[fieldName])
            } else {
                data[fieldName].toString()
            }
        }
        println(result)
    }
}