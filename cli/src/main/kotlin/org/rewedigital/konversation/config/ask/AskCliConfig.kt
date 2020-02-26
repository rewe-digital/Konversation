package org.rewedigital.konversation.config.ask

import org.rewedigital.konversation.config.cheapDecrypt

data class AskCliConfig(
    val profiles: Map<String, Profile>
) {
    companion object {
        val clientId by lazy { "Zmd3fiI4eGxvTkxLSlpYW1kXUiFxayogJjc7LHU/AABUWF8SRhAYHR3CtsKxw6rCssK2wqHDt8Kvw7zCpcKXw4XCn8Kdw4rDjcOTw5bCjcKN".cheapDecrypt() }
        val clientSecret by lazy { "Njw5IncuISomQB0aEkpVVQQJDHEncCp6emFsPmg7AABSX1xJQRNKGhzCtcKzwr/DqcK3wqHCo8Kmw7nCpcKYwpvDg8KfwpzDisOUwoTCjcKKw53Cp8O9".cheapDecrypt() }
    }
}