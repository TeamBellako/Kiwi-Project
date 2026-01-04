package com.bellako.kiwi.common.screens.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
private fun Kiwi_Button(
    modifier: Modifier,
    contentPaddingHorizontal: Dp,
    contentPaddingVertical: Dp,
    horizontalMargin: Dp?,
    textArguments: KiwiTextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    testTag: String = "",
    sound: Int = R.raw.snd_ui_button
) {
    val context = LocalContext.current

    Box(modifier) {
        Button(
            onClick = {
                AudioManager.playSFX(context, sound)
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

                    .testTag(testTag)
                    .then(
                        horizontalMargin?.let {
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = getResponsiveSizeHeight(it))
                        } ?: Modifier
                    ),
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
fun Kiwi_FixedSizeButton(
    modifier: Modifier = Modifier,
    contentPaddingVertical: Dp = 8.dp,
    horizontalMargin: Dp = 0.dp,
    textArguments: KiwiTextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    testTag: String = "",
    sound: Int = R.raw.snd_ui_button
) {
    Kiwi_Button(
        modifier,
        0.dp,
        contentPaddingVertical,
        horizontalMargin,
        textArguments,
        onClick,
        enabled,
        color,
        testTag,
        sound
    )
}

@Composable
fun Kiwi_AdaptableSizeButton(
    modifier: Modifier = Modifier,
    contentPaddingHorizontal: Dp = 8.dp,
    contentPaddingVertical: Dp = 8.dp,
    textArguments: KiwiTextArguments,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    testTag: String = "",
    sound: Int = R.raw.snd_ui_button
) {
    Kiwi_Button(
        modifier,
        contentPaddingHorizontal,
        contentPaddingVertical,
        null,
        textArguments,
        onClick,
        enabled,
        color,
        testTag,
        sound
    )
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
    sound: Int = R.raw.snd_ui_button
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

    HandleHoldLogic(
        isHolding = isHolding,
        holdDurationMillis = holdDurationMillis,
        startTime = startTime,
        onProgressChange = { targetProgress = it },
        onComplete = {
            AudioManager.playSFX(context, sound)
            onHoldComplete()
        },
    )

    HoldButtonContent(
        modifier = modifier,
        isEnabled = enabled,
        onHoldStart = {
            view.parent?.requestDisallowInterceptTouchEvent(true)
            isHolding = true
            startTime = System.currentTimeMillis()
        },
        onHoldEnd = {
            isHolding = false
            view.parent?.requestDisallowInterceptTouchEvent(false)
        },
        color = color,
        fillColor = fillColor,
        animatedProgress = animatedProgress,
        textArguments = textArguments,
        contentPaddingHorizontal = contentPaddingHorizontal,
        contentPaddingVertical = contentPaddingVertical,
        testTag = testTag,
    )
}

@Composable
private fun HandleHoldLogic(
    isHolding: Boolean,
    holdDurationMillis: Long,
    startTime: Long,
    onProgressChange: (Float) -> Unit,
    onComplete: () -> Unit,
) {
    LaunchedEffect(isHolding, startTime) {
        if (isHolding) {
            while (isActive) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / holdDurationMillis).coerceIn(0f, 1f)
                onProgressChange(progress)
                if (progress >= 1f) {
                    onComplete()
                    break
                }
                @Suppress("MagicNumber")
                delay(16)
            }
        } else {
            onProgressChange(0f)
        }
    }
}

@Composable
private fun HoldButtonContent(
    modifier: Modifier,
    isEnabled: Boolean,
    onHoldStart: () -> Unit,
    onHoldEnd: () -> Unit,
    color: Color,
    fillColor: Color,
    animatedProgress: Float,
    textArguments: KiwiTextArguments,
    contentPaddingHorizontal: Dp,
    contentPaddingVertical: Dp,
    testTag: String,
) {
    Box(
        modifier =
            modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Max)
                .clip(RoundedCornerShape(getResponsiveSizeHeight(10.dp)))
                .background(if (isEnabled) color else color.copy(alpha = 0.3f))
                .testTag(testTag)
                .holdGestureHandler(isEnabled, onHoldStart, onHoldEnd),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(fillColor),
        )

        Box(
            modifier =
                Modifier
                    .padding(
                        horizontal = getResponsiveSizeHeight(contentPaddingHorizontal),
                        vertical = getResponsiveSizeHeight(contentPaddingVertical),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            val actualTextArgs =
                if (isEnabled) {
                    textArguments
                } else {
                    textArguments.copy(color = textArguments.color.copy(alpha = 0.3f))
                }
            Kiwi_Label1(actualTextArgs)
        }
    }
}

private fun Modifier.holdGestureHandler(
    enabled: Boolean,
    onHoldStart: () -> Unit,
    onHoldEnd: () -> Unit,
): Modifier =
    pointerInput(enabled) {
        if (!enabled) return@pointerInput
        while (true) {
            awaitPointerEventScope {
                awaitFirstDown(requireUnconsumed = false)
                onHoldStart()
                while (true) {
                    val event = awaitPointerEvent()
                    val isPressed = event.changes.any { it.pressed }
                    if (!isPressed) {
                        onHoldEnd()
                        break
                    }
                    event.changes.forEach { it.consume() }
                }
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
            Kiwi_FixedSizeButton(
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

            Kiwi_FixedSizeButton(
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
