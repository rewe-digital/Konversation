package org.rewedigital.konversation

data class Entity(
    val master: String,
    val key: String?,
    val synonyms: List<String>)