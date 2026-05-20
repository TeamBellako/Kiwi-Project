package com.bellako.kiwi.features.nodes.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

private const val WHITE_FADE_IN_MS = 800
private const val WHITE_FADE_OUT_MS = 500
private const val WHITE_HOLD_MS = 450L

/**
 * Plays a fullscreen white-veil transition between leaving a node and starting
 * the next sequence (conversation, combat, future features). The veil fades in
 * slowly, sits at full white for a beat, and is then lifted by the caller once
 * the follow-up sequence is mounted.
 *
 * Decoupled by design — the controller knows nothing about what comes after
 * the whiteout. Wire it once at the app shell and any node-bearing screen can
 * trigger a transition without coupling to specific follow-up sequences.
 */
@Stable
class NodeEntryTransitionController internal constructor(
    private val whiteAlphaAnim: Animatable<Float, AnimationVector1D>,
) {
    /** Alpha of the fullscreen white veil. 0f when idle. */
    val whiteAlpha: Float get() = whiteAlphaAnim.value

    /**
     * Fades the white veil in and holds it at full white briefly so the
     * transition reads as a beat rather than a flicker. Returns once the hold
     * is done — at that point the caller should mount the follow-up sequence
     * and call [fadeOut] to lift the veil.
     */
    suspend fun enter() {
        whiteAlphaAnim.snapTo(0f)
        whiteAlphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(WHITE_FADE_IN_MS, easing = LinearEasing),
        )
        delay(WHITE_HOLD_MS)
    }

    /**
     * Fades the white veil back out. Idempotent — calling it while the veil
     * is already gone is a no-op, so a follow-up screen can dismiss the veil
     * on its own timing without racing the caller's fallback dismiss.
     */
    suspend fun fadeOut() {
        if (whiteAlphaAnim.value <= 0f) return
        whiteAlphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(WHITE_FADE_OUT_MS, easing = LinearEasing),
        )
    }
}

@Composable
fun rememberNodeEntryTransitionController(): NodeEntryTransitionController {
    val alphaAnim = remember { Animatable(0f) }
    return remember { NodeEntryTransitionController(alphaAnim) }
}

/**
 * Provides the active [NodeEntryTransitionController] to descendants. Anything
 * inside the provider can call [LocalNodeEntryTransition].current to trigger
 * the transition.
 */
val LocalNodeEntryTransition =
    compositionLocalOf<NodeEntryTransitionController?> { null }

/**
 * Fullscreen white veil driven by [controller]. Renders nothing while idle so
 * it doesn't intercept input; once visible it swallows clicks so the user
 * can't tap through the transition.
 */
@Composable
fun NodeEntryWhiteoutOverlay(
    controller: NodeEntryTransitionController,
    modifier: Modifier = Modifier,
) {
    val alpha = controller.whiteAlpha
    if (alpha <= 0f) return
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(Color.White)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {},
                ),
    )
}
