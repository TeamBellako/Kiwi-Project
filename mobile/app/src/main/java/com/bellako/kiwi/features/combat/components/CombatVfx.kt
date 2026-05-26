package com.bellako.kiwi.features.combat.components

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.hypot
import kotlin.random.Random

private const val PLAYER_SHAKE_CYCLES = 4
private const val PLAYER_SHAKE_AMPLITUDE_PX = 28f
private const val PLAYER_SHAKE_STEP_MS = 50
private const val PLAYER_FLASH_CYCLES = 2
private const val PLAYER_FLASH_STEP_MS = 90L
private const val PLAYER_FLASH_ALPHA = 0.15f
private const val PLAYER_VIGNETTE_PEAK_ALPHA = 0.3f
private const val PLAYER_VIGNETTE_RISE_MS = 120
private const val PLAYER_VIGNETTE_FALL_MS = 500
// The death sequence is driven by a 0..1 "eyelids closed" progress: a few
// flutters, then a slow full close. 0 = eyes open, 1 = eyes fully shut (black).
// Total run time gates the defeat-screen transition — see DEFEAT_TRANSITION_DELAY_MS.
private const val DEATH_PAUSE_MS = 500L
private const val DEATH_BLINK_CYCLES = 3
private const val DEATH_BLINK_RISE_MS = 260
private const val DEATH_BLINK_FALL_MS = 320
// How far the eyelids drop on each pre-close flutter, and how far they reopen between flutters.
private const val DEATH_BLINK_PEAK = 0.5f
private const val DEATH_BLINK_TROUGH = 0.08f
private const val DEATH_CLOSE_MS = 700

// Shape of the clear "eye" opening: a wide horizontal ellipse, so its height is
// a small fraction of its width.
private const val DEATH_VIGNETTE_EYE_ASPECT = 0.4f
// Fraction of the opening's radius spent fading from clear to solid black.
private const val DEATH_VIGNETTE_FEATHER = 0.55f
// Headroom so the screen is still fully clear when the close has only just begun.
private const val DEATH_VIGNETTE_OPEN_HEADROOM = 1.08f
// Slight overdraw so the squashed cover rect leaves no hairline gap at the edges.
private const val DEATH_VIGNETTE_COVER_OVERDRAW = 1.05f

// Focus blur — ambient pulses one surface out of focus at a time; an override
// flag (e.g. a bark) cinematic-focuses one surface and blurs the other.
// The background sits at a small standing blur even at rest, so when focus
// shifts the change reads more clearly than coming from 0.
private const val FOCUS_BLUR_ENEMY_REST_DP = 0f
private const val FOCUS_BLUR_BACKGROUND_REST_DP = 1.5f
private const val FOCUS_BLUR_PEAK_MIN_DP = 2.5f
private const val FOCUS_BLUR_PEAK_MAX_DP = 4f
private const val FOCUS_BLUR_RAMP_IN_MIN_MS = 800
private const val FOCUS_BLUR_RAMP_IN_MAX_MS = 1500
private const val FOCUS_BLUR_HOLD_MIN_MS = 200L
private const val FOCUS_BLUR_HOLD_MAX_MS = 500L
private const val FOCUS_BLUR_RAMP_OUT_MIN_MS = 900
private const val FOCUS_BLUR_RAMP_OUT_MAX_MS = 1700
private const val FOCUS_BLUR_IDLE_MIN_MS = 800L
private const val FOCUS_BLUR_IDLE_MAX_MS = 2500L
private const val FOCUS_BLUR_OVERRIDE_RAMP_MS = 350
private const val FOCUS_BLUR_OVERRIDE_RELEASE_MS = 600
private const val FOCUS_BLUR_REQUEST_DEFAULT_HOLD_MS = 1200L
// Below this px radius the blur pipeline is skipped entirely (renderEffect = null).
private const val FOCUS_BLUR_PIPELINE_THRESHOLD_PX = 0.5f

internal class PlayerDamageVfx(
    val shakeOffsetX: Animatable<Float, *>,
    val flashAlpha: Animatable<Float, *>,
    val vignetteAlpha: Animatable<Float, *>,
)

@Composable
internal fun rememberPlayerDamageVfx(
    currentHp: Int,
    key: Any,
): PlayerDamageVfx {
    val shakeOffsetX = remember(key) { Animatable(0f) }
    val flashAlpha = remember(key) { Animatable(0f) }
    val vignetteAlpha = remember(key) { Animatable(0f) }
    var previousHp by remember(key) { mutableIntStateOf(currentHp) }
    var trigger by remember(key) { mutableIntStateOf(0) }

    LaunchedEffect(currentHp) {
        if (currentHp < previousHp) trigger++
        previousHp = currentHp
    }

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        coroutineScope {
            launch {
                shakeOffsetX.shake(PLAYER_SHAKE_CYCLES, PLAYER_SHAKE_AMPLITUDE_PX, PLAYER_SHAKE_STEP_MS)
            }
            launch {
                repeat(PLAYER_FLASH_CYCLES) {
                    flashAlpha.snapTo(PLAYER_FLASH_ALPHA)
                    delay(PLAYER_FLASH_STEP_MS)
                    flashAlpha.snapTo(0f)
                    delay(PLAYER_FLASH_STEP_MS)
                }
            }
            launch {
                vignetteAlpha.animateTo(PLAYER_VIGNETTE_PEAK_ALPHA, tween(PLAYER_VIGNETTE_RISE_MS))
                vignetteAlpha.animateTo(0f, tween(PLAYER_VIGNETTE_FALL_MS))
            }
        }
    }
    return PlayerDamageVfx(shakeOffsetX, flashAlpha, vignetteAlpha)
}

@Composable
internal fun rememberDeathSequenceVfx(
    isPlayerDefeated: Boolean,
    key: Any,
): Animatable<Float, *> {
    val closeProgress = remember(key) { Animatable(0f) }
    LaunchedEffect(isPlayerDefeated, key) {
        if (!isPlayerDefeated) {
            closeProgress.snapTo(0f)
            return@LaunchedEffect
        }
        delay(DEATH_PAUSE_MS)
        // A few eyelid flutters, then the eyes shut for good.
        repeat(DEATH_BLINK_CYCLES) {
            closeProgress.animateTo(DEATH_BLINK_PEAK, tween(DEATH_BLINK_RISE_MS))
            closeProgress.animateTo(DEATH_BLINK_TROUGH, tween(DEATH_BLINK_FALL_MS))
        }
        closeProgress.animateTo(1f, tween(DEATH_CLOSE_MS))
    }
    return closeProgress
}

@Composable
internal fun PlayerDamageOverlays(vfx: PlayerDamageVfx) {
    if (vfx.flashAlpha.value > 0f) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = vfx.flashAlpha.value)),
        )
    }
    if (vfx.vignetteAlpha.value > 0f) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    Color.Red.copy(alpha = 0f),
                                    Color.Red.copy(alpha = vfx.vignetteAlpha.value),
                                ),
                        ),
                    ),
        )
    }
}

/**
 * Black "closing eyes" vignette for the death sequence. [closeProgress] runs
 * 0 (eyes open, fully clear) to 1 (eyes shut, fully black); the clear opening
 * is a horizontal ellipse that shrinks from outside in as it rises.
 */
@Composable
internal fun DeathSequenceOverlay(closeProgress: Float) {
    val progress = closeProgress.coerceIn(0f, 1f)
    if (progress <= 0f) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Eyes fully shut: a solid black cover, with no sub-pixel opening left.
        if (progress >= 1f) {
            drawRect(Color.Black)
            return@Canvas
        }
        val center = Offset(size.width / 2f, size.height / 2f)
        // Distance to a screen corner in the vertically-squashed space the
        // gradient is drawn in — the opening must clear this to leave the
        // screen fully visible at the start of the close.
        val cornerRadius =
            hypot(size.width / 2f, size.height / 2f / DEATH_VIGNETTE_EYE_ASPECT)
        val fullyOpenRadius =
            cornerRadius / (1f - DEATH_VIGNETTE_FEATHER) * DEATH_VIGNETTE_OPEN_HEADROOM
        val openRadius = (fullyOpenRadius * (1f - progress)).coerceAtLeast(1f)

        val brush =
            Brush.radialGradient(
                colorStops =
                    arrayOf(
                        0f to Color.Transparent,
                        (1f - DEATH_VIGNETTE_FEATHER) to Color.Transparent,
                        1f to Color.Black,
                    ),
                center = center,
                radius = openRadius,
            )
        // Squash the y-axis so the circular gradient reads as a wide,
        // eye-shaped ellipse. The cover rect is scaled up by 1/aspect first so
        // that, once squashed, it still spans the whole screen — leaving solid
        // clamped black everywhere outside the elliptical opening.
        val coverWidth = size.width * DEATH_VIGNETTE_COVER_OVERDRAW
        val coverHeight = size.height / DEATH_VIGNETTE_EYE_ASPECT * DEATH_VIGNETTE_COVER_OVERDRAW
        scale(scaleX = 1f, scaleY = DEATH_VIGNETTE_EYE_ASPECT, pivot = center) {
            drawRect(
                brush = brush,
                topLeft = Offset(center.x - coverWidth / 2f, center.y - coverHeight / 2f),
                size = Size(coverWidth, coverHeight),
            )
        }
    }
}

internal enum class FocusTarget { ENEMY, BACKGROUND }

private fun restDpFor(target: FocusTarget): Float = when (target) {
    FocusTarget.ENEMY -> FOCUS_BLUR_ENEMY_REST_DP
    FocusTarget.BACKGROUND -> FOCUS_BLUR_BACKGROUND_REST_DP
}

/**
 * Returns a [BlurEffect] when blur is supported and the radius is large enough
 * to be worth a separate render pass. Returns null otherwise — most ambient
 * frames have one surface at 0dp, so the blur pipeline is skipped there.
 */
internal fun blurRenderEffectOrNull(radiusPx: Float): RenderEffect? =
    if (radiusPx > FOCUS_BLUR_PIPELINE_THRESHOLD_PX &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    ) {
        BlurEffect(radiusPx, radiusPx)
    } else {
        null
    }

/**
 * Ambient + cinematic focus-blur controller. Ambient pulses one surface out of
 * focus at a time with randomized timing; setting [overrideTarget] (or calling
 * [requestFocus]) snaps the loop into a cinematic focus — the OPPOSITE surface
 * blurs to peak and holds until the override clears.
 */
@Stable
internal class FocusBlurVfx(
    val enemyBlurRadius: Animatable<Float, *>,
    val backgroundBlurRadius: Animatable<Float, *>,
    internal val overrideTarget: MutableState<FocusTarget?>,
) {
    suspend fun requestFocus(
        target: FocusTarget,
        holdMs: Long = FOCUS_BLUR_REQUEST_DEFAULT_HOLD_MS,
    ) {
        overrideTarget.value = target
        try {
            delay(holdMs)
        } finally {
            overrideTarget.value = null
        }
    }
}

@Composable
internal fun rememberFocusBlurVfx(
    key: Any,
    enabled: Boolean,
): FocusBlurVfx {
    val enemyBlurRadius = remember(key) { Animatable(FOCUS_BLUR_ENEMY_REST_DP) }
    val backgroundBlurRadius = remember(key) { Animatable(FOCUS_BLUR_BACKGROUND_REST_DP) }
    val overrideTarget = remember(key) { mutableStateOf<FocusTarget?>(null) }
    val vfx = remember(key) {
        FocusBlurVfx(enemyBlurRadius, backgroundBlurRadius, overrideTarget)
    }

    LaunchedEffect(key, enabled) {
        if (!enabled) {
            enemyBlurRadius.snapTo(FOCUS_BLUR_ENEMY_REST_DP)
            backgroundBlurRadius.snapTo(FOCUS_BLUR_BACKGROUND_REST_DP)
            overrideTarget.value = null
            return@LaunchedEffect
        }
        runFocusBlurLoop(enemyBlurRadius, backgroundBlurRadius, overrideTarget)
    }
    return vfx
}

private suspend fun runFocusBlurLoop(
    enemyBlurRadius: Animatable<Float, *>,
    backgroundBlurRadius: Animatable<Float, *>,
    overrideTarget: MutableState<FocusTarget?>,
) {
    var lastPulsed = FocusTarget.BACKGROUND
    while (true) {
        val override = overrideTarget.value
        if (override != null) {
            runFocusBlurOverride(
                override,
                enemyBlurRadius,
                backgroundBlurRadius,
                overrideTarget,
            )
        } else {
            lastPulsed = runFocusBlurAmbient(
                lastPulsed,
                enemyBlurRadius,
                backgroundBlurRadius,
                overrideTarget,
            )
        }
    }
}

private suspend fun runFocusBlurOverride(
    target: FocusTarget,
    enemyBlurRadius: Animatable<Float, *>,
    backgroundBlurRadius: Animatable<Float, *>,
    overrideTarget: MutableState<FocusTarget?>,
) {
    // Focusing on a surface blurs the OPPOSITE surface.
    val blurSurface = if (target == FocusTarget.ENEMY) FocusTarget.BACKGROUND else FocusTarget.ENEMY
    val toBlur = if (blurSurface == FocusTarget.ENEMY) enemyBlurRadius else backgroundBlurRadius
    val toClear = if (target == FocusTarget.ENEMY) enemyBlurRadius else backgroundBlurRadius
    val blurSurfaceRest = restDpFor(blurSurface)
    val focusedSurfaceRest = restDpFor(target)
    coroutineScope {
        launch {
            toClear.animateTo(
                focusedSurfaceRest,
                tween(FOCUS_BLUR_OVERRIDE_RAMP_MS, easing = EaseInOut),
            )
        }
        launch {
            toBlur.animateTo(
                FOCUS_BLUR_PEAK_MAX_DP,
                tween(FOCUS_BLUR_OVERRIDE_RAMP_MS, easing = EaseInOut),
            )
        }
    }
    snapshotFlow { overrideTarget.value }.first { it == null }
    toBlur.animateTo(
        blurSurfaceRest,
        tween(FOCUS_BLUR_OVERRIDE_RELEASE_MS, easing = EaseInOut),
    )
}

private suspend fun runFocusBlurAmbient(
    lastPulsed: FocusTarget,
    enemyBlurRadius: Animatable<Float, *>,
    backgroundBlurRadius: Animatable<Float, *>,
    overrideTarget: MutableState<FocusTarget?>,
): FocusTarget {
    val idleMs = Random.nextLong(FOCUS_BLUR_IDLE_MIN_MS, FOCUS_BLUR_IDLE_MAX_MS)
    val overrideAppeared = withTimeoutOrNull(idleMs) {
        snapshotFlow { overrideTarget.value }.first { it != null }
    }
    if (overrideAppeared != null) return lastPulsed

    val target = if (lastPulsed == FocusTarget.ENEMY) FocusTarget.BACKGROUND else FocusTarget.ENEMY
    val animatable = if (target == FocusTarget.ENEMY) enemyBlurRadius else backgroundBlurRadius
    val rest = restDpFor(target)
    val peak = FOCUS_BLUR_PEAK_MIN_DP +
        Random.nextFloat() * (FOCUS_BLUR_PEAK_MAX_DP - FOCUS_BLUR_PEAK_MIN_DP)
    val rampInMs = Random.nextInt(FOCUS_BLUR_RAMP_IN_MIN_MS, FOCUS_BLUR_RAMP_IN_MAX_MS)
    val holdMs = Random.nextLong(FOCUS_BLUR_HOLD_MIN_MS, FOCUS_BLUR_HOLD_MAX_MS)
    val rampOutMs = Random.nextInt(FOCUS_BLUR_RAMP_OUT_MIN_MS, FOCUS_BLUR_RAMP_OUT_MAX_MS)

    coroutineScope {
        val pulse = launch {
            animatable.animateTo(peak, tween(rampInMs, easing = EaseInOut))
            delay(holdMs)
            animatable.animateTo(rest, tween(rampOutMs, easing = EaseInOut))
        }
        val watcher = launch {
            snapshotFlow { overrideTarget.value }.first { it != null }
            pulse.cancel()
        }
        pulse.join()
        watcher.cancel()
    }
    return target
}
