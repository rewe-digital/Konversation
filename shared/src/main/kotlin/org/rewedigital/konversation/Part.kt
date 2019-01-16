package org.rewedigital.konversation

interface Part {
    val variants: MutableList<String>
    val type: PartType
}