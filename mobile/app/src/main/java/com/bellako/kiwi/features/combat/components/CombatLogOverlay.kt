package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private const val OVERLAY_FADE_MS = 220
private const val LOG_SLIDE_MS = 300
private const val LOG_HEIGHT_FRACTION = 0.7f
private const val LOG_SLIDE_DIVISOR = 2

/**
 * The combat log surfaced on top of a combat screen: a full-screen dim plus
 * the log panel, with a coordinated open/close animation (the dim fades, the
 * panel slides up and fades).
 *
 * Rendered at the root of each combat screen so the dim covers the whole
 * screen — the navbar lives outside this content area, so it stays clear.
 */
@Composable
fun CombatLogOverlay(
    isOpen: Boolean,
    entries: List<CombatLogEntry>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(tween(OVERLAY_FADE_MS)),
            exit = fadeOut(tween(OVERLAY_FADE_MS)),
            modifier = Modifier.fillMaxSize(),
        ) {
            LogDimOverlay(
                modifier = Modifier.fillMaxSize(),
                onDismiss = onDismiss,
            )
        }

        AnimatedVisibility(
            visible = isOpen,
            enter =
                slideInVertically(tween(LOG_SLIDE_MS)) { it / LOG_SLIDE_DIVISOR } +
                    fadeIn(tween(LOG_SLIDE_MS)),
            exit =
                slideOutVertically(tween(LOG_SLIDE_MS)) { it / LOG_SLIDE_DIVISOR } +
                    fadeOut(tween(LOG_SLIDE_MS)),
            modifier = Modifier.align(Alignment.Center),
        ) {
            CombatLog(
                entries = entries,
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeWidth(Spacing.medium))
                        .fillMaxHeight(LOG_HEIGHT_FRACTION)
                        // Swallow taps on the panel so they don't dismiss it.
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
            )
        }
    }
}
