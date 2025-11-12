package com.bellako.kiwi.common.screens.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun Kiwi_Button(
    modifier: Modifier = Modifier,
    contentPaddingHorizontal: Dp = 8.dp,
    contentPaddingVertical: Dp = 8.dp,
    textArguments: KiwiTextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    testTag: String = "",
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        Button(
            onClick = {
                AudioManager.playSFX(context, R.raw.snd_ui_button)
                onClick.invoke()
            },
            enabled = enabled,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = color,
                    disabledContainerColor = color.copy(alpha = 0.15f),
                    contentColor = color,
                    disabledContentColor = color.copy(alpha = 0.3f),
                ),
            contentPadding =
                PaddingValues(
                    getResponsiveSizeHeight(contentPaddingHorizontal),
                    getResponsiveSizeHeight(contentPaddingVertical),
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
            shape = RoundedCornerShape(getResponsiveSizeHeight(10.dp)),
        ) {
            val actualTextArguments =
                if (enabled) {
                    textArguments
                } else {
                    textArguments.copy(color = textArguments.color.copy(alpha = 0.3f))
                }
            Kiwi_Label1(actualTextArguments)
        }
    }
}

@Composable
fun Kiwi_HoldButton(
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 2000,
    contentPaddingHorizontal: Dp = 8.dp,
    contentPaddingVertical: Dp = 8.dp,
    textArguments: KiwiTextArguments,
    onHoldComplete: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    fillColor: Color,
    testTag: String = "",
) {
    val context = LocalContext.current
    val view = LocalView.current

    var isHolding by remember { mutableStateOf(false) }
    var targetProgress by remember { mutableFloatStateOf(0f) }
    var startTime by remember { mutableLongStateOf(0L) }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "holdProgress",
    )

    LaunchedEffect(isHolding, startTime) {
        if (isHolding) {
            while (isActive && isHolding) {
                val elapsed = System.currentTimeMillis() - startTime
                targetProgress = (elapsed.toFloat() / holdDurationMillis).coerceIn(0f, 1f)
                if (targetProgress >= 1f) {
                    break
                }
                delay(16)
            }
        } else {
            targetProgress = 0f
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(getResponsiveSizeHeight(10.dp)))
                .background(color)
                .testTag(testTag)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    while (true) {
                        awaitPointerEventScope {
                            val down = awaitFirstDown(requireUnconsumed = false)

                            view.parent?.requestDisallowInterceptTouchEvent(true)

                            isHolding = true
                            startTime = System.currentTimeMillis()
                            var success = false

                            while (isHolding) {
                                val event = awaitPointerEvent()

                                event.changes.forEach { change ->
                                    if (!change.pressed) {
                                        isHolding = false
                                    }
                                    if (change.changedToDown() || change.changedToUp() || change.positionChanged()) {
                                        change.consume()
                                    }
                                }

                                val elapsed = System.currentTimeMillis() - startTime
                                if (elapsed >= holdDurationMillis) {
                                    success = true
                                    break
                                }
                            }

                            if (success) {
                                AudioManager.playSFX(context, R.raw.snd_ui_button)
                                onHoldComplete()
                            }

                            view.parent?.requestDisallowInterceptTouchEvent(false)

                            isHolding = false
                            targetProgress = 0f
                        }
                    }
                },
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(if (enabled) fillColor else fillColor.copy(alpha = 0.3f)),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = getResponsiveSizeHeight(contentPaddingHorizontal),
                        vertical = getResponsiveSizeHeight(contentPaddingVertical),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            val actualTextArguments =
                if (enabled) {
                    textArguments
                } else {
                    textArguments.copy(color = textArguments.color.copy(alpha = 0.3f))
                }
            Kiwi_Label1(actualTextArguments)
        }
    }
}

// -------------------------------------------------------------------------------------------------

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Kiwi_Button_Preview() {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_Theme {
        Column {
            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        "BUTTON",
                        color = kiwiColors.colorF,
                        bold = true,
                    ),
                color = kiwiColors.color5,
                onClick = {},
            )

            Kiwi_Spacer()

            Kiwi_Button(
                textArguments =
                    KiwiTextArguments(
                        "BUTTON",
                        color = kiwiColors.colorF,
                        bold = true,
                    ),
                color = kiwiColors.color5,
                onClick = {},
                enabled = false,
            )
        }
    }
}
