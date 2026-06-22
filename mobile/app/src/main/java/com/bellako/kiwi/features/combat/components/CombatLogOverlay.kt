package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlin.math.roundToInt

private const val LOG_MORPH_MS = 320
private const val LOG_HEIGHT_FRACTION = 0.7f
// The collapsed control has fully handed off to the morphing panel by this
// point of the open progress; the log text only starts fading in afterwards,
// once the container has the room to show it.
private const val CONTROL_FADE_END = 0.30f
private const val LOG_TEXT_FADE_START = 0.45f

/** Smooth 0..1 ramp of [progress] across the [start, end] window. */
private fun ramp(
    progress: Float,
    start: Float,
    end: Float,
): Float = ((progress - start) / (end - start)).coerceIn(0f, 1f)

/**
 * Open progress for the combat log: 0 = collapsed into its control (turn
 * indicator / log button), 1 = fully expanded to the centred panel. The same
 * value drives the morphing overlay and the fade-out of the control it grew
 * from, so they stay in lockstep.
 */
@Composable
fun rememberCombatLogProgress(isOpen: Boolean): Float {
    val progress by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(LOG_MORPH_MS, easing = EaseInOut),
        label = "combat_log_morph",
    )
    return progress
}

/**
 * Alpha for the collapsed control as the log grows out of it: fully opaque
 * while closed, gone once the morphing panel has taken over its footprint.
 * The morphing panel is seeded at the control's exact bounds, so the hand-off
 * reads as the control itself expanding rather than a second panel appearing.
 */
fun combatLogControlAlpha(progress: Float): Float = 1f - ramp(progress, 0f, CONTROL_FADE_END)

/**
 * The combat log surfaced on top of a combat screen. Rather than a separate
 * panel sliding in, the log *is* the control that opened it: a copy of the log
 * panel is seeded at [sourceBounds] (the turn indicator in combat, the log
 * button on the result screens) and morphed — position, size and content — to
 * a centred panel as [progress] runs 0 → 1, with the dim fading in behind it.
 * Closing simply runs [progress] back to 0, reversing the morph.
 *
 * Rendered at the root of each combat screen so the dim covers the whole
 * screen — the navbar lives outside this content area, so it stays clear.
 */
@Composable
fun CombatLogOverlay(
    progress: Float,
    entries: List<CombatLogEntry>,
    sourceBounds: Rect?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Nothing to draw while fully closed (and we can't place the panel until we
    // know where its control sits).
    if (progress <= 0f || sourceBounds == null) return

    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // The overlay's own offset in root coordinates, so source bounds (also
        // captured in root coordinates) can be expressed relative to it.
        var overlayOrigin by remember { mutableStateOf(Offset.Zero) }

        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val horizontalPadPx = with(density) { getResponsiveSizeWidth(Spacing.medium).toPx() }

        // Destination: the centred log panel, full width minus the side gutters
        // and a fixed fraction of the screen height.
        val targetWidth = maxWidthPx - 2f * horizontalPadPx
        val targetHeight = maxHeightPx * LOG_HEIGHT_FRACTION
        val targetLeft = horizontalPadPx
        val targetTop = (maxHeightPx - targetHeight) / 2f

        // Origin: the control's bounds, mapped into this overlay's space.
        val sourceLeft = sourceBounds.left - overlayOrigin.x
        val sourceTop = sourceBounds.top - overlayOrigin.y

        val currentLeft = lerp(sourceLeft, targetLeft, progress)
        val currentTop = lerp(sourceTop, targetTop, progress)
        val currentWidth = lerp(sourceBounds.width, targetWidth, progress)
        val currentHeight = lerp(sourceBounds.height, targetHeight, progress)

        val textAlpha = ramp(progress, LOG_TEXT_FADE_START, 1f)

        // Dim fades in/out with the morph. Tapping it dismisses the log. This
        // box is static and fills the overlay, so it also reports the overlay's
        // root offset used to place the morphing panel.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { overlayOrigin = it.boundsInRoot().topLeft }
                    .alpha(progress),
        ) {
            LogDimOverlay(
                modifier = Modifier.fillMaxSize(),
                onDismiss = onDismiss,
            )
        }

        Box(
            modifier =
                Modifier
                    .offset { IntOffset(currentLeft.roundToInt(), currentTop.roundToInt()) }
                    .size(
                        width = with(density) { currentWidth.toDp() },
                        height = with(density) { currentHeight.toDp() },
                    )
                    // Swallow taps on the panel so they don't fall through to the dim.
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                    ),
        ) {
            CombatLog(
                entries = entries,
                modifier = Modifier.fillMaxSize(),
                contentAlpha = textAlpha,
            )
        }
    }
}
