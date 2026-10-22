package com.arclabs.fixture.feature.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arclabs.fixture.core.ui.SampleData

@Preview(showBackground = true)
@Composable
private fun SampleScreenContentPreview() {
    SampleScreen(uiState = SampleUiState.Content(SampleData.GREETING))
}

@Preview(showBackground = true)
@Composable
private fun SampleScreenLoadingPreview() {
    SampleScreen(uiState = SampleUiState.Loading)
}
