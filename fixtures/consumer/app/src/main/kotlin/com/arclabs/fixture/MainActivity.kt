package com.arclabs.fixture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.arclabs.fixture.feature.sample.SampleNavKey
import com.arclabs.fixture.feature.sample.SampleRoute
import dagger.hilt.android.AndroidEntryPoint

/** Single activity: edge-to-edge, one Navigation 3 back stack starting at [SampleNavKey]. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FixtureTheme {
                val backStack = rememberNavBackStack(SampleNavKey)
                Scaffold { innerPadding ->
                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier.padding(innerPadding),
                        onBack = { backStack.removeLastOrNull() },
                        entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                        entryProvider =
                        entryProvider {
                            entry<SampleNavKey> { SampleRoute() }
                        },
                    )
                }
            }
        }
    }
}
