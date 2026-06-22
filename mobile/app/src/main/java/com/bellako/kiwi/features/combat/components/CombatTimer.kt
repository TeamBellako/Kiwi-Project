package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.delay

private const val TIMER_TICK_MILLIS = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val EMPTY_TIMER = "--:--:--"
private val TIMER_RADIUS = 20.dp

// Below this much time remaining the timer blinks with a red tint to signal urgency.
private const val URGENT_THRESHOLD_MILLIS = 10L * SECONDS_PER_MINUTE * TIMER_TICK_MILLIS

// Duration of a single half of the blink cycle (fade in / fade out).
private const val BLINK_HALF_CYCLE_MILLIS = 550

// How far the red tint pushes toward fully red at the peak of the blink. The
// background tint is kept subtler than the text/border so the time stays legible.
private const val BLINK_TEXT_MAX = 1f
private const val BLINK_BG_MAX = 0.45f

// How far the timer starts shifted up at [introProgress] = 0, in dp. Sized so
// the whole timer panel sits hidden behind the health bar above it during the
// combat intro; the bar above must be rendered on top (zIndex) for the
// "from behind" effect to read correctly.
private val TIMER_INTRO_HIDE_OFFSET = 60.dp

@Composable
fun CombatTimer(
    endsAt: Long?,
    modifier: Modifier = Modifier,
    // 0f = panel parked above its layout slot (so a higher-zIndex element
    // covers it), 1f = settled in place. Drives the "slides out from behind
    // the health bar" beat of the combat intro.
    introProgress: Float = 1f,
) {
    val colors = LocalKiwiColors.current

    var now by remember(endsAt) { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(endsAt) {
        if (endsAt == null) return@LaunchedEffect
        while (true) {
            now = System.currentTimeMillis()
            delay(TIMER_TICK_MILLIS)
        }
    }

    val remainingMillis = if (endsAt == null) null else (endsAt - now).coerceAtLeast(0L)
    val text = if (remainingMillis == null) EMPTY_TIMER else formatRemaining(remainingMillis)
    val isUrgent = remainingMillis != null && remainingMillis <= URGENT_THRESHOLD_MILLIS

    val blink = rememberInfiniteTransition(label = "combatTimerBlink")
    val blinkFraction by blink.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(BLINK_HALF_CYCLE_MILLIS),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "combatTimerBlinkFraction",
    )
    val tint = if (isUrgent) blinkFraction else 0f

    val textColor = lerp(colors.colorF, colors.colorR, tint * BLINK_TEXT_MAX)
    val bgColor = lerp(colors.color2, colors.colorR1, tint * BLINK_BG_MAX)
    val borderColor = lerp(colors.color5C, colors.colorR, tint * BLINK_TEXT_MAX)

    val hideOffsetPx = with(LocalDensity.current) { TIMER_INTRO_HIDE_OFFSET.toPx() }
    val translationY = -hideOffsetPx * (1f - introProgress.coerceIn(0f, 1f))

    Box(
        modifier =
            modifier
                .graphicsLayer { this.translationY = translationY }
                .alpha(introProgress.coerceIn(0f, 1f))
                .combatPanel(bgColor = bgColor, borderColor = borderColor, radius = TIMER_RADIUS)
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.xSmall),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Label3(
            KiwiTextArguments(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

private fun formatRemaining(remainingMillis: Long): String {
    val totalSeconds = remainingMillis / TIMER_TICK_MILLIS
    val hours = totalSeconds / SECONDS_PER_HOUR
    val minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 80)
@Composable
@Suppress("MagicNumber")
fun CombatTimer_Preview() {
    Kiwi_Theme {
        CombatTimer(endsAt = System.currentTimeMillis() + SECONDS_PER_HOUR * 7 * TIMER_TICK_MILLIS)
    }
}

@Preview(name = "Urgent", widthDp = 392, heightDp = 80)
@Composable
@Suppress("MagicNumber")
fun CombatTimer_Urgent_Preview() {
    Kiwi_Theme {
        CombatTimer(endsAt = System.currentTimeMillis() + SECONDS_PER_MINUTE * 6 * TIMER_TICK_MILLIS)
    }
}
