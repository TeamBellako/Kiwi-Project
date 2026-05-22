package com.bellako.kiwi.features.combat.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
import com.bellako.kiwi.features.combat.data.CombatActorDomain
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private const val PLAYER_HEALTH_BAR_WIDTH_FRACTION = 0.5f
private val STATUS_POPUP_BOTTOM_OFFSET = 44.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongParameterList")
internal fun PlayerControls(
    deckSkills: List<SkillDomain>,
    userActor: CombatActorDomain,
    isOverlayOpen: Boolean,
    selectedStatus: CombatActiveStatusDomain?,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onStatusClick: (CombatActiveStatusDomain) -> Unit,
    onDismissPopup: () -> Unit,
    skillSlotIntroScale: (slotIndex: Int) -> Float = { 1f },
    skillSlotIntroAlpha: (slotIndex: Int) -> Float = { 1f },
    playerBarRevealProgress: Float = 1f,
    playerBarNumbersAlpha: Float = 1f,
) {
    val colors = LocalKiwiColors.current
    val dimAlpha = if (isOverlayOpen) KIWI_DISABLED_ALPHA else 1f

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CombatDeck(
                deckSkills = deckSkills,
                isDisabled = isOverlayOpen,
                onSkillClick = onSkillClick,
                onApplyGoalProgress = onApplyGoalProgress,
                modifier = Modifier.alpha(dimAlpha),
                slotIntroScale = skillSlotIntroScale,
                slotIntroAlpha = skillSlotIntroAlpha,
            )

            Kiwi_Spacer(Spacing.small)

            // The health bar is never dimmed by the overlay — the player must
            // be able to read their HP clearly, especially while taking damage.
            CombatHealthBar(
                currentHp = userActor.stats.currentHp,
                maxHp = userActor.stats.maxHp,
                fillTint = colors.color7C,
                label = "Player HP",
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(PLAYER_HEALTH_BAR_WIDTH_FRACTION),
                barRevealProgress = playerBarRevealProgress,
                numbersAlpha = playerBarNumbersAlpha,
            )

            Kiwi_Spacer(Spacing.small)

            CombatStatusRow(
                statuses = userActor.activeStatus,
                selectedStatusId = selectedStatus?.stateId,
                onStatusClick = onStatusClick,
            )
        }

        if (selectedStatus != null) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismissPopup,
                        ),
            )

            CombatStatusPopup(
                status = selectedStatus,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = getResponsiveSizeHeight(STATUS_POPUP_BOTTOM_OFFSET)),
            )
        }
    }
}
