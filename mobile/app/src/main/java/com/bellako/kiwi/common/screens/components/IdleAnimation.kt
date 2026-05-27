@file:Suppress("MagicNumber")

package com.bellako.kiwi.common.screens.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val FLOATING_SPRITE_KEY = "liria"

private const val BREATHING_MIN_SCALE = 1f
private const val BREATHING_MAX_SCALE = 1.02f
private const val BREATHING_PERIOD_MS = 2200

private const val FLOAT_PERIOD_MS = 4500

private val FULL_TURN_RADIANS = (2.0 * PI).toFloat()

/**
 * Idle animation for a talking character sprite. Liria is an airborne entity,
 * so she gets a gentle floating orbit; every other character "breathes" in
 * place. The choice is driven off the sprite name so both conversation
 * layouts pick the right idle without extra plumbing.
 */
@Composable
fun rememberCharacterIdleModifier(sprite: String): Modifier =
    if (sprite.contains(FLOATING_SPRITE_KEY, ignoreCase = true)) {
        rememberFloatingModifier()
    } else {
        rememberBreathingModifier()
    }

/**
 * A subtle, looping scale pulse — as if the character were breathing.
 * Anchored bottom-center so the character stays planted while the body
 * gently expands. The animated value is read only inside [graphicsLayer], so
 * the pulse runs on the draw phase without triggering recomposition.
 */
@Composable
fun rememberBreathingModifier(): Modifier {
    val scale = rememberBreathingScale()
    return Modifier.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        transformOrigin = TransformOrigin(0.5f, 1f)
    }
}

/**
 * The breathing scale value driving [rememberBreathingModifier], exposed for
 * callers that want to fold the breathing scale into their own graphicsLayer
 * (e.g. alongside a renderEffect) instead of stacking two layers.
 */
@Composable
fun rememberBreathingScale(): State<Float> {
    val transition = rememberInfiniteTransition(label = "breathing")
    return transition.animateFloat(
        initialValue = BREATHING_MIN_SCALE,
        targetValue = BREATHING_MAX_SCALE,
        animationSpec =
            infiniteRepeatable(
                animation = tween(BREATHING_PERIOD_MS, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "breathing_scale",
    )
}

/**
 * A slow elliptical drift — a "horizontal wheel" — that mimics an airborne
 * entity floating around as it talks. Horizontal travel is wider than
 * vertical so the orbit reads as a wheel lying flat. The angle advances
 * linearly and is read only inside [graphicsLayer], so the orbit runs on the
 * draw phase without triggering recomposition.
 */
@Composable
fun rememberFloatingModifier(): Modifier {
    val transition = rememberInfiniteTransition(label = "floating")
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_TURN_RADIANS,
        animationSpec =
            infiniteRepeatable(
                animation = tween(FLOAT_PERIOD_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "floating_angle",
    )
    val horizontalAmplitude = 14.dp
    val verticalAmplitude = 6.dp
    return Modifier.graphicsLayer {
        translationX = horizontalAmplitude.toPx() * sin(angle)
        translationY = verticalAmplitude.toPx() * cos(angle)
    }
}
