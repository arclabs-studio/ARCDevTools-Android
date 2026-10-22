package com.arclabs.fixture

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arclabs.fixture.core.ui.GreetingCard
import com.arclabs.fixture.core.ui.SampleData

@Preview
@Composable
private fun FixtureThemeLightPreview() {
    FixtureTheme(darkTheme = false) { GreetingCard(greeting = SampleData.GREETING) }
}

@Preview
@Composable
private fun FixtureThemeDarkPreview() {
    FixtureTheme(darkTheme = true) { GreetingCard(greeting = SampleData.GREETING) }
}
