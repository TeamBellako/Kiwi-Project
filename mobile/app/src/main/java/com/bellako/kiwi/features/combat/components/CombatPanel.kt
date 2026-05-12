package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private val DEFAULT_PANEL_RADIUS = 12.dp
private val DEFAULT_BORDER_WIDTH = 1.dp
private const val INNER_GLOW_FADE_START = 0.3f
private const val INNER_GLOW_FADE_END = 0.7f

/**
 * Applies the standard combat panel look: rounded background plus matching border.
 * Used by the combat log, turn indicator, status popup, and timer.
 *
 * When [innerGlowColor] is non-null, an inner vertical glow is painted between the
 * background and the border — strongest at the top/bottom edges and fading toward
 * the center.
 */
@Composable
fun Modifier.combatPanel(
    bgColor: Color,
    borderColor: Color,
    radius: Dp = DEFAULT_PANEL_RADIUS,
    borderWidth: Dp = DEFAULT_BORDER_WIDTH,
    innerGlowColor: Color? = null,
): Modifier {
    val shape = RoundedCornerShape(getResponsiveSizeHeight(radius))
    var result = this.background(color = bgColor, shape = shape)
    if (innerGlowColor != null) {
        val glowBrush =
            Brush.verticalGradient(
                0f to innerGlowColor,
                INNER_GLOW_FADE_START to Color.Transparent,
                INNER_GLOW_FADE_END to Color.Transparent,
                1f to innerGlowColor,
            )
        result = result.background(brush = glowBrush, shape = shape)
    }
    return result.border(width = getResponsiveSizeHeight(borderWidth), color = borderColor, shape = shape)
}
