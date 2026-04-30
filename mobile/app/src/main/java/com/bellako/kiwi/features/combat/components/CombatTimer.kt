package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun CombatTimer(
    endsAt: Long?,
    modifier: Modifier = Modifier,
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

    Box(
        modifier =
            modifier
                .background(
                    color = colors.color2,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(20.dp)),
                ).border(
                    width = getResponsiveSizeHeight(1.dp),
                    color = colors.color5C,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(20.dp)),
                ).padding(
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
