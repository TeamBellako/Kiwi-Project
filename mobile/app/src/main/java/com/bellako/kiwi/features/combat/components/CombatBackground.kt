package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.ui.LocalKiwiColors

private const val BACKGROUND_SATURATION = 0.45f
private const val EDGE_FADE_TOP_ALPHA = 0.75f
private const val EDGE_FADE_BOTTOM_ALPHA = 0.95f
private const val EDGE_FADE_TOP_END = 0.18f
private const val EDGE_FADE_BOTTOM_START = 0.55f

@Composable
internal fun CombatBackground(background: String?) {
    val colors = LocalKiwiColors.current
    val resId = AssetResolver.drawable(LocalContext.current, background) ?: return
    Kiwi_Image(
        painterResourceId = resId,
        alt = "Combat background",
        modifier = Modifier.fillMaxSize(),
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
