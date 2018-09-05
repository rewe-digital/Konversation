package com.rewedigital.voice.konversation

import java.util.Random

class Random {
    fun next(max: Int): Int = Random().nextInt(max)
}