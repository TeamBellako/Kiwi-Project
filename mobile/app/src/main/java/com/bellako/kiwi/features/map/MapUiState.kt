package com.bellako.kiwi.features.map

import androidx.compose.ui.geometry.Offset

/**
 * Represents the UI state for the zoomable map.
 *
 * @property scale The current zoom scale of the map
 * @property offset The current offset/position of the map
 * @property mapWidthPx The width of the map image in pixels
 * @property mapHeightPx The height of the map image in pixels
 * @property viewportWidthPx The width of the viewport (visible area) in pixels
 * @property viewportHeightPx The height of the viewport (visible area) in pixels
 */
data class MapUiState(
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val mapWidthPx: Float = 0f,
    val mapHeightPx: Float = 0f,
    val viewportWidthPx: Float = 0f,
    val viewportHeightPx: Float = 0f
)