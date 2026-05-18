package com.bellako.kiwi.features.map.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.lifecycle.viewModelScope
import com.bellako.kiwi.common.model.BaseViewModel
import com.bellako.kiwi.features.map.data.MapInfo
import com.bellako.kiwi.features.map.data.MapState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val FLING_FRICTION = 0.7f // friction per frame (closer to 1 -> slower braking)
private const val FLING_MIN_VELOCITY = 10f // px/s threshold to stop the fling
private const val FRAME_MILLIS = 16L

@HiltViewModel
class MapViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        IMapViewModel {
        private var initialScale: Float = 1f
        private var minScale: Float = 1f
        private var maxScale: Float = 1f
        private var mapMarginFactor: Float = 0f
        private var elasticityFactor = 0f

        private val _state = kotlinx.coroutines.flow.MutableStateFlow(MapState(scale = initialScale))
        override val state: kotlinx.coroutines.flow.StateFlow<MapState> = _state.asStateFlow()

        private var flingJob: Job? = null
        private var lastPointerPosition = Offset.Zero
        private var lastPointerTime = 0L
        private var velocityPxPerSec = Offset.Zero

        // ---------------------------------------------------------------------------------------------

        fun setParameters(
            maxScale: Float,
            mapWidthPx: Float,
            mapHeightPx: Float,
            viewportWidthPx: Float,
            viewportHeightPx: Float,
            mapMarginFactor: Float,
            elasticityFactor: Float,
        ) {
            this.maxScale = maxScale
            this.mapMarginFactor = mapMarginFactor
            this.elasticityFactor = elasticityFactor

            val scaleX = (viewportWidthPx - 2 * mapWidthPx * mapMarginFactor) / mapWidthPx
            val scaleY = (viewportHeightPx - 2 * mapHeightPx * mapMarginFactor) / mapHeightPx
            minScale = min(scaleX, scaleY).coerceAtMost(maxScale)
            initialScale = minScale

            _state.value =
                MapState(
                    scale = initialScale,
                    offset = Offset(0f, 0f),
                    viewportWidthPx = viewportWidthPx,
                    viewportHeightPx = viewportHeightPx,
                    mapWidthPx = mapWidthPx,
                    mapHeightPx = mapHeightPx,
                )

            velocityPxPerSec = Offset.Zero
            lastPointerPosition = Offset.Zero
            lastPointerTime = 0L
        }

        private fun setScale(newScale: Float) {
            _state.value = _state.value.copy(scale = newScale)
        }

        private fun setOffset(newOffset: Offset) {
            _state.value = _state.value.copy(offset = newOffset)
        }

        private fun setMapInfo(mapInfo: MapInfo) {
            _state.value = _state.value.copy(mapInfo = mapInfo)
        }

        override fun setBackgroundColor(color: Color) {
            _state.value = _state.value.copy(mapInfo = _state.value.mapInfo.copy(backgroundColor = color))
        }

        // ---------------------------------------------------------------------------------------------

        override fun updateScale(
            scaleFactor: Float,
            centroid: Offset,
        ) {
            val state = _state.value
            val newScale = (state.scale * scaleFactor).coerceIn(minScale, maxScale)
            val newOffset = calculateOffsetForZoom(state, newScale, centroid)
            setScale(newScale)
            setOffset(newOffset)
            unSelectNode()
        }

        override fun updateOffset(delta: Offset) {
            val state = _state.value
            val maxOffset = getMaxOffset(state)
            val targetOffset = state.offset + delta

            val x =
                when {
                    targetOffset.x < -maxOffset.x -> -maxOffset.x + (targetOffset.x + maxOffset.x) * elasticityFactor
                    targetOffset.x > maxOffset.x -> maxOffset.x + (targetOffset.x - maxOffset.x) * elasticityFactor
                    else -> targetOffset.x
                }

            val y =
                when {
                    targetOffset.y < -maxOffset.y -> -maxOffset.y + (targetOffset.y + maxOffset.y) * elasticityFactor
                    targetOffset.y > maxOffset.y -> maxOffset.y + (targetOffset.y - maxOffset.y) * elasticityFactor
                    else -> targetOffset.y
                }

            setOffset(Offset(x, y))
            updateFling(delta)
        }

        override fun switchMap(mapInfo: MapInfo) {
            setMapInfo(mapInfo)
        }

        /**
         * Zoom math: keep the point under the centroid steady while scaling.
         */
        private fun calculateOffsetForZoom(
            state: MapState,
            newScale: Float,
            centroid: Offset,
        ): Offset {
            val center = Offset(state.viewportWidthPx / 2f, state.viewportHeightPx / 2f)
            val focus = centroid - center
            val scaleFactor = newScale / state.scale
            // (state.offset - focus) scaled, then add focus back
            val newOffset = (state.offset - focus) * scaleFactor + focus
            return calculateConstrainedOffset(newOffset, state.copy(scale = newScale))
        }

        fun getMaxOffset(state: MapState): Offset {
            val scaledMapWidth = state.mapWidthPx * state.scale
            val scaledMapHeight = state.mapHeightPx * state.scale

            val marginX = scaledMapWidth * mapMarginFactor
            val marginY = scaledMapHeight * mapMarginFactor

            return Offset(
                max(0f, (scaledMapWidth - state.viewportWidthPx) / 2f + marginX),
                max(0f, (scaledMapHeight - state.viewportHeightPx) / 2f + marginY),
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

        @Suppress("MagicNumber")
        private fun updateFling(delta: Offset) {
            val now = System.currentTimeMillis()
            if (lastPointerTime == 0L) {
                lastPointerTime = now
                lastPointerPosition = delta
                return
            }
            val elapsed = now - lastPointerTime
            if (elapsed <= 0L) {
                lastPointerTime = now
                lastPointerPosition = lastPointerPosition + delta
                return
            }
            // compute velocity in px/sec
            val newPos = lastPointerPosition + delta
            val vx = (newPos.x - lastPointerPosition.x) / (elapsed / 1000f)
            val vy = (newPos.y - lastPointerPosition.y) / (elapsed / 1000f)
            velocityPxPerSec = Offset(vx, vy)
            lastPointerPosition = newPos
            lastPointerTime = now
        }

        /**
         * Smooth fling implemented as a coroutine loop updating offset at ~60fps applying friction.
         * Uses safe main-thread state updates via viewModelScope.
         */
        @Suppress("MagicNumber")
        fun startFling() {
            flingJob?.cancel()
            // copy velocity start
            var vel = velocityPxPerSec
            // quick guard
            if (abs(vel.x) < FLING_MIN_VELOCITY && abs(vel.y) < FLING_MIN_VELOCITY) {
                velocityPxPerSec = Offset.Zero
                return
            }
            flingJob =
                viewModelScope.launch {
                    // animate until both velocity components drop under threshold
                    while (isActive && (abs(vel.x) > FLING_MIN_VELOCITY || abs(vel.y) > FLING_MIN_VELOCITY)) {
                        // displacement for this frame (px)
                        val dtSec = FRAME_MILLIS / 1000f
                        val delta = vel * dtSec
                        val state = _state.value
                        val maxOffset = getMaxOffset(state)
                        // compute tentative new offset and clamp
                        val tentative = state.offset + delta
                        val constrained = calculateConstrainedOffset(tentative, state)
                        setOffset(constrained)

                        // apply friction to velocity
                        vel = vel * FLING_FRICTION

                        // If we hit bounds, reflect a bit (soft collision) and reduce velocity
                        val hitX = constrained.x == -maxOffset.x || constrained.x == maxOffset.x
                        val hitY = constrained.y == -maxOffset.y || constrained.y == maxOffset.y
                        if (hitX) vel = Offset(-vel.x * 0.35f, vel.y)
                        if (hitY) vel = Offset(vel.x, -vel.y * 0.35f)

                        delay(FRAME_MILLIS)
                    }
                    // clear velocities
                    velocityPxPerSec = Offset.Zero
                    lastPointerTime = 0L
                    lastPointerPosition = Offset.Zero
                }
        }

        // ---------------------------------------------------------------------------------------------

        fun selectNode(
            nodeId: Long,
            nodeX: Float,
            nodeY: Float,
            animate: Boolean = true,
        ) {
            if (_state.value.selectedNodeId == nodeId) {
                return
            }
            _state.value = _state.value.copy(selectedNodeId = nodeId)
            if (animate) {
                focusOnNodeAnimated(nodeX, nodeY)
            } else {
                focusOnNodeImmediate(nodeX, nodeY)
            }
        }

        @Suppress("MagicNumber")
        private fun focusOnNodeImmediate(
            nodeX: Float,
            nodeY: Float,
        ) {
            flingJob?.cancel()

            val startState = _state.value
            val targetScale = maxScale.coerceAtLeast(startState.scale)

            val scaledMapWidth = startState.mapWidthPx * targetScale
            val scaledMapHeight = startState.mapHeightPx * targetScale

            val nodePosX = (nodeX - 0.5f) * scaledMapWidth
            val nodePosY = (0.5f - nodeY) * scaledMapHeight
            val desiredOffset = Offset(-nodePosX, -nodePosY)

            val constrainedTargetOffset =
                calculateConstrainedOffset(desiredOffset, startState.copy(scale = targetScale))

            _state.value =
                startState.copy(
                    scale = targetScale,
                    offset = constrainedTargetOffset,
                    isFocusingNode = false,
                )
        }

        fun unSelectNode() {
            _state.value = _state.value.copy(selectedNodeId = null)
        }

        fun setPlayerNode(nodeId: Long) {
            _state.value = _state.value.copy(playerNode = nodeId)
        }

        @Suppress("MagicNumber")
        fun focusOnNodeAnimated(
            nodeX: Float,
            nodeY: Float,
        ) {
            flingJob?.cancel()

            viewModelScope.launch(AndroidUiDispatcher.Main) {
                _state.value = _state.value.copy(isFocusingNode = true)

                val startState = _state.value

                val targetScale = maxScale.coerceAtLeast(startState.scale)

                val scaledMapWidth = startState.mapWidthPx * targetScale
                val scaledMapHeight = startState.mapHeightPx * targetScale

                val nodePosX = (nodeX - 0.5f) * scaledMapWidth
                val nodePosY = (0.5f - nodeY) * scaledMapHeight
                val desiredOffset = Offset(-nodePosX, -nodePosY)

                val constrainedTargetOffset =
                    calculateConstrainedOffset(desiredOffset, startState.copy(scale = targetScale))

                val scaleAnim = Animatable(startState.scale)
                val offsetAnim = Animatable(startState.offset, Offset.VectorConverter)

                launch {
                    scaleAnim.animateTo(targetScale, tween(400, easing = FastOutSlowInEasing)) {
                        _state.value =
                            _state.value.copy(
                                scale = this.value,
                                offset = calculateConstrainedOffset(offsetAnim.value, _state.value.copy(scale = this.value)),
                            )
                    }
                }

                launch {
                    offsetAnim.animateTo(constrainedTargetOffset, tween(400, easing = FastOutSlowInEasing)) {
                        _state.value =
                            _state.value.copy(
                                scale = scaleAnim.value,
                                offset = calculateConstrainedOffset(this.value, _state.value.copy(scale = scaleAnim.value)),
                            )
                    }
                }

                launch {
                    delay(390)
                    _state.value = _state.value.copy(isFocusingNode = false)
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            flingJob?.cancel()
        }
    }
