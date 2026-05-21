package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

private const val BLINK_DIM_ALPHA = 0.35f
private const val BLINK_FADE_MS = 220
private const val BLINK_CYCLES = 2

private const val TURN_PULSE_DIM_ALPHA = 0.4f
private const val TURN_PULSE_HALF_CYCLE_MS = 650

/** Shared blink alpha animator used by combat log entries and the turn indicator. */
@Composable
fun rememberBlinkAlpha(): Animatable<Float, *> = remember { Animatable(1f) }

/**
 * A continuous, gentle alpha pulse used to keep attention on the turn indicator
 * while it's the player's turn to act. Returns full opacity (1f) when [active]
 * is false, so callers can multiply it in unconditionally.
 */
@Composable
fun rememberTurnPulseAlpha(active: Boolean): Float {
    val transition = rememberInfiniteTransition(label = "combat_turn_pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = TURN_PULSE_DIM_ALPHA,
        animationSpec =
            infiniteRepeatable(
                animation = tween(TURN_PULSE_HALF_CYCLE_MS),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "combat_turn_pulse_alpha",
    )
    return if (active) pulse else 1f
}

/** Plays the standard combat blink: snap to full, then dim/restore [BLINK_CYCLES] times. */
suspend fun Animatable<Float, *>.blink() {
    snapTo(1f)
    repeat(BLINK_CYCLES) {
        animateTo(BLINK_DIM_ALPHA, tween(BLINK_FADE_MS))
        animateTo(1f, tween(BLINK_FADE_MS))
    }
}

/** Plays a horizontal shake: oscillate between +/- [amplitude] for [cycles] iterations, then return to 0. */
suspend fun Animatable<Float, *>.shake(
    cycles: Int,
    amplitude: Float,
    stepMs: Int,
) {
    repeat(cycles) {
        animateTo(amplitude, tween(stepMs))
        animateTo(-amplitude, tween(stepMs))
    }
    animateTo(0f, tween(stepMs))
}
