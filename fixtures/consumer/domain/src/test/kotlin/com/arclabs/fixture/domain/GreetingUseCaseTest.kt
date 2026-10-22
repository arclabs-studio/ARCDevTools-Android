package com.arclabs.fixture.domain

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@Tag("unit")
class GreetingUseCaseTest {
    @Test
    fun `greets the name with the stored salutation`() =
        runTest {
            // Given
            val sut = makeSut(salutation = "Hola")

            // When
            val greeting = sut("Ada")

            // Then
            assertEquals("Hola, Ada!", greeting)
        }

    @Test
    fun `ignores whitespace around the name`() =
        runTest {
            // Given
            val sut = makeSut(salutation = "Hello")

            // When
            val greeting = sut("  Grace \n")

            // Then
            assertEquals("Hello, Grace!", greeting)
        }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "\t"])
    fun `greets a blank name as friend`(name: String) =
        runTest {
            // Given
            val sut = makeSut(salutation = "Hello")

            // When
            val greeting = sut(name)

            // Then
            assertEquals("Hello, friend!", greeting)
        }

    private fun makeSut(salutation: String) = GreetingUseCase(FakeGreetingRepository(salutation))
}
