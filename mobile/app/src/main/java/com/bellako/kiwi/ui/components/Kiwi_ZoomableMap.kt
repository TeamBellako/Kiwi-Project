package com.bellako.kiwi.ui.components

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import com.bellako.kiwi.features.map.MapViewModel

/**
 * A composable that displays a zoomable and draggable map.
 *
 * @param mapResourceId The resource ID of the map image
 * @param contentDescription The content description for accessibility
 * @param viewModel The ViewModel that handles the map state and logic
 * @param modifier Optional modifier for the composable
 */
@Composable
fun Kiwi_ZoomableMap(
    mapResourceId: Int,
    contentDescription: String,
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    // Collect the current state from the ViewModel
    val mapState by viewModel.state.collectAsState()

    // Get the current density for pixel conversion
    val density = LocalDensity.current

    // Container for the map
    Box(
        modifier = modifier
            .clipToBounds() // Prevent drawing outside bounds
            .onGloballyPositioned { coordinates ->
                // Measure the viewport size and inform the ViewModel
                with(density) {
                    viewModel.updateDimensions(
                        mapWidth = mapState.mapWidthPx, // Keep existing map width
                        mapHeight = mapState.mapHeightPx, // Keep existing map height
                        viewportWidth = coordinates.size.width.toFloat(),
                        viewportHeight = coordinates.size.height.toFloat()
                    )
                }
            }
            // Handle zoom and pan gestures
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, zoom, _ ->
                        // Update scale (zoom)
                        viewModel.updateScale(zoom, centroid)

                        // Update offset (pan)
                        viewModel.updateOffset(pan)

                        // Consume all changes to avoid gesture conflicts
                        true
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Use direct values without animation for immediate movement

        // The map image with zoom and pan transformations
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
                    // Measure the image size and inform the ViewModel
                    with(density) {
                        viewModel.updateDimensions(
                            mapWidth = coordinates.size.width.toFloat(),
                            mapHeight = coordinates.size.height.toFloat(),
                            viewportWidth = mapState.viewportWidthPx, // Keep existing viewport width
                            viewportHeight = mapState.viewportHeightPx // Keep existing viewport height
                        )
                    }
                }
        )
    }
}
