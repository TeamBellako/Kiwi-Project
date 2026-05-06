package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.features.combat.data.ActiveBarkDomain
import com.bellako.kiwi.features.combat.data.BarkDismissMode
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.delay

private const val DEFAULT_AUTO_DISMISS_MS = 3000L
private const val ARROW_BOUNCE_MS = 600
private const val ARROW_BOUNCE_TARGET = -10f
private val BARK_MIN_WIDTH = 180.dp
private val BARK_MAX_WIDTH = 280.dp
private val BARK_PADDING_HORIZONTAL = 24.dp
private val BARK_PADDING_VERTICAL = 20.dp
private val BARK_ARROW_SIZE = 12.dp

@Composable
fun CombatBarkBubble(
    bark: ActiveBarkDomain,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    val context = LocalContext.current
    val isAuto = bark.dismissMode == BarkDismissMode.AUTO

    if (isAuto) {
        LaunchedEffect(bark.triggerId) {
            val ms =
                bark.conversation.delayEndMs
                    ?.toLong()
                    ?.takeIf { it > 0 } ?: DEFAULT_AUTO_DISMISS_MS
            delay(ms)
            onDismiss()
        }
    }

    val clickModifier =
        if (isAuto) {
            Modifier
        } else {
            Modifier.clickable {
                AudioManager.playSFX(context, R.raw.snd_fx_03_page)
                onDismiss()
            }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .widthIn(
                    min = getResponsiveSizeWidth(BARK_MIN_WIDTH),
                    max = getResponsiveSizeWidth(BARK_MAX_WIDTH),
                ).then(clickModifier),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.dialogue_small_bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
            )
            Kiwi_P2(
                KiwiTextArguments(
                    bark.conversation.dialog,
                    textAlign = TextAlign.Center,
                    color = colors.color6,
                    modifier =
                        Modifier.padding(
                            horizontal = getResponsiveSizeWidth(BARK_PADDING_HORIZONTAL),
                            vertical = getResponsiveSizeHeight(BARK_PADDING_VERTICAL),
                        ),
                ),
            )
        }
        if (!isAuto) {
            BarkClickIndicator()
        }
    }
}

@Composable
private fun BarkClickIndicator() {
    val transition = rememberInfiniteTransition(label = "bark_arrow_bounce")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = ARROW_BOUNCE_TARGET,
        animationSpec =
            infiniteRepeatable(
                animation = tween(ARROW_BOUNCE_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "bark_arrow_offset",
    )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = getResponsiveSizeHeight(Spacing.xSmall)),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Image(
            R.drawable.ic_dialogue_arrow,
            "Tap to continue",
            modifier =
                Modifier
                    .size(getResponsiveSizeWidth(BARK_ARROW_SIZE), getResponsiveSizeHeight(BARK_ARROW_SIZE))
                    .offset(y = getResponsiveSizeHeight(offsetY.dp)),
        )
    }
}
