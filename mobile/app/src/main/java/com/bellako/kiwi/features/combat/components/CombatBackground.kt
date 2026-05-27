package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.ui.LocalKiwiColors

private const val BACKGROUND_SATURATION = 0.45f
// The image is laid out larger than the screen so the damage shake can move it
// around without ever exposing the backdrop behind it.
private const val BACKGROUND_OVERSCAN_SCALE = 2f
private const val EDGE_FADE_TOP_ALPHA = 0.75f
private const val EDGE_FADE_BOTTOM_ALPHA = 0.95f
private const val EDGE_FADE_TOP_END = 0.18f
private const val EDGE_FADE_BOTTOM_START = 0.55f

@Composable
internal fun CombatBackground(
    background: String?,
    alpha: Float = 1f,
    shakeOffsetX: () -> Float = { 0f },
    blurRadiusDp: () -> Float = { 0f },
) {
    val colors = LocalKiwiColors.current
    val resId = AssetResolver.drawable(LocalContext.current, background) ?: return
    BoxWithConstraints(
        // A fixed, screen-sized viewport. The oversized image is clipped to it,
        // so the shake slides the image within the margin instead of exposing
        // the backdrop at the edges.
        modifier = Modifier.fillMaxSize().clipToBounds(),
    ) {
        Kiwi_Image(
            painterResourceId = resId,
            alt = "Combat background",
            modifier =
                Modifier
                    // requiredSize, not size — size would be coerced back to the
                    // viewport's constraints, leaving the image only screen-sized.
                    .requiredSize(
                        maxWidth * BACKGROUND_OVERSCAN_SCALE,
                        maxHeight * BACKGROUND_OVERSCAN_SCALE,
                    ).align(Alignment.Center)
                    // Read inside graphicsLayer so the shake and focus blur
                    // run on the draw phase without recomposing.
                    .graphicsLayer {
                        translationX = shakeOffsetX()
                        renderEffect = blurRenderEffectOrNull(blurRadiusDp().dp.toPx())
                    }
                    .alpha(alpha),
            contentScale = ContentScale.Crop,
            colorFilter =
                ColorFilter.colorMatrix(
                    ColorMatrix().apply { setToSaturation(BACKGROUND_SATURATION) },
                ),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .alpha(alpha)
                    .background(
                        Brush.verticalGradient(
                            0f to colors.color2.copy(alpha = EDGE_FADE_TOP_ALPHA),
                            EDGE_FADE_TOP_END to Color.Transparent,
                            EDGE_FADE_BOTTOM_START to Color.Transparent,
                            1f to colors.color2.copy(alpha = EDGE_FADE_BOTTOM_ALPHA),
                        ),
                    ),
        )
    }
}
