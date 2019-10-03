package org.rewedigital.konversation.parser

import java.text.ParseException

class KonversationSyntaxError(
    line: String,
    private val intent: String,
    private val lineNumber: Int,
    reason: String,
    private val file: String) : ParseException("Syntax error in line $lineNumber:\n$line\nCaused by: $reason", lineNumber) {
    override fun getStackTrace(): Array<StackTraceElement> =
        (listOf(StackTraceElement(
            file.substringBefore('.'),
            intent,
            file,
            lineNumber
        )) + super.getStackTrace().toList()).toTypedArray()

    override fun toString() = "${javaClass.name}: Syntax error in line $lineNumber"
}