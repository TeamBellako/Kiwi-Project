package com.bellako.kiwi.features.combat.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.model.MAX_DECK_SLOTS
import com.bellako.kiwi.features.skills.screen.SkillComponent
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private const val SKILL_WEIGHT = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongParameterList")
fun CombatDeck(
    deckSkills: List<SkillDomain>,
    isDisabled: Boolean,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    modifier: Modifier = Modifier,
    // Pop-scale per slot (0-indexed in slot order). Defaults to 1f so the deck
    // renders unanimated outside the combat intro.
    slotIntroScale: (slotIndex: Int) -> Float = { 1f },
) {
    val slotMap = deckSkills.associateBy { it.deckSlot }

    Column(modifier = modifier.fillMaxWidth()) {
        for (rowStart in 1..MAX_DECK_SLOTS step 2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (slot in rowStart..rowStart + 1) {
                    val skill = slotMap[slot]
                    Box(
                        modifier =
                            Modifier
                                .weight(SKILL_WEIGHT)
                                .scale(slotIntroScale(slot - 1)),
                    ) {
                        if (skill != null) {
                            SkillComponent(
                                skill = skill,
                                isDisabled = isDisabled || skill.isCooldown,
                                onClick = { onSkillClick(skill.id, skill.name) },
                                onApplyGoalProgress = onApplyGoalProgress,
                            )
                        } else {
                            Kiwi_Image(
                                R.drawable.skill_empty,
                                "Empty skill slot",
                            )
                        }
                    }
                }
            }
            Kiwi_Spacer(Spacing.small)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 300)
@Composable
fun CombatDeck_Preview() {
    Kiwi_Theme {
        CombatDeck(
            deckSkills =
                listOf(
                    SkillsTestFactory.timeCooldownSkillEquipped(),
                    SkillsTestFactory.goalCooldownSkillEquipped(),
                ),
            isDisabled = false,
            onSkillClick = { _, _ -> },
            onApplyGoalProgress = { _, _, _ -> },
        )
    }
}
