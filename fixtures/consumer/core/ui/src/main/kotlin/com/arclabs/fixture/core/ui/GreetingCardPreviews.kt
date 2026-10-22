package com.arclabs.fixture.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun GreetingCardPreview() {
    GreetingCard(greeting = SampleData.GREETING)
}
