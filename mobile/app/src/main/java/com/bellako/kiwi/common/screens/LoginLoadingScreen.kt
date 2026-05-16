package com.bellako.kiwi.common.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.features.conversations.components.CharacterName
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlin.math.roundToInt

const val LOGIN_LOADING_ANIM_DURATION_MS = 600

private const val SPRITE_HEIGHT_DP = 400
private const val SPRITE_OFFSET_X_DP = -50
private const val SPRITE_OFFSET_Y_DP = 100
private const val DIALOGUE_GRADIENT_START_STOP = -0.2f
private const val DIALOGUE_GRADIENT_MID_STOP = 0.5f
private const val DIALOGUE_GRADIENT_END_STOP = 1f
private const val PERCENT_MULTIPLIER = 100
private const val PROGRESS_TRACK_ALPHA = 0.25f
private const val PROGRESS_BG_ALPHA = 0.7f

@Composable
fun LoginLoadingScreen(
    visible: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter =
            slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
            ) +
                fadeIn(
                    animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
                ),
        exit =
            slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
            ) +
                fadeOut(
                    animationSpec = tween(durationMillis = LOGIN_LOADING_ANIM_DURATION_MS, easing = EaseInOut),
                ),
    ) {
        LoginLoadingContent(progress = progress)
    }
}

@Composable
private fun LoginLoadingContent(progress: Float) {
    val kiwiColors = LocalKiwiColors.current

    val percent = (progress.coerceIn(0f, 1f) * PERCENT_MULTIPLIER).roundToInt()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black),
    ) {
        Kiwi_Image(
            R.drawable.background_mindveil,
            "Loading background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
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
                )
            }

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

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(getResponsiveSizeHeight(Spacing.medium))
                    .background(
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

@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun LoginLoadingScreen_Preview() {
    Kiwi_Theme {
        LoginLoadingContent(progress = 0.4f)
    }
}
