package com.bellako.kiwi.features.map.model

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.map.data.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

private const val FLING_FRICTION = 0.9f // to brake the velocity [0..1] the lower it is, the faster it stops
private const val FLING_MIN_VELOCITY = 10f // threshold to stop the fling
private const val FRAME_MILLIS = 16L

@HiltViewModel
class MapViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        IMapViewModel {
        private var initialScale: Float = 0f
        private var minScale: Float = 0f
        private var maxScale: Float = 0f
        private var dragLimitFactor: Float = 0f

        private val _state = MutableStateFlow(MapState(scale = initialScale))
        override val state: StateFlow<MapState> = _state.asStateFlow()
        override val previousState = MutableStateFlow(MapState())

        private val _selectedNodeId = MutableStateFlow<Int?>(null)
        val selectedNodeId: StateFlow<Int?> = _selectedNodeId.asStateFlow()

        private var flingJob: Job? = null
        private var flingLastPosition = Offset(0f, 0f)
        private var flingLastTime = 0L
        private var flingVelocity = Offset(0f, 0f)

        // ---------------------------------------------------------------------------------------------

        fun setParameters(
            initialScale: Float,
            minScale: Float,
            maxScale: Float,
            dragLimitFactor: Float,
            mapWidthPx: Float,
            mapHeightPx: Float,
            viewportWidthPx: Float,
            viewportHeightPx: Float,
        ) {
            this.initialScale = initialScale
            this.minScale = minScale
            this.maxScale = maxScale
            this.dragLimitFactor = dragLimitFactor

            val scaledMapWidth = mapWidthPx * (viewportHeightPx / mapWidthPx)
            val scaledMapHeight = mapHeightPx * (viewportHeightPx / mapWidthPx)

            _state.value =
                _state.value.copy(
                    scale = initialScale,
                    scaleBase = viewportHeightPx / viewportWidthPx,
                    viewportWidthPx = viewportWidthPx,
                    viewportHeightPx = viewportHeightPx,
                    mapWidthPx = scaledMapWidth,
                    mapHeightPx = scaledMapHeight,
                )

            updatePreviousState()
        }

        private fun setScale(newScale: Float) {
            _state.value = _state.value.copy(scale = newScale)
        }

        private fun setOffset(newOffset: Offset) {
            _state.value = _state.value.copy(offset = newOffset)
        }

        // ---------------------------------------------------------------------------------------------

        override fun updatePreviousState() {
            previousState.value = _state.value
        }

        override fun updateScale(
            scaleFactor: Float,
            centroid: Offset,
        ) {
            val newScale = (_state.value.scale * scaleFactor).coerceIn(minScale, maxScale)
            val newOffset = calculateOffsetForZoom(_state.value, newScale, centroid)
            setScale(newScale)
            setOffset(newOffset)
            unSelectNode()
        }

        override fun updateOffset(delta: Offset) {
            val newOffset = calculateConstrainedOffset(_state.value.offset + delta, _state.value)
            setOffset(newOffset)
            updateFling(delta)
        }

        private fun calculateOffsetForZoom(
            state: MapState,
            newScale: Float,
            centroid: Offset,
        ): Offset {
            val scaleFactor = newScale / state.scale

            val centroidRelativeToCenter = centroid - Offset(state.viewportWidthPx / 2f, state.viewportHeightPx / 2f)

            val offsetDelta = centroidRelativeToCenter * (1f - scaleFactor)
            val newOffset = (state.offset + offsetDelta) * scaleFactor

            return calculateConstrainedOffset(newOffset, state.copy(scale = newScale))
        }

        fun getMaxOffset(state: MapState): Offset {
            val scaledMapWidth = state.mapWidthPx * state.scale
            val scaledMapHeight = state.mapHeightPx * state.scale
            // Half the difference between the scaled map size and the viewport size
            return Offset(
                max(0f, (scaledMapWidth - state.viewportWidthPx) / 2f) * dragLimitFactor,
                max(0f, (scaledMapHeight - state.viewportHeightPx) / 2f) * dragLimitFactor,
            )
        }

        private fun calculateConstrainedOffset(
            offset: Offset,
            state: MapState,
        ): Offset {
            val maxOffset = getMaxOffset(state)
            val resultX = offset.x.coerceIn(-maxOffset.x, maxOffset.x)
            val resultY = offset.y.coerceIn(-maxOffset.y, maxOffset.y)
            return Offset(resultX, resultY)
        }

        // ---------------------------------------------------------------------------------------------

        private fun updateFling(delta: Offset) {
            val now = System.currentTimeMillis()
            val elapsed = now - flingLastTime
            if (elapsed > 0L) {
                val newPos = flingLastPosition + delta
                flingVelocity = (newPos - flingLastPosition) / elapsed.milliseconds.toDouble(DurationUnit.SECONDS).toFloat()
                flingLastPosition = newPos
                flingLastTime = now
            }
        }

        fun startFling() {
            flingJob?.cancel()
            var velocity = flingVelocity
            flingJob =
                viewModelScope.launch {
                    while (abs(velocity.x) > FLING_MIN_VELOCITY || abs(velocity.y) > FLING_MIN_VELOCITY) {
                        val delta = velocity * FRAME_MILLIS.milliseconds.toDouble(DurationUnit.SECONDS).toFloat() // Calculate displacement
                        velocity *= FLING_FRICTION // Brake
                        updateOffset(delta)
                        delay(FRAME_MILLIS)
                    }
                }
        }

        fun selectNode(
            nodeId: Int,
            nodeX: Float,
            nodeY: Float,
        ) {
            setSelectedNode(nodeId)
            focusOnNode(nodeX, nodeY)
        }

        fun unSelectNode() {
            _selectedNodeId.value = null
        }

        fun getSelectedNode(): Int? = _selectedNodeId.value

        fun setSelectedNode(nodeId: Int) {
            _selectedNodeId.value = nodeId
        }

        fun focusOnNode(
            nodeX: Float,
            nodeY: Float,
        ) {
            val state = _state.value.copy(scale = maxScale)

            val scaledMapWidth = state.mapWidthPx * state.scale
            val scaledMapHeight = state.mapHeightPx * state.scale

            @Suppress("MagicNumber")
            val nodePosX = (nodeX - 0.5f) * scaledMapWidth

            @Suppress("MagicNumber")
            val nodePosY = (0.5f - nodeY) * scaledMapHeight

            val newOffset = Offset(-nodePosX, -nodePosY)
            val constrainedOffset = calculateConstrainedOffset(newOffset, state)

            _state.value = state.copy(offset = constrainedOffset)
            updatePreviousState()
        }
    }
