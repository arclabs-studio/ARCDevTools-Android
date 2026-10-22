package com.arclabs.fixture.feature.sample

import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Navigation 3 saves the back stack by serializing its keys at runtime, so a key that cannot be
 * serialized only fails when the state is saved (Robolectric, JUnit 4 rule run by JUnit Vintage).
 */
@RunWith(RobolectricTestRunner::class)
class SampleNavKeyTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `back stack with the key survives state restore`() {
        // Given: an empty back stack to which the key is pushed after the first composition
        val restorationTester = StateRestorationTester(composeTestRule)
        var backStack: NavBackStack<NavKey>? = null
        restorationTester.setContent { backStack = rememberNavBackStack() }
        composeTestRule.runOnIdle { backStack?.add(SampleNavKey) }

        // When
        backStack = null
        restorationTester.emulateSavedInstanceStateRestore()

        // Then
        assertEquals(listOf<NavKey>(SampleNavKey), backStack?.toList())
    }
}
