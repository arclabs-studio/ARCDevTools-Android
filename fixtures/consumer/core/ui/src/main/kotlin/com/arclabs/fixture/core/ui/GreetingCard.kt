package com.arclabs.fixture.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val ContentPadding = 16.dp

/** A card that shows a single greeting in the headline style. */
@Composable
fun GreetingCard(greeting: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(ContentPadding),
        )
    }
}
