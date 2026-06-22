package com.bellako.kiwi.features.combat.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    // Pop scale per slot (0-indexed). Stays > 0 even before the slot's pop
    // starts so the slot's layout bounds remain positive — the matching
    // `slotIntroAlpha` is what hides it visually.
    slotIntroScale: (slotIndex: Int) -> Float = { 1f },
    // Fade-in alpha per slot (0-indexed). Defaults to 1f.
    slotIntroAlpha: (slotIndex: Int) -> Float = { 1f },
) {
    val slotMap = deckSkills.associateBy { it.deckSlot }
    val skillHoldPadding = getResponsiveSizeHeight(4.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        for (rowStart in 1..MAX_DECK_SLOTS step 2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                for (slot in rowStart..rowStart + 1) {
                    val skill = slotMap[slot]
                    val slotMod =
                        Modifier
                            .weight(SKILL_WEIGHT)
                            .scale(slotIntroScale(slot - 1))
                            .alpha(slotIntroAlpha(slot - 1))
                    if (skill != null) {
                        SkillComponent(
                            skill = skill,
                            isDisabled = isDisabled || skill.isCooldown,
                            onClick = { onSkillClick(skill.id, skill.name) },
                            modifier = slotMod,
                            onApplyGoalProgress = onApplyGoalProgress,
                            canOpenDetails = !isDisabled,
                        )
                    } else {
                        Kiwi_Image(
                            R.drawable.skill_empty,
                            "Empty skill slot",
                            modifier = slotMod,
                        )
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
