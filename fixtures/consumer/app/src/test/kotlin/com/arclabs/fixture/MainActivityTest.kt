package com.arclabs.fixture

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * End-to-end through the real Hilt graph (FixtureApplication), Navigation 3, the sample
 * ViewModel and Room 3 (Robolectric, JUnit 4 rule run by the JUnit Vintage engine).
 */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `greets Android with the default salutation`() {
        // Given / When: the activity is launched by the rule

        // Then
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Hello, Android!").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Hello, Android!").assertExists()
    }
}
