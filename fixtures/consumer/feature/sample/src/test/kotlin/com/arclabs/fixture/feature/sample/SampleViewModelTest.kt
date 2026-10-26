package com.arclabs.fixture.feature.sample

import app.cash.turbine.test
import com.arclabs.fixture.domain.GreetingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
@Tag("unit")
class SampleViewModelTest {
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits Loading then the greeting`() =
        runTest {
            // Given
            val sut = makeSut(salutation = "Hola")

            // When / Then
            sut.uiState.test {
                assertEquals(SampleUiState.Loading, awaitItem())
                assertEquals(SampleUiState.Content("Hola, Android!"), awaitItem())
            }
        }

    private fun makeSut(salutation: String) = SampleViewModel(GreetingUseCase(FakeGreetingRepository(salutation)))
}
