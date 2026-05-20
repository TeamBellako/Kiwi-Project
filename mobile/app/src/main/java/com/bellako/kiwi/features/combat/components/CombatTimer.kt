package com.bellako.kiwi.features.combat.components

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

    val text =
        if (endsAt == null) {
            EMPTY_TIMER
        } else {
            formatRemaining((endsAt - now).coerceAtLeast(0L))
        }

    val hideOffsetPx = with(LocalDensity.current) { TIMER_INTRO_HIDE_OFFSET.toPx() }
    val translationY = -hideOffsetPx * (1f - introProgress.coerceIn(0f, 1f))

    Box(
        modifier =
            modifier
                .graphicsLayer { this.translationY = translationY }
                .alpha(introProgress.coerceIn(0f, 1f))
                .combatPanel(bgColor = colors.color2, borderColor = colors.color5C, radius = TIMER_RADIUS)
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.xSmall),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Label3(
            KiwiTextArguments(
                text = text,
                color = colors.colorF,
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
