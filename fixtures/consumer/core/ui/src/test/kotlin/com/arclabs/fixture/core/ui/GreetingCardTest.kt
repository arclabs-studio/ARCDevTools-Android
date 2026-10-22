package com.arclabs.fixture.core.ui

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Robolectric + Compose UI test (JUnit 4 rule, run by the JUnit Vintage engine). */
@RunWith(RobolectricTestRunner::class)
class GreetingCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shows the greeting it is given`() {
        // Given
        composeTestRule.setContent { GreetingCard(greeting = SampleData.GREETING) }

        // When / Then
        composeTestRule.onNodeWithText("Hello, Ada!").assertExists()
    }
}
