package com.arclabs.fixture.feature.sample

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.arclabs.fixture.domain.GreetingUseCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Robolectric + Compose UI tests (JUnit 4 rule, run by the JUnit Vintage engine). */
@RunWith(RobolectricTestRunner::class)
class SampleScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `Loading shows a labelled progress indicator`() {
        // Given
        composeTestRule.setContent { SampleScreen(uiState = SampleUiState.Loading) }

        // When / Then
        composeTestRule.onNodeWithContentDescription("Loading greeting").assertExists()
    }

    @Test
    fun `route shows the greeting built by the view model`() {
        // Given
        val viewModel = SampleViewModel(GreetingUseCase(FakeGreetingRepository("Hi")))

        // When
        composeTestRule.setContent { SampleRoute(viewModel = viewModel) }

        // Then
        composeTestRule.onNodeWithText("Hi, Android!").assertExists()
    }
}
