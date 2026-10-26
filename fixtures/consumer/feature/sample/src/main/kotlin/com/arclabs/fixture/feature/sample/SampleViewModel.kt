package com.arclabs.fixture.feature.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arclabs.fixture.domain.GreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Exposes the greeting for [SampleNavKey] as a [StateFlow] of [SampleUiState]. */
@HiltViewModel
class SampleViewModel @Inject constructor(greetingUseCase: GreetingUseCase) : ViewModel() {
    val uiState: StateFlow<SampleUiState> =
        flow { emit(SampleUiState.Content(greetingUseCase(GREETED_NAME))) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                initialValue = SampleUiState.Loading,
            )

    private companion object {
        const val GREETED_NAME = "Android"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
