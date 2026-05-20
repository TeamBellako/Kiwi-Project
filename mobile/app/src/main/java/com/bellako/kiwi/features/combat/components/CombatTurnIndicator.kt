package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.SkillEffectResultType
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val INDICATOR_RADIUS = 12.dp
private val CHEVRON_SIZE = 14.dp
private const val CHEVRON_OPEN_ROTATION = 180f
private const val CHEVRON_CLOSED_ROTATION = 0f

// Inner-glow hues for the turn indicator. Tweak alpha or the base color to retune the feedback.
@Suppress("MagicNumber")
private val TURN_GLOW_DAMAGE = Color(0xB3D63A2F)

@Suppress("MagicNumber")
private val TURN_GLOW_STAT_MOD = Color(0xB330B0FF)

@Composable
@Suppress("LongParameterList")
fun CombatTurnIndicator(
    message: AnnotatedString,
    isLogOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color? = null,
    introAlpha: Float = 1f,
) {
    val colors = LocalKiwiColors.current
    val rotation by animateFloatAsState(
        targetValue = if (isLogOpen) CHEVRON_CLOSED_ROTATION else CHEVRON_OPEN_ROTATION,
        label = "combat_turn_chevron",
    )
    val blinkAlpha = rememberBlinkAlpha()

    LaunchedEffect(message.text) {
        blinkAlpha.blink()
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .alpha(introAlpha)
                .combatPanel(
                    bgColor = colors.color3A,
                    borderColor = colors.color5C,
                    radius = INDICATOR_RADIUS,
                    innerGlowColor = glowColor,
                ).clickable(onClick = onClick)
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.medium),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f).alpha(blinkAlpha.value),
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_AnnotatedString_P2(
                KiwiAnnotatedStringArguments(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                ),
            )
        }

        Kiwi_Image(
            painterResourceId = R.drawable.ic_dialogue_arrow,
            alt = if (isLogOpen) "Close combat log" else "Open combat log",
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(CHEVRON_SIZE))
                    .rotate(rotation),
        )
    }
}

@Composable
fun userTurnMessage(): AnnotatedString {
    val colors = LocalKiwiColors.current
    return buildAnnotatedString {
        withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
            append("IT'S YOUR TURN!")
        }
    }
}

/**
 * Picks the inner-glow hue for the turn indicator based on the latest combat action.
 * Returns null when nothing relevant happened (no glow).
 *
 * Priority: a damage hit on the player wins over any stat-modifying effect in the same action.
 */
@Suppress("ReturnCount")
fun combatTurnGlowColor(action: CombatActionDomain?): Color? {
    if (action == null) return null
    if (action.actionType == CombatActionType.ACTOR_DAMAGED_BY_STATE && action.actor == CombatActor.USER) {
        return TURN_GLOW_DAMAGE
    }
    val effects = action.skillEffectsResults
    if (effects.any { it.typeResult == SkillEffectResultType.DAMAGE && it.target == CombatActor.USER }) {
        return TURN_GLOW_DAMAGE
    }
    if (effects.any { it.typeResult == SkillEffectResultType.MODIFY_STAT }) {
        return TURN_GLOW_STAT_MOD
    }
    return null
}

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 120)
@Composable
fun CombatTurnIndicator_Preview() {
    Kiwi_Theme {
        CombatTurnIndicator(
            message = userTurnMessage(),
            isLogOpen = false,
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}

@Preview(name = "Damage glow", widthDp = 392, heightDp = 120)
@Composable
fun CombatTurnIndicator_DamageGlow_Preview() {
    Kiwi_Theme {
        CombatTurnIndicator(
            message = userTurnMessage(),
            isLogOpen = false,
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
            glowColor = TURN_GLOW_DAMAGE,
        )
    }
}

@Preview(name = "Stat-mod glow", widthDp = 392, heightDp = 120)
@Composable
fun CombatTurnIndicator_StatModGlow_Preview() {
    Kiwi_Theme {
        CombatTurnIndicator(
            message = userTurnMessage(),
            isLogOpen = false,
            onClick = {},
            modifier = Modifier.padding(Spacing.medium),
            glowColor = TURN_GLOW_STAT_MOD,
        )
    }
}
