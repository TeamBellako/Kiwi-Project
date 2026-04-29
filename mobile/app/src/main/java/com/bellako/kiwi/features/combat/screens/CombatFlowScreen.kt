package com.bellako.kiwi.features.combat.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.audio.Kiwi_Music_Combat
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.skills.data.SkillDomain
import kotlinx.coroutines.delay

private const val DEFEAT_TRANSITION_DELAY_MS = 1800L
private const val DEFEAT_FADE_MS = 600
private const val VICTORY_TRANSITION_DELAY_MS = 1200L
private const val VICTORY_FADE_MS = 600

private enum class CombatPhase { COMBAT, VICTORY, DEFEAT }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CombatFlowScreen(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    isTurnPlaying: Boolean,
    onConfirmAbandon: () -> Unit,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onDismiss: () -> Unit,
    onVictoryContinue: () -> Unit,
) {
    Kiwi_Music_Combat(combat.musicId)

    Box(modifier = Modifier.fillMaxSize()) {
        val isDefeat = isCombatDefeat(combat)
        val isVictory = isCombatVictory(combat)
        val phase =
            produceState(CombatPhase.COMBAT, combat.id, isDefeat, isVictory) {
                value =
                    when {
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

        val fadeDuration =
            when (phase.value) {
                CombatPhase.VICTORY -> VICTORY_FADE_MS
                else -> DEFEAT_FADE_MS
            }
        Crossfade(
            targetState = phase.value,
            animationSpec = tween(fadeDuration, easing = EaseInOut),
            label = "combat_phase",
        ) { current ->
            when (current) {
                CombatPhase.DEFEAT ->
                    CombatDefeatScreen(
                        combat = combat,
                        deckSkills = deckSkills,
                        onContinue = onDismiss,
                    )
                CombatPhase.VICTORY ->
                    CombatVictoryScreen(
                        combat = combat,
                        deckSkills = deckSkills,
                        onContinue = onVictoryContinue,
                    )
                CombatPhase.COMBAT ->
                    CombatScreen(
                        combat = combat,
                        deckSkills = deckSkills,
                        isTurnPlaying = isTurnPlaying,
                        onConfirmAbandon = onConfirmAbandon,
                        onSkillClick = onSkillClick,
                        onApplyGoalProgress = onApplyGoalProgress,
                    )
            }
        }
    }
}

private fun isCombatDefeat(combat: CombatDomain): Boolean =
    combat.combatStatus == CombatGeneralStatus.USER_LOST &&
        combat.log.none { it.actionType == CombatActionType.ABANDON }

private fun isCombatVictory(combat: CombatDomain): Boolean = combat.combatStatus == CombatGeneralStatus.USER_WON
