package com.bellako.kiwi.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun <T> Kiwi_AnchoredDraggable(
    modifier: Modifier = Modifier,
    initialState: T,
    anchors: List<Pair<T, Float>>,
    onStateChange: (T) -> Unit,
    content: @Composable (T) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(anchors.first { it.first == initialState }.second) }
    var currentState by remember { mutableStateOf(initialState) }
    val screenHeightPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .offset {
                IntOffset(x = 0, y = (screenHeightPx - offsetY.value).toInt())
            }
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        offsetY.snapTo(
                            (offsetY.value - delta)
                                .coerceIn(anchors.minOf { it.second }, anchors.maxOf { it.second })
                        )
                    }
                },
                onDragStopped = {
                    coroutineScope.launch {
                        val (nearestState, nearestOffset) = anchors.minByOrNull {
                            abs(it.second - offsetY.value)
                        }!!
                        currentState = nearestState
                        onStateChange(nearestState)
                        offsetY.animateTo(
                            targetValue = nearestOffset,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { offsetY.value.toDp() })
        ) {
            content(currentState)
        }
    }
}
