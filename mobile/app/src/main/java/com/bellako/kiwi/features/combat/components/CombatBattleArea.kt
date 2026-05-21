package com.bellako.kiwi.features.combat.components

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.rememberBreathingModifier
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val HEALTH_BAR_WIDTH_FRACTION = 0.6f
private const val LOG_HEIGHT_FRACTION = 0.85f
private const val DAMAGE_WIGGLE_CYCLES = 4
private const val DAMAGE_WIGGLE_AMPLITUDE_PX = 22f
private const val DAMAGE_WIGGLE_STEP_MS = 50
private const val DAMAGE_FLASH_CYCLES = 3
private const val DAMAGE_FLASH_STEP_MS = 90L
private const val DAMAGE_FLASH_RED_ALPHA = 0.65f
private const val DAMAGE_FLASH_DIM_ALPHA = 0.4f
private const val ENEMY_DEFEAT_POST_DAMAGE_PAUSE_MS = 250L
private const val ENEMY_DEFEAT_FADE_MS = 800
private const val LOG_DIM_ALPHA = 0.55f

@Composable
@Suppress("LongParameterList")
internal fun ColumnScope.CombatBattleArea(
    combat: CombatDomain,
    isLogOpen: Boolean,
    onDismissLog: () -> Unit,
    logEntries: List<CombatLogEntry>,
    enemyBarRevealProgress: Float = 1f,
    enemyBarNumbersAlpha: Float = 1f,
    timerIntroProgress: Float = 1f,
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth(),
    ) {
        EnemyHud(
            currentHp = combat.enemy.stats.currentHp,
            maxHp = combat.enemy.stats.maxHp,
            endsAt = combat.endsAt,
            barRevealProgress = enemyBarRevealProgress,
            barNumbersAlpha = enemyBarNumbersAlpha,
            timerIntroProgress = timerIntroProgress,
        )

        if (isLogOpen) {
            LogDimOverlay(
                modifier = Modifier.fillMaxSize(),
                onDismiss = onDismissLog,
            )

            CombatLog(
                entries = logEntries,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            horizontal = getResponsiveSizeWidth(Spacing.medium),
                            vertical = getResponsiveSizeHeight(Spacing.small),
                        ).fillMaxHeight(LOG_HEIGHT_FRACTION)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
            )
        }
    }
}

@Composable
@Suppress("LongParameterList")
internal fun CombatEnemySprite(
    enemySprite: String,
    currentHp: Int,
    isEnemyDefeated: Boolean,
    context: Context,
    modifier: Modifier = Modifier,
    introAlpha: Float = 1f,
) {
    var previousHp by remember { mutableIntStateOf(currentHp) }
    var damageTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentHp) {
        if (currentHp < previousHp) damageTrigger++
        previousHp = currentHp
    }

    val offsetX = remember { Animatable(0f) }
    val redAlpha = remember { Animatable(0f) }
    val spriteAlpha = remember { Animatable(1f) }
    var isDamageAnimating by remember { mutableStateOf(false) }

    val breathingModifier = rememberBreathingModifier()

    LaunchedEffect(damageTrigger) {
        if (damageTrigger == 0) return@LaunchedEffect
        isDamageAnimating = true
        try {
            coroutineScope {
                launch {
                    offsetX.shake(DAMAGE_WIGGLE_CYCLES, DAMAGE_WIGGLE_AMPLITUDE_PX, DAMAGE_WIGGLE_STEP_MS)
                }
                launch {
                    repeat(DAMAGE_FLASH_CYCLES) {
                        redAlpha.snapTo(DAMAGE_FLASH_RED_ALPHA)
                        spriteAlpha.snapTo(DAMAGE_FLASH_DIM_ALPHA)
                        delay(DAMAGE_FLASH_STEP_MS)
                        redAlpha.snapTo(0f)
                        spriteAlpha.snapTo(1f)
                        delay(DAMAGE_FLASH_STEP_MS)
                    }
                }
            }
        } finally {
            isDamageAnimating = false
        }
    }

    LaunchedEffect(isEnemyDefeated) {
        if (!isEnemyDefeated) return@LaunchedEffect
        snapshotFlow { isDamageAnimating }.first { !it }
        delay(ENEMY_DEFEAT_POST_DAMAGE_PAUSE_MS)
        spriteAlpha.animateTo(0f, tween(ENEMY_DEFEAT_FADE_MS))
    }

    Kiwi_Image(
        painterResourceId = resolveEnemySprite(enemySprite, context),
        alt = "Enemy sprite",
        modifier =
            modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .alpha(spriteAlpha.value * introAlpha)
                .then(breathingModifier),
        contentScale = ContentScale.Fit,
        colorFilter =
            if (redAlpha.value > 0f) {
                ColorFilter.tint(
                    Color.Red.copy(alpha = redAlpha.value),
                    BlendMode.SrcAtop,
                )
            } else {
                null
            },
    )
}

@Composable
@Suppress("LongParameterList")
private fun EnemyHud(
    currentHp: Int,
    maxHp: Int,
    endsAt: Long?,
    barRevealProgress: Float = 1f,
    barNumbersAlpha: Float = 1f,
    timerIntroProgress: Float = 1f,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = getResponsiveSizeHeight(Spacing.small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.xSmall)),
        ) {
            // zIndex on the health bar so the timer below can render behind it
            // during the intro slide-down.
            CombatHealthBar(
                currentHp = currentHp,
                maxHp = maxHp,
                modifier = Modifier.fillMaxWidth(HEALTH_BAR_WIDTH_FRACTION).zIndex(1f),
                barRevealProgress = barRevealProgress,
                numbersAlpha = barNumbersAlpha,
            )

            CombatTimer(
                endsAt = endsAt,
                introProgress = timerIntroProgress,
            )
        }
    }
}

@Composable
internal fun LogDimOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .background(Color.Black.copy(alpha = LOG_DIM_ALPHA))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss,
                ),
    )
}

private fun resolveEnemySprite(
    spriteName: String,
    context: Context,
): Int = AssetResolver.drawableOr(context, spriteName, R.drawable.character_liria_base)
