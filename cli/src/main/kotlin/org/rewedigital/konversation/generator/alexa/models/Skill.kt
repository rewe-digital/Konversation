package org.rewedigital.konversation.generator.alexa.models

data class Skill(
    val apis: List<String>,
    val asin: String,
    val lastUpdated: String,
    val nameByLocale: Map<String, String>,
    val publicationStatus: String,
    val skillId: String,
    val stage: String
)