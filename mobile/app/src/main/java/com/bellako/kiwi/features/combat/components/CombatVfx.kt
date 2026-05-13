package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PLAYER_SHAKE_CYCLES = 4
private const val PLAYER_SHAKE_AMPLITUDE_PX = 28f
private const val PLAYER_SHAKE_STEP_MS = 50
private const val PLAYER_FLASH_CYCLES = 2
private const val PLAYER_FLASH_STEP_MS = 90L
private const val PLAYER_FLASH_ALPHA = 0.15f
private const val PLAYER_VIGNETTE_PEAK_ALPHA = 0.3f
private const val PLAYER_VIGNETTE_RISE_MS = 120
private const val PLAYER_VIGNETTE_FALL_MS = 500
private const val DEATH_PAUSE_MS = 400L
private const val DEATH_BLINK_CYCLES = 3
private const val DEATH_BLINK_RISE_MS = 180
private const val DEATH_BLINK_FALL_MS = 220
private const val DEATH_BLINK_PEAK_ALPHA = 0.55f
private const val DEATH_BLINK_TROUGH_ALPHA = 0.15f
private const val DEATH_HOLD_RISE_MS = 200
private const val DEATH_HOLD_ALPHA = 0.6f
private const val DEATH_VIGNETTE_INNER_ALPHA_FACTOR = 0.4f

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
    val alpha = remember(key) { Animatable(0f) }
    LaunchedEffect(isPlayerDefeated, key) {
        if (!isPlayerDefeated) {
            alpha.snapTo(0f)
            return@LaunchedEffect
        }
        delay(DEATH_PAUSE_MS)
        repeat(DEATH_BLINK_CYCLES) {
            alpha.animateTo(DEATH_BLINK_PEAK_ALPHA, tween(DEATH_BLINK_RISE_MS))
            alpha.animateTo(DEATH_BLINK_TROUGH_ALPHA, tween(DEATH_BLINK_FALL_MS))
        }
        alpha.animateTo(DEATH_HOLD_ALPHA, tween(DEATH_HOLD_RISE_MS))
    }
    return alpha
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

@Composable
internal fun DeathSequenceOverlay(alpha: Float) {
    if (alpha <= 0f) return
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors =
                            listOf(
                                Color.Red.copy(alpha = alpha * DEATH_VIGNETTE_INNER_ALPHA_FACTOR),
                                Color.Red.copy(alpha = alpha),
                            ),
                    ),
                ),
    )
}
