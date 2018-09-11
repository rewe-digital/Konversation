package com.rewedigital.voice.konversation

import java.util.Random

actual class Random actual constructor() {
    actual fun next(max: Int): Int = Random().nextInt(max)
}