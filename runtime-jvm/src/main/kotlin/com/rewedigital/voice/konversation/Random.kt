package com.rewedigital.voice.konversation

import java.util.Random

actual class Random actual constructor() {
    actual fun next(max: Int): Int = if(System.getenv("USE_FIRST_OPTION") == "ON") 0 else Random().nextInt(max)
}