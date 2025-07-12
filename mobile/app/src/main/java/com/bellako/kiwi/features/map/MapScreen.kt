package com.bellako.kiwi.features.map

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.components.Kiwi_ZoomableMap
import com.bellako.kiwi.ui.modals.AppBarModal
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.theme.KiwiTheme

/**
 * A screen that displays a zoomable and draggable world map.
 * This integrates the ZoomableMap component into the home screen.
 */
@Composable
fun HomeScreen() {
    // Get the ViewModel using Hilt
    val mapViewModel: MapViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag(CommonTestTags.HOME_SCREEN),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H1(Kiwi_TextArguments(
            "WORLD MAP",
            color = MaterialTheme.colorScheme.inversePrimary,
            bold = true
        ))

        // Use the ZoomableMap component instead of static image
        Kiwi_ZoomableMap(
            mapResourceId = R.drawable.ph_home_map,
            contentDescription = "Interactive World Map",
            viewModel = mapViewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * A screen that displays a zoomable and draggable map with a top app bar.
 * This is an alternative implementation focused specifically on the map functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen() {
    // Get the ViewModel using Hilt
    val mapViewModel: MapViewModel = hiltViewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interactive Map") }
            )
        }
    ) { paddingValues ->
        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Use the ZoomableMap component
            Kiwi_ZoomableMap(
                mapResourceId = R.drawable.ph_home_map,
                contentDescription = "Interactive Map",
                viewModel = mapViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun MapScreenPreview() {
    KiwiTheme {
        Scaffold(
            bottomBar = {
                AppBarModal(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapScreen()
                    DashboardModal(
                        MetricsFakeViewModel(
                            MetricsState(
                                "2025-06-12",
                                1173,
                                9900
                            )
                        )
                    )
                }
            }
        )
    }
}