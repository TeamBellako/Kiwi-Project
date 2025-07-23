package com.bellako.kiwi.features.map

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_H1
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.modals.AppBarModal
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.theme.KiwiTheme

@Composable
fun MapScreen(
    initialZoom: Float = 4.0f,
    minZoom: Float = 2.0f,
    maxZoom: Float = 8.0f,
    initialPositionFactor: Float = 0.8f,
    dragLimitFactor: Float = 0.9f,
    mapResourceId: Int = R.drawable.ph_home_map,
    contentDescription: String = "Interactive World Map",
    title: String = "WORLD MAP",
    viewModel: MapViewModel? = null // Optional parameter for testing
) {
    val mapViewModel = viewModel ?: hiltViewModel<MapViewModel>()

    mapViewModel.setParameters(
        initialScale = initialZoom,
        minScale = minZoom,
        maxScale = maxZoom,
        initialPositionFactor = initialPositionFactor,
        dragLimitFactor = dragLimitFactor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag(CommonTestTags.HOME_SCREEN),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H1(Kiwi_TextArguments(
            title,
            color = MaterialTheme.colorScheme.inversePrimary,
            bold = true
        ))

        ZoomableMap(
            mapResourceId = mapResourceId,
            contentDescription = contentDescription,
            viewModel = mapViewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ZoomableMap(
    mapResourceId: Int,
    contentDescription: String,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val mapState by viewModel.state.collectAsState()
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned { coordinates ->
                with(density) {
                    viewModel.updateDimensions(
                        mapWidth = mapState.mapWidthPx,
                        mapHeight = mapState.mapHeightPx,
                        viewportWidth = coordinates.size.width.toFloat(),
                        viewportHeight = coordinates.size.height.toFloat()
                    )
                }
            }
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, zoom, _ ->
                        viewModel.updateScale(zoom, centroid)
                        viewModel.updateOffset(pan)
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Kiwi_Image(
            painterResourceId = mapResourceId,
            alt = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = mapState.scale,
                    scaleY = mapState.scale,
                    translationX = mapState.offset.x,
                    translationY = mapState.offset.y
                )
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        viewModel.updateDimensions(
                            mapWidth = coordinates.size.width.toFloat(),
                            mapHeight = coordinates.size.height.toFloat(),
                            viewportWidth = mapState.viewportWidthPx,
                            viewportHeight = mapState.viewportHeightPx
                        )
                    }
                }
        )
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
