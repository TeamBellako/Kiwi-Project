package com.bellako.kiwi.common.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val KIWI_BUTTON_PRESSED_SCALE = 0.92f
private const val KIWI_BUTTON_COLOR_DURATION_MS = 120
private const val KIWI_BUTTON_PRESSED_DARKEN = 0.35f
private const val KIWI_BUTTON_PRESSED_TEXT_LIGHTEN = 0.45f

@Suppress("LongParameterList")
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
    sound: Int = R.raw.snd_ui_button,
    iconRes: Int? = null,
    iconSize: Dp = 10.dp,
    iconSpacer: Dp = Spacing.small,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }

    val animatedColor by animateColorAsState(
        targetValue =
            if (pressed) lerp(color, Color.Black, KIWI_BUTTON_PRESSED_DARKEN) else color,
        animationSpec = tween(durationMillis = KIWI_BUTTON_COLOR_DURATION_MS),
        label = "buttonColor",
    )

    val animatedTextColor by animateColorAsState(
        targetValue =
            if (pressed) {
                lerp(textArguments.color, Color.White, KIWI_BUTTON_PRESSED_TEXT_LIGHTEN)
            } else {
                textArguments.color
            },
        animationSpec = tween(durationMillis = KIWI_BUTTON_COLOR_DURATION_MS),
        label = "buttonTextColor",
    )

    val containerColor =
        if (enabled) animatedColor else color.copy(alpha = KIWI_DISABLED_ALPHA)

    Box(modifier) {
        Box(
            modifier =
                Modifier
                    .then(
                        horizontalMargin?.let {
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = getResponsiveSizeWidth(it))
                        } ?: Modifier,
                    )
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .clip(RoundedCornerShape(getResponsiveSizeHeight(12.dp)))
                    .background(containerColor)
                    .testTag(testTag)
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput
                        detectTapGestures(
                            onPress = {
                                pressed = true
                                scope.launch {
                                    scale.animateTo(
                                        targetValue = KIWI_BUTTON_PRESSED_SCALE,
                                        animationSpec = spring(stiffness = Spring.StiffnessHigh),
                                    )
                                }
                                val released = tryAwaitRelease()
                                pressed = false
                                if (released) {
                                    AudioManager.playSFX(context, sound)
                                    scale.animateTo(
                                        targetValue = 1f,
                                        animationSpec =
                                            spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium,
                                            ),
                                    )
                                    onClick.invoke()
                                } else {
                                    scope.launch {
                                        scale.animateTo(
                                            targetValue = 1f,
                                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                        )
                                    }
                                }
                            },
                        )
                    }
                    .padding(
                        horizontal = getResponsiveSizeWidth(contentPaddingHorizontal),
                        vertical = getResponsiveSizeWidth(contentPaddingVertical),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint =
                            if (enabled) {
                                animatedTextColor
                            } else {
                                textArguments.color.copy(alpha = KIWI_DISABLED_ALPHA)
                            },
                        modifier = Modifier.height(getResponsiveSizeHeight(iconSize)),
                    )
                    Kiwi_Spacer_Horizontal(iconSpacer)
                }

                val actualTextArguments =
                    if (enabled) {
                        textArguments.copy(color = animatedTextColor)
                    } else {
                        textArguments.copy(color = textArguments.color.copy(alpha = KIWI_DISABLED_ALPHA))
                    }
                Kiwi_Label1(actualTextArguments)
            }
        }
    }
}

@Suppress("LongParameterList")
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
    sound: Int = R.raw.snd_ui_button,
    iconRes: Int? = null,
    iconSize: Dp = 10.dp,
    iconSpacer: Dp = Spacing.small,
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
        sound,
        iconRes,
        iconSize,
        iconSpacer,
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
    sound: Int = R.raw.snd_ui_button,
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
        sound,
    )
}

@Composable
fun Kiwi_HoldButton(
    modifier: Modifier = Modifier,
    holdDurationMillis: Long = 2000,
    horizontalMargin: Dp = 8.dp,
    contentPaddingVertical: Dp = 8.dp,
    textArguments: KiwiTextArguments,
    onHoldComplete: () -> Unit,
    enabled: Boolean = true,
    color: Color,
    fillColor: Color,
    testTag: String = "",
    sound: Int = R.raw.snd_ui_button,
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
        horizontalMargin = horizontalMargin,
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
    horizontalMargin: Dp,
    contentPaddingVertical: Dp,
    testTag: String,
) {
    Box(
        modifier =
            modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = getResponsiveSizeWidth(horizontalMargin))
                .clip(RoundedCornerShape(getResponsiveSizeHeight(10.dp)))
                .background(if (isEnabled) color else color.copy(alpha = KIWI_DISABLED_ALPHA))
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
                    .fillMaxWidth()
                    .padding(
                        vertical = getResponsiveSizeHeight(contentPaddingVertical),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            val actualTextArgs =
                if (isEnabled) {
                    textArguments
                } else {
                    textArguments.copy(color = textArguments.color.copy(alpha = KIWI_DISABLED_ALPHA))
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
@Suppress("MagicNumber")
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
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5A,
                onClick = {},
            )

            Kiwi_Spacer()

            Kiwi_FixedSizeButton(
                textArguments =
                    KiwiTextArguments(
                        "BUTTON",
                        color = kiwiColors.colorF,
                        fontWeight = FontWeight.Bold,
                    ),
                color = kiwiColors.color5,
                onClick = {},
                enabled = false,
            )
            Kiwi_Spacer()
            Row {
                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "BUTTON",
                            color = kiwiColors.colorF,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = kiwiColors.color5,
                    onClick = {},
                    modifier = Modifier.weight(0.5f),
                )

                Kiwi_Spacer()

                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "BUTTON",
                            color = kiwiColors.colorF,
                            fontWeight = FontWeight.Bold,
                        ),
                    color = kiwiColors.color5,
                    onClick = {},
                    modifier = Modifier.weight(0.5f),
                    enabled = false,
                    iconRes = R.drawable.ic_eye_open,
                    iconSize = 15.dp,
                )
            }
        }
    }
}
