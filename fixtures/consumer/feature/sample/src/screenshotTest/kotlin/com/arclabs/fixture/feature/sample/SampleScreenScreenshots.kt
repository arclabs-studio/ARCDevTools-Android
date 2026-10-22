package com.arclabs.fixture.feature.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.arclabs.fixture.core.ui.SampleData

@PreviewTest
@Preview(showBackground = true)
@Composable
fun SampleScreenContentScreenshot() {
    SampleScreen(uiState = SampleUiState.Content(SampleData.GREETING))
}
