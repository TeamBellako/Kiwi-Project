package com.bellako.kiwi.features.map.model

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.common.model.BaseViewModel
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
    private var initialScale: Float = 0f
    private var minScale: Float = 0f
    private var maxScale: Float = 0f
    private var initialPosition: Offset = Offset(0f, 0f)
    private var dragLimitFactor: Float = 0f

    private val _state = MutableStateFlow(MapState(scale = initialScale))
    override val state: StateFlow<MapState> = _state.asStateFlow()

    fun setParameters(
        initialScale: Float,
        minScale: Float,
        maxScale: Float,
        initialPosition: Offset,
        dragLimitFactor: Float,
        mapWidthPx: Float,
        mapHeightPx: Float,
        viewportWidthPx: Float,
        viewportHeightPx: Float
    ) {
        this.initialScale = initialScale
        _state.value = _state.value.copy(scale = this.initialScale)
        _state.value = _state.value.copy(scaleBase = viewportHeightPx / viewportWidthPx)

        this.minScale = minScale
        this.maxScale = maxScale
        this.initialPosition = initialPosition
        this.dragLimitFactor = dragLimitFactor

        _state.value = _state.value.copy(viewportWidthPx = viewportWidthPx)
        _state.value = _state.value.copy(viewportHeightPx = viewportHeightPx)
        // considering always orientation portrait (viewportHeightPx > viewportWidthPx)
        _state.value = _state.value.copy(mapWidthPx = mapWidthPx * (viewportHeightPx / mapHeightPx))
        _state.value = _state.value.copy(mapHeightPx = mapHeightPx * (viewportHeightPx / mapHeightPx))

        setInitialPositionScale()
    }

    private fun setInitialPositionScale() {
        setScale(initialScale)
        setOffset(Offset(
            -_state.value.mapWidthPx * initialPosition.x.coerceIn(-1f, 1f),
            -_state.value.mapHeightPx * initialPosition.y.coerceIn(-1f, 1f)
        ))
        updateScale(1f, Offset(0f, 0f))
    }

    private fun setScale(newScale: Float) {
        _state.value = _state.value.copy(scale = newScale)
    }

    private fun setOffset(newOffset: Offset) {
        _state.value = _state.value.copy(offset = newOffset)
    }

    override fun updateScale(scaleFactor: Float, centroid: Offset) {
        val newScale = (_state.value.scale * scaleFactor).coerceIn(minScale, maxScale)
        val newOffset = calculateOffsetForZoom(_state.value, newScale, centroid)
        setScale(newScale)
        setOffset(newOffset)
    }

    override fun updateOffset(delta: Offset) {
        val newOffset = calculateConstrainedOffset(_state.value.offset + delta, _state.value)
        setOffset(newOffset)
    }

    private fun calculateOffsetForZoom(state: MapState, newScale: Float, centroid: Offset): Offset {
        val scaleFactor = newScale / state.scale

        // Calculate the position of the centroid relative to the center of the viewport
        val centroidRelativeToCenter = centroid - Offset(state.viewportWidthPx / 2f, state.viewportHeightPx / 2f)

        // Formula: newOffset = oldOffset + (centroidRelativeToCenter * (1 - scaleFactor))
        val offsetDelta = centroidRelativeToCenter * (1f - scaleFactor)
        val newOffset = (state.offset + offsetDelta) * scaleFactor

        return calculateConstrainedOffset(newOffset, state.copy(scale = newScale))
    }

    fun getMaxOffset(state: MapState): Offset {
        val scaledMapWidth = state.mapWidthPx * state.scale
        val scaledMapHeight = state.mapHeightPx * state.scale
        // Half the difference between the scaled map size and the viewport size
        return Offset(
            (scaledMapWidth - state.viewportWidthPx) / 2f,
            (scaledMapHeight - state.viewportHeightPx) / 2f
        )
    }

    private fun calculateConstrainedOffset(offset: Offset, state: MapState): Offset {
        if (state.mapWidthPx <= 0 || state.mapHeightPx <= 0 || state.viewportWidthPx <= 0 || state.viewportHeightPx <= 0) {
            return offset
        }

        val maxOffset = getMaxOffset(state)
        // Don't allow panning if the map is smaller than the viewport
        val effectiveMaxOffsetX = max(0f, maxOffset.x)
        val effectiveMaxOffsetY = max(0f, maxOffset.y)

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
