package com.bellako.kiwi.common.screens.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.KiwiTheme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import kotlinx.coroutines.launch

@Composable
fun Kiwi_DraggableBar(
    modifier: Modifier = Modifier,
    content: @Composable (currentStateIndex: Int) -> Unit,
    states: List<Int>,
    initialStateIndex: Int = 0,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val scope = rememberCoroutineScope()

    // states
    val statesBottom: List<Float> = states.map { state -> getResponsiveSizeHeight(state).toFloat() }

    fun closestState(value: Float): Float = statesBottom.minByOrNull { kotlin.math.abs(it - value) } ?: statesBottom.first()

    // offset
    val animatableOffset = remember { Animatable(statesBottom[initialStateIndex]) }
    var offsetY by remember { mutableFloatStateOf(statesBottom[initialStateIndex]) }

    // velocity
    val velocityThreshold = 400f
    var lastPosition by remember { mutableFloatStateOf(animatableOffset.value) }
    var lastTime by remember { mutableLongStateOf(0L) }
    var dragVelocity by remember { mutableFloatStateOf(0f) }

    val offset = getScreenHeight(withoutInsetTop = true).dp - animatableOffset.value.dp
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(statesBottom.max().dp)
                .offset(y = offset)
                .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp)))
                .background(backgroundColor)
                .then(modifier),
    ) {
        // Drag area
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                lastTime = System.currentTimeMillis()
                                lastPosition = animatableOffset.value
                            },
                            onDrag = { change, dragAmount ->
                                val dragAmountY = with(density) { dragAmount.y.toDp() }.value
                                val now = System.currentTimeMillis()
                                val elapsed = now - lastTime
                                if (elapsed > 0) {
                                    val newPos = animatableOffset.value - dragAmountY
                                    dragVelocity = (newPos - lastPosition) / (elapsed / 1000f)
                                    lastPosition = newPos
                                    lastTime = now
                                }
                                val newOffset =
                                    (animatableOffset.value - dragAmountY)
                                        .coerceIn(
                                            statesBottom.minOrNull() ?: 0f,
                                            statesBottom.maxOrNull() ?: Float.MAX_VALUE,
                                        )
                                scope.launch {
                                    animatableOffset.snapTo(newOffset)
                                }
                                offsetY = newOffset
                                change.consume()
                            },
                            onDragEnd = {
                                scope.launch {
                                    val target =
                                        when {
                                            dragVelocity > velocityThreshold -> {
                                                // High velocity upwards, snap to next upper state
                                                statesBottom.filter { it >= animatableOffset.value }.minOrNull() ?: statesBottom.last()
                                            }
                                            dragVelocity < -velocityThreshold -> {
                                                // High velocity downwards, snap to next lower state
                                                statesBottom.filter { it <= animatableOffset.value }.maxOrNull() ?: statesBottom.first()
                                            }
                                            else -> {
                                                // Low velocity, snap to closest state
                                                closestState(animatableOffset.value)
                                            }
                                        }
                                    animatableOffset.animateTo(target)
                                    offsetY = target
                                }
                            },
                        )
                    },
        ) {
            Kiwi_Spacer(Spacing.large)
            Kiwi_Spacer(Spacing.large)
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = getResponsiveSizeHeight(Spacing.large + Spacing.large))
                    .pointerInput(Unit) { /* prevent inputs behind this */ },
        )

        val closestLargerIndex = statesBottom.indexOf(statesBottom.filter { it >= offsetY }.minOrNull())
        content(closestLargerIndex)
    }
}

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
private fun Kiwi_DraggableBar_Preview_0() {
    Kiwi_DraggableBar_Preview(0)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
private fun Kiwi_DraggableBar_Preview_1() {
    Kiwi_DraggableBar_Preview(1)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
private fun Kiwi_DraggableBar_Preview_2() {
    Kiwi_DraggableBar_Preview(2)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Kiwi_DraggableBar_Preview(initialStateIndex: Int = 0) {
    KiwiTheme {
        Kiwi_DraggableBar(
            content = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(getResponsiveSizeHeight(1600.dp)),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Kiwi_H3(
                        KiwiTextArguments(
                            "Content",
                            TextAlign.Center,
                            MaterialTheme.colorScheme.secondary,
                        ),
                    )
                }
            },
            states = listOf(0, 100, 600),
            initialStateIndex = initialStateIndex,
        )
    }
}
