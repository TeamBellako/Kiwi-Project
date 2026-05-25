package com.bellako.kiwi.common.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.rememberFloatingModifier
import com.bellako.kiwi.features.conversations.components.CharacterName
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

const val LOGIN_LOADING_ANIM_DURATION_MS = 600

private const val ELEMENT_ENTER_MS = 450
private const val HOLD_BEFORE_EXIT_MS = 1500L
private const val SPRITE_RISE_DIVISOR = 4

private const val SPRITE_HEIGHT_DP = 400
private const val SPRITE_OFFSET_X_DP = -50
private const val SPRITE_OFFSET_Y_DP = 100
private const val DIALOGUE_GRADIENT_START_STOP = -0.2f
private const val DIALOGUE_GRADIENT_MID_STOP = 0.5f
private const val DIALOGUE_GRADIENT_END_STOP = 1f
private const val PERCENT_MULTIPLIER = 100
private const val PROGRESS_TRACK_ALPHA = 0.25f
private const val PROGRESS_BG_ALPHA = 0.7f

/** Current animation state, snapshotted from the driving [Animatable]s each frame. */
private data class LoadingAnim(
    val enterOffset: Float,
    val exitOffset: Float,
    val spriteVisible: Boolean,
    val dialogueVisible: Boolean,
)

private fun enterFrom(offsetY: (Int) -> Int): EnterTransition =
    slideInVertically(initialOffsetY = offsetY, animationSpec = tween(ELEMENT_ENTER_MS, easing = EaseInOut)) +
        fadeIn(animationSpec = tween(ELEMENT_ENTER_MS, easing = EaseInOut))

@Composable
fun LoginLoadingScreen(
    visible: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
    onExitComplete: () -> Unit = {},
) {
    var present by remember { mutableStateOf(false) }
    var dismissRequested by remember { mutableStateOf(false) }
    var entranceComplete by remember { mutableStateOf(false) }
    var spriteVisible by remember { mutableStateOf(false) }
    var dialogueVisible by remember { mutableStateOf(false) }

    // Single shared offset (1 = one screen below, 0 = in place) that the background
    // and the progress bar BOTH read, so they move as one linked unit on entry.
    val enterOffset = remember { Animatable(1f) }
    // Single container offset (0 = in place, 1 = one screen below) that slides the
    // entire screen — every element together — down on exit.
    val exitOffset = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            present = true
            dismissRequested = false
        } else {
            dismissRequested = true
        }
    }

    // Keyed on `present` so a fast loading (visible flipping to false early)
    // cannot cancel the entrance — it always plays in full.
    LaunchedEffect(present) {
        if (!present) return@LaunchedEffect
        entranceComplete = false
        spriteVisible = false
        dialogueVisible = false
        enterOffset.snapTo(1f)
        exitOffset.snapTo(0f)
        enterOffset.animateTo(0f, tween(LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut))
        spriteVisible = true
        delay(ELEMENT_ENTER_MS.toLong())
        dialogueVisible = true
        delay(ELEMENT_ENTER_MS.toLong())
        entranceComplete = true
    }

    // Exit only starts once the entrance has fully played AND a dismiss was requested.
    // Hold the fully-assembled screen briefly so it can be absorbed before sliding out.
    LaunchedEffect(entranceComplete, dismissRequested) {
        if (!entranceComplete || !dismissRequested) return@LaunchedEffect
        delay(HOLD_BEFORE_EXIT_MS)
        exitOffset.animateTo(1f, tween(LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut))
        entranceComplete = false
        present = false
        onExitComplete()
    }

    if (!present) return

    LoginLoadingContent(
        progress = progress,
        anim =
            LoadingAnim(
                enterOffset = enterOffset.value,
                exitOffset = exitOffset.value,
                spriteVisible = spriteVisible,
                dialogueVisible = dialogueVisible,
            ),
        modifier = modifier,
    )
}

@Composable
@Suppress("LongMethod")
private fun LoginLoadingContent(
    progress: Float,
    anim: LoadingAnim,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    val percent = (progress.coerceIn(0f, 1f) * PERCENT_MULTIPLIER).roundToInt()

    // Liria floats here too — she's an airborne entity wherever she appears.
    val floatingModifier = rememberFloatingModifier()

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        val travelPx = constraints.maxHeight.toFloat()

        // The whole screen rides this one layer, so every element slides down
        // together as a single linked unit on exit.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = anim.exitOffset * travelPx
                        alpha = 1f - anim.exitOffset
                    },
        ) {
            // Background and the progress bar share `enterOffset`, so they move
            // by the exact same pixels at the same time when entering.
            val linkedEntry =
                Modifier.graphicsLayer {
                    translationY = anim.enterOffset * travelPx
                    alpha = 1f - anim.enterOffset
                }

            Kiwi_Image(
                R.drawable.background_mindveil,
                "Loading background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().then(linkedEntry),
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
            ) {
                AnimatedVisibility(
                    visible = anim.spriteVisible,
                    enter = enterFrom { fullHeight -> fullHeight / SPRITE_RISE_DIVISOR },
                    exit = ExitTransition.None,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .height(getResponsiveSizeHeight(SPRITE_HEIGHT_DP.dp))
                                .fillMaxWidth()
                                .offset(
                                    x = getResponsiveSizeWidth(SPRITE_OFFSET_X_DP.dp),
                                    y = getResponsiveSizeHeight(SPRITE_OFFSET_Y_DP.dp),
                                ),
                    ) {
                        Kiwi_Image(
                            R.drawable.character_liria_base,
                            "Liria",
                            modifier = floatingModifier,
                        )
                    }
                }

                AnimatedVisibility(
                    visible = anim.dialogueVisible,
                    enter = enterFrom { fullHeight -> fullHeight },
                    exit = ExitTransition.None,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        DIALOGUE_GRADIENT_START_STOP to Color.Transparent,
                                        DIALOGUE_GRADIENT_MID_STOP to kiwiColors.color2,
                                        DIALOGUE_GRADIENT_END_STOP to kiwiColors.color2,
                                    ),
                                ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                                Modifier.padding(
                                    horizontal = Spacing.medium,
                                    vertical = getResponsiveSizeHeight(Spacing.large),
                                ),
                        ) {
                            Kiwi_Image(
                                R.drawable.dialogue_light_medium,
                                "Dialogue frame",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Kiwi_P2(
                                KiwiTextArguments(
                                    "We are loading your adventure, please stay put.",
                                    textAlign = TextAlign.Center,
                                    color = kiwiColors.color3,
                                    modifier = Modifier.padding(Spacing.medium, Spacing.medium),
                                ),
                            )
                            Box(
                                modifier =
                                    Modifier
                                        .matchParentSize()
                                        .offset(x = getResponsiveSizeWidth(25.dp)),
                            ) {
                                CharacterName("Liria", dark = false, small = false)
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .then(linkedEntry)
                        .padding(
                            start = getResponsiveSizeHeight(Spacing.medium),
                            end = getResponsiveSizeHeight(Spacing.medium),
                            top = getResponsiveSizeHeight(Spacing.xLarge),
                            bottom = getResponsiveSizeHeight(Spacing.large),
                        ).background(
                            color = kiwiColors.color2.copy(alpha = PROGRESS_BG_ALPHA),
                            shape = RoundedCornerShape(getResponsiveSizeHeight(Spacing.small)),
                        ).padding(
                            horizontal = getResponsiveSizeHeight(Spacing.medium),
                            vertical = getResponsiveSizeHeight(Spacing.small),
                        ),
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.weight(1f),
                    color = kiwiColors.color6,
                    trackColor = kiwiColors.color6.copy(alpha = PROGRESS_TRACK_ALPHA),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Kiwi_P2(
                    KiwiTextArguments(
                        "$percent%",
                        textAlign = TextAlign.Center,
                        color = kiwiColors.color6,
                        modifier = Modifier.padding(start = Spacing.medium),
                    ),
                )
            }
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun LoginLoadingScreen_Preview() {
    Kiwi_Theme {
        LoginLoadingContent(
            progress = 0.4f,
            anim =
                LoadingAnim(
                    enterOffset = 0f,
                    exitOffset = 0f,
                    spriteVisible = true,
                    dialogueVisible = true,
                ),
        )
    }
}
