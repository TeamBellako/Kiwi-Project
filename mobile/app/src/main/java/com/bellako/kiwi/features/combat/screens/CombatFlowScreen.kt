package com.bellako.kiwi.features.combat.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.audio.Kiwi_Music_Combat
import com.bellako.kiwi.features.combat.data.ActiveBarkDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.skills.data.SkillDomain
import kotlinx.coroutines.delay

// Held off until the death "closing eyes" sequence has fully blacked out the
// screen (see CombatVfx death timing, ~2940ms), plus a margin so the combat
// screen stays solid black for the whole crossfade and never peeks through.
private const val DEFEAT_TRANSITION_DELAY_MS = 3200L
private const val VICTORY_TRANSITION_DELAY_MS = 1200L
private const val VICTORY_FADE_MS = 600
private const val DEFEAT_REVEAL_FADE_MS = 900

private enum class CombatPhase { COMBAT, VICTORY, DEFEAT }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongParameterList")
fun CombatFlowScreen(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    isTurnPlaying: Boolean,
    activeBark: ActiveBarkDomain? = null,
    onBarkDismiss: () -> Unit = {},
    onConfirmAbandon: () -> Unit,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onDismiss: () -> Unit,
    onVictoryContinue: () -> Unit,
) {
    Kiwi_Music_Combat(combat.music)

    Box(modifier = Modifier.fillMaxSize()) {
        val isDefeat = isCombatDefeat(combat)
        val isVictory = isCombatVictory(combat)
        val isBarkActive = activeBark != null
        val phase =
            produceState(CombatPhase.COMBAT, combat.id, isDefeat, isVictory, isBarkActive) {
                value =
                    when {
                        isBarkActive -> CombatPhase.COMBAT
                        isDefeat -> {
                            delay(DEFEAT_TRANSITION_DELAY_MS)
                            CombatPhase.DEFEAT
                        }
                        isVictory -> {
                            delay(VICTORY_TRANSITION_DELAY_MS)
                            CombatPhase.VICTORY
                        }
                        else -> CombatPhase.COMBAT
                    }
            }

        val context = LocalContext.current
        LaunchedEffect(combat.id, phase.value) {
            when (phase.value) {
                CombatPhase.VICTORY -> AudioManager.playSFX(context, R.raw.snd_fx_01_victory)
                CombatPhase.DEFEAT -> AudioManager.playSFX(context, R.raw.snd_fx__06_loose)
                CombatPhase.COMBAT -> Unit
            }
        }

        // The defeat screen is revealed from black: the death "closing eyes"
        // sequence has already blacked out the combat screen, so the defeat
        // screen cuts in behind a full black veil that then fades away. A
        // crossfade here would instead re-reveal the combat screen underneath.
        if (phase.value == CombatPhase.DEFEAT) {
            CombatDefeatScreen(
                combat = combat,
                deckSkills = deckSkills,
                onContinue = onDismiss,
            )
            DefeatRevealVeil()
        } else {
            Crossfade(
                targetState = phase.value,
                animationSpec = tween(VICTORY_FADE_MS, easing = EaseInOut),
                label = "combat_phase",
            ) { current ->
                when (current) {
                    CombatPhase.VICTORY ->
                        CombatVictoryScreen(
                            combat = combat,
                            deckSkills = deckSkills,
                            onContinue = onVictoryContinue,
                        )
                    CombatPhase.COMBAT, CombatPhase.DEFEAT ->
                        CombatScreen(
                            combat = combat,
                            deckSkills = deckSkills,
                            isTurnPlaying = isTurnPlaying,
                            activeBark = activeBark,
                            onBarkDismiss = onBarkDismiss,
                            onConfirmAbandon = onConfirmAbandon,
                            onSkillClick = onSkillClick,
                            onApplyGoalProgress = onApplyGoalProgress,
                        )
                }
            }
        }
    }
}

private fun isCombatDefeat(combat: CombatDomain): Boolean =
    combat.combatStatus == CombatGeneralStatus.USER_LOST &&
        combat.log.none { it.actionType == CombatActionType.ABANDON }

private fun isCombatVictory(combat: CombatDomain): Boolean = combat.combatStatus == CombatGeneralStatus.USER_WON

// Black veil that hands the death-sequence blackout over to the defeat screen:
// it starts fully opaque (matching the closed-eyes overlay) and fades out so
// the defeat screen is revealed from black instead of cutting in.
@Composable
private fun DefeatRevealVeil() {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(0f, tween(DEFEAT_REVEAL_FADE_MS, easing = EaseInOut))
    }
    if (alpha.value > 0f) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha.value)),
        )
    }
}
