package org.rewedigital.konversation

import kotlin.test.asserter

/** Asserts that the [expected] value is equal to the [actual] value, with an optional [message], ignoring line breaks. */
fun assertEqualsIgnoringLineBreaks(expected: String, actual: String, message: String? = null) {
    asserter.assertEquals(message, expected.filterNot { it == '\r' }, actual.filterNot { it == '\r' })
}