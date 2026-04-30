package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private val DEFAULT_PANEL_RADIUS = 12.dp
private val DEFAULT_BORDER_WIDTH = 1.dp

/**
 * Applies the standard combat panel look: rounded background plus matching border.
 * Used by the combat log, turn indicator, status popup, and timer.
 */
@Composable
fun Modifier.combatPanel(
    bgColor: Color,
    borderColor: Color,
    radius: Dp = DEFAULT_PANEL_RADIUS,
    borderWidth: Dp = DEFAULT_BORDER_WIDTH,
): Modifier {
    val shape = RoundedCornerShape(getResponsiveSizeHeight(radius))
    return this
        .background(color = bgColor, shape = shape)
        .border(width = getResponsiveSizeHeight(borderWidth), color = borderColor, shape = shape)
}
