package com.arclabs.fixture.domain

/**
 * Builds the greeting for a person: "<salutation>, <name>!". Surrounding whitespace in [name] is
 * ignored and a blank name is greeted as "friend".
 */
class GreetingUseCase(private val repository: GreetingRepository) {
    suspend operator fun invoke(name: String): String {
        val addressee = name.trim().ifEmpty { FALLBACK_NAME }
        return "${repository.salutation()}, $addressee!"
    }

    private companion object {
        const val FALLBACK_NAME = "friend"
    }
}
