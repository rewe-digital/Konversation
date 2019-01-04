package com.rewedigital.voice.konversation

import java.util.Random

actual class Random actual constructor() {
    actual fun next(max: Int): Int = forcedValue ?: Random().nextInt(max)

    companion object {
        // Just for testing!
        internal var forcedValue: Int? = null
    }
}