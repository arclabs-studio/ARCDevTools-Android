package com.arclabs.fixture.feature.sample

/** State of the sample screen. */
sealed interface SampleUiState {
    /** The greeting is being prepared. */
    data object Loading : SampleUiState

    /** The greeting is ready to show. */
    data class Content(val greeting: String) : SampleUiState
}
