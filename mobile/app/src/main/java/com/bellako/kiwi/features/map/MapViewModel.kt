package com.bellako.kiwi.features.map

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.bellako.kiwi.services.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * ViewModel for handling the zoomable and draggable map functionality.
 */
@HiltViewModel
class MapViewModel @Inject constructor() : BaseViewModel(), IMapViewModel {
    // Initial zoom level for the map
    private val INITIAL_SCALE = 2.0f

    private val _state = MutableStateFlow(MapUiState(scale = INITIAL_SCALE))
    override val state: StateFlow<MapUiState> = _state.asStateFlow()

    // Constants for zoom limits
    private val MIN_SCALE = 1f
    private val MAX_SCALE = 4f

    // Flag to track if initial position has been set
    private var initialPositionSet = false

    /**
     * Updates the map dimensions and viewport size
     */
    override fun updateDimensions(
        mapWidth: Float,
        mapHeight: Float,
        viewportWidth: Float,
        viewportHeight: Float
    ) {
        _state.value = _state.value.copy(
            mapWidthPx = mapWidth,
            mapHeightPx = mapHeight,
            viewportWidthPx = viewportWidth,
            viewportHeightPx = viewportHeight
        )

        // Set initial position to upper right corner if not already set
        // and if all dimensions are available
        if (!initialPositionSet && mapWidth > 0 && mapHeight > 0 && 
            viewportWidth > 0 && viewportHeight > 0) {
            setInitialPosition()
            initialPositionSet = true
        }
    }

    /**
     * Sets the initial position of the map to focus on the upper right corner
     */
    private fun setInitialPosition() {
        val currentState = _state.value

        // Calculate the maximum allowed offset in each direction based on current scale
        val scaledMapWidth = currentState.mapWidthPx * currentState.scale
        val scaledMapHeight = currentState.mapHeightPx * currentState.scale

        val maxOffsetX = (scaledMapWidth - currentState.viewportWidthPx) / 2
        val maxOffsetY = (scaledMapHeight - currentState.viewportHeightPx) / 2

        // Set offset to upper right corner (positive X, negative Y)
        // Use 80% of max offset to avoid being right at the edge
        val newOffset = Offset(
            x = maxOffsetX * 0.8f,
            y = -maxOffsetY * 0.8f
        )

        // Apply boundary constraints to the new offset
        val constrainedOffset = calculateConstrainedOffset(newOffset, currentState)

        // Update state with new offset
        _state.value = currentState.copy(offset = constrainedOffset)
    }

    /**
     * Updates the scale (zoom) of the map
     */
    override fun updateScale(scaleFactor: Float, centroid: Offset) {
        val currentState = _state.value

        // Calculate new scale with limits
        val newScale = (currentState.scale * scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)

        // If scale hasn't changed, no need to update
        if (newScale == currentState.scale) return

        // Calculate new offset to keep the centroid point fixed during zoom
        val newOffset = calculateOffsetForZoom(currentState, newScale, centroid)

        // Update state with new scale and offset
        _state.value = currentState.copy(
            scale = newScale,
            offset = newOffset
        )
    }

    /**
     * Updates the offset (position) of the map
     */
    override fun updateOffset(delta: Offset) {
        val currentState = _state.value

        // Calculate new offset with boundary constraints
        val newOffset = calculateConstrainedOffset(
            currentState.offset + delta,
            currentState
        )

        // Update state with new offset
        _state.value = currentState.copy(offset = newOffset)
    }

    /**
     * Calculates the new offset when zooming to keep the centroid point fixed
     * This ensures zoom happens at the finger position
     */
    private fun calculateOffsetForZoom(
        state: MapUiState,
        newScale: Float,
        centroid: Offset
    ): Offset {
        // Calculate how much the scale has changed
        val scaleFactor = newScale / state.scale

        // Calculate the position of the centroid relative to the center of the viewport
        // This is the point that should stay fixed during zoom
        val centroidRelativeToCenter = centroid - Offset(
            state.viewportWidthPx / 2f,
            state.viewportHeightPx / 2f
        )

        // Calculate how the current offset should change to keep the centroid fixed
        // The formula is: newOffset = oldOffset + (centroidRelativeToCenter * (1 - scaleFactor))
        val offsetDelta = centroidRelativeToCenter * (1 - scaleFactor)

        // Calculate the new unconstrained offset
        val unconstrained = state.offset + offsetDelta

        // Apply boundary constraints to the new offset
        return calculateConstrainedOffset(unconstrained, state.copy(scale = newScale))
    }

    /**
     * Calculates the constrained offset to keep the map within boundaries
     */
    private fun calculateConstrainedOffset(
        offset: Offset,
        state: MapUiState
    ): Offset {
        // If dimensions are not set yet, return the original offset
        if (state.mapWidthPx <= 0 || state.mapHeightPx <= 0 ||
            state.viewportWidthPx <= 0 || state.viewportHeightPx <= 0) {
            return offset
        }

        // Calculate the scaled dimensions of the map
        val scaledMapWidth = state.mapWidthPx * state.scale
        val scaledMapHeight = state.mapHeightPx * state.scale

        // Calculate the maximum allowed offset in each direction
        val maxOffsetX = (scaledMapWidth - state.viewportWidthPx) / 2
        val maxOffsetY = (scaledMapHeight - state.viewportHeightPx) / 2

        // Handle the case when the scaled map is smaller than the viewport
        val effectiveMaxOffsetX = max(0f, maxOffsetX)
        val effectiveMaxOffsetY = max(0f, maxOffsetY)

        // Constrain the offset within the boundaries
        return Offset(
            x = offset.x.coerceIn(-effectiveMaxOffsetX, effectiveMaxOffsetX),
            y = offset.y.coerceIn(-effectiveMaxOffsetY, effectiveMaxOffsetY)
        )
    }
}
