package com.bellako.kiwi.features.map.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_TextArguments
import com.bellako.kiwi.common.screens.modals.AppBarModal
import com.bellako.kiwi.common.screens.modals.DashboardModal
import com.bellako.kiwi.common.utils.detectTransformGesturesAndEnd
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.getScreenHeight
import com.bellako.kiwi.ui.getScreenWidth


/**
 * @param minZoom how small (zoom out) the map can be, considering 1 the full map on screen
 * @param maxZoom how big (zoom in) the map can be, considering 1 the full map on screen
 * @param initialZoom (minZoom..maxZoom)
 * @param initialPosition (-1,1) relative to the center of the map
 * @param dragLimitFactor (0,1) padding for the map limit
 * @param mapResourceId image to show as the map
 * @param title
 * @param viewModel Optional parameter for testing
 */
@Composable
fun MapScreen(
    minZoom: Float = 1.5f,
    maxZoom: Float = 6f,
    initialZoom: Float = 2f,
    initialPosition: Offset = Offset(-0.4f, -0.4f),
    dragLimitFactor: Float = 1f,
    mapResourceId: Int = R.drawable.ph_home_map,
    title: String = "WORLD MAP",
    viewModel: MapViewModel? = null
) {
    val mapViewModel = viewModel ?: hiltViewModel<MapViewModel>()
    val density = LocalDensity.current
    val imageBitmap = ImageBitmap.imageResource(id = mapResourceId)

    mapViewModel.setParameters(
        minScale = minZoom,
        maxScale = maxZoom,
        initialScale = initialZoom,
        initialPosition = initialPosition,
        dragLimitFactor = dragLimitFactor,
        mapWidthPx = imageBitmap.width.toFloat(),
        mapHeightPx = imageBitmap.height.toFloat(),
        viewportWidthPx = with(density) { getScreenWidth().dp.toPx() },
        viewportHeightPx = with(density) { getScreenHeight(withoutInsetTop = true, withoutInsetBottom = true).dp.toPx() }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag(CommonTestTags.HOME_SCREEN),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H2(Kiwi_TextArguments(
            title,
            color = MaterialTheme.colorScheme.inversePrimary,
            bold = true
        ))

        InteractiveMap(
            mapResourceId = mapResourceId,
            viewModel = mapViewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun InteractiveMap(
    mapResourceId: Int,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val mapState by viewModel.state.collectAsState()

    Box(
        modifier = modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTransformGesturesAndEnd(
                    onGesture = { centroid, pan, zoom, _ ->
                        viewModel.updateScale(zoom, centroid)
                        viewModel.updateOffset(pan)
                    },
                    onGestureEnd = {
                        viewModel.startFling()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Kiwi_Image(
            painterResourceId = mapResourceId,
            alt = "Interactive Map",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = mapState.scale * mapState.scaleBase,
                    scaleY = mapState.scale * mapState.scaleBase,
                    translationX = mapState.offset.x,
                    translationY = mapState.offset.y
                )
        )
    }
}

// -------------------------------------------------------------------------------------------------

@SuppressLint("ViewModelConstructorInComposable")
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
                    DashboardModal(MetricsFakeViewModel(MetricsState("2025-06-12", 1173, 9900)))
                }
            }
        )
    }
}
