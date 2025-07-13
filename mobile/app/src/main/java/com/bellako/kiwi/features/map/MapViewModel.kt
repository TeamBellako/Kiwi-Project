package com.bellako.kiwi.features.map

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.services.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

@HiltViewModel
class MapViewModel @Inject constructor() : BaseViewModel(), IMapViewModel {
    private var initialScale: Float = 4.0f
    private var minScale: Float = 2.0f
    private var maxScale: Float = 8.0f
    private var initialPositionFactor: Float = 0.8f
    private var dragLimitFactor: Float = 0.9f

    private val _state = MutableStateFlow(MapState(scale = initialScale))
    override val state: StateFlow<MapState> = _state.asStateFlow()

    private var initialPositionSet = false

    fun setParameters(
        initialScale: Float = this.initialScale,
        minScale: Float = this.minScale,
        maxScale: Float = this.maxScale,
        initialPositionFactor: Float = this.initialPositionFactor,
        dragLimitFactor: Float = this.dragLimitFactor
    ) {
        this.initialScale = initialScale
        this.minScale = minScale
        this.maxScale = maxScale
        this.initialPositionFactor = initialPositionFactor
        this.dragLimitFactor = dragLimitFactor

        if (_state.value.scale != initialScale) {
            _state.value = _state.value.copy(scale = initialScale)
        }
    }
    override fun updateDimensions(
        mapWidth: Float,
        mapHeight: Float,
        viewportWidth: Float,
        viewportHeight: Float
    ) {
        // Only update dimensions if they are valid (greater than 0)
        // This prevents overwriting valid dimensions with invalid ones
        val currentState = _state.value
        val newMapWidth = if (mapWidth > 0) mapWidth else currentState.mapWidthPx
        val newMapHeight = if (mapHeight > 0) mapHeight else currentState.mapHeightPx
        val newViewportWidth = if (viewportWidth > 0) viewportWidth else currentState.viewportWidthPx
        val newViewportHeight = if (viewportHeight > 0) viewportHeight else currentState.viewportHeightPx

        _state.value = currentState.copy(
            mapWidthPx = newMapWidth,
            mapHeightPx = newMapHeight,
            viewportWidthPx = newViewportWidth,
            viewportHeightPx = newViewportHeight
        )

        if (!initialPositionSet && newMapWidth > 0 && newMapHeight > 0 && 
            newViewportWidth > 0 && newViewportHeight > 0) {
            setInitialPosition()
            initialPositionSet = true
        }
    }

    private fun setInitialPosition() {
        val currentState = _state.value

        val scaledMapWidth = currentState.mapWidthPx * currentState.scale
        val scaledMapHeight = currentState.mapHeightPx * currentState.scale

        val maxOffsetX = (scaledMapWidth - currentState.viewportWidthPx) / 2
        val maxOffsetY = (scaledMapHeight - currentState.viewportHeightPx) / 2

        // Set offset to upper left corner (negative X, negative Y)
        val newOffset = Offset(
            x = -maxOffsetX * initialPositionFactor,
            y = -maxOffsetY * initialPositionFactor
        )

        val constrainedOffset = calculateConstrainedOffset(newOffset, currentState)
        _state.value = currentState.copy(offset = constrainedOffset)
    }

    override fun updateScale(scaleFactor: Float, centroid: Offset) {
        val currentState = _state.value
        val newScale = (currentState.scale * scaleFactor).coerceIn(minScale, maxScale)

        if (newScale == currentState.scale) return

        val newOffset = calculateOffsetForZoom(currentState, newScale, centroid)
        _state.value = currentState.copy(
            scale = newScale,
            offset = newOffset
        )
    }

    override fun updateOffset(delta: Offset) {
        val currentState = _state.value
        val newOffset = calculateConstrainedOffset(
            currentState.offset + delta,
            currentState
        )
        _state.value = currentState.copy(offset = newOffset)
    }

    private fun calculateOffsetForZoom(
        state: MapState,
        newScale: Float,
        centroid: Offset
    ): Offset {
        val scaleFactor = newScale / state.scale

        // Calculate the position of the centroid relative to the center of the viewport
        val centroidRelativeToCenter = centroid - Offset(
            state.viewportWidthPx / 2f,
            state.viewportHeightPx / 2f
        )

        // Formula: newOffset = oldOffset + (centroidRelativeToCenter * (1 - scaleFactor))
        val offsetDelta = centroidRelativeToCenter * (1 - scaleFactor)
        val unconstrained = state.offset + offsetDelta

        return calculateConstrainedOffset(unconstrained, state.copy(scale = newScale))
    }

    private fun calculateConstrainedOffset(
        offset: Offset,
        state: MapState
    ): Offset {
        if (state.mapWidthPx <= 0 || state.mapHeightPx <= 0 ||
            state.viewportWidthPx <= 0 || state.viewportHeightPx <= 0) {
            return offset
        }

        val scaledMapWidth = state.mapWidthPx * state.scale
        val scaledMapHeight = state.mapHeightPx * state.scale

        // Half the difference between the scaled map size and the viewport size
        val maxOffsetX = (scaledMapWidth - state.viewportWidthPx) / 2
        val maxOffsetY = (scaledMapHeight - state.viewportHeightPx) / 2

        // Don't allow panning if the map is smaller than the viewport
        val effectiveMaxOffsetX = max(0f, maxOffsetX)
        val effectiveMaxOffsetY = max(0f, maxOffsetY)

        // Add margin to prevent dragging too close to the edge
        val restrictedMaxOffsetX = effectiveMaxOffsetX * dragLimitFactor
        val restrictedMaxOffsetY = effectiveMaxOffsetY * dragLimitFactor

        // When dragging beyond the maximum allowed offset, set the offset to exactly the maximum allowed value
        // This is the key fix for the dragging limit issue

        // If the absolute value of the offset exceeds the restricted maximum,
        // set it to exactly the restricted maximum with the appropriate sign
        val resultX = if (abs(offset.x) > restrictedMaxOffsetX) {
            sign(offset.x) * restrictedMaxOffsetX
        } else {
            offset.x
        }

        val resultY = if (abs(offset.y) > restrictedMaxOffsetY) {
            sign(offset.y) * restrictedMaxOffsetY
        } else {
            offset.y
        }

        return Offset(resultX, resultY)
    }
}
