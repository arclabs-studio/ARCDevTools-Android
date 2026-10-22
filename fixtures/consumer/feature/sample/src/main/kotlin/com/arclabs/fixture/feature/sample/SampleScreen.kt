package com.arclabs.fixture.feature.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arclabs.fixture.core.ui.GreetingCard

/** Entry point of the sample screen for the [SampleNavKey] Navigation 3 entry. */
@Composable
fun SampleRoute(modifier: Modifier = Modifier, viewModel: SampleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SampleScreen(uiState = uiState, modifier = modifier)
}

/** Stateless sample screen: a progress indicator while loading, then the greeting. */
@Composable
internal fun SampleScreen(uiState: SampleUiState, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            SampleUiState.Loading -> {
                val loadingDescription = stringResource(R.string.sample_loading)
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = loadingDescription },
                )
            }

            is SampleUiState.Content -> {
                GreetingCard(greeting = uiState.greeting)
            }
        }
    }
}
