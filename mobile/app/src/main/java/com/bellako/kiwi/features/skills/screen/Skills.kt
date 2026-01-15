package com.bellako.kiwi.features.skills.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.features.skills.data.CooldownType
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Skill(
    skill: SkillDomain,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier
                    .clickable { },
        ) {
            // Background image
            Kiwi_Image(
                R.drawable.button_skill,
                "Skill background",
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // ICON
                Column(
                    modifier =
                        Modifier
                            .padding(getResponsiveSizeHeight(12.dp)),
                ) {
                    Kiwi_Image(
                        skillIcon(skill.icon),
                        "Skill Icon",
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(65.dp)),
                    )
                }

                // TEXT
                Column {
                    Kiwi_H1(
                        KiwiTextArguments(
                            text = skill.name,
                        ),
                    )

                    Kiwi_P2(
                        KiwiTextArguments(
                            color = kiwiColors.color7A,
                            text = skillStatusText(skill.isCooldown, skill.cooldownType),
                            italic = true,
                            modifier =
                                Modifier
                                    .padding(
                                        bottom = getResponsiveSizeHeight(Spacing.xSmall),
                                        end = getResponsiveSizeHeight(Spacing.small),
                                    ),
                        ),
                    )
                }
            }
        }
    }
}

// HELPERS
@DrawableRes
private fun skillIcon(skillIcon: Int): Int =
    when (skillIcon) {
        1 -> R.drawable.ic_skill_1
        2 -> R.drawable.ic_skill_2
        else -> R.drawable.ic_skill_3
    }

private fun skillStatusText(
    isCooldown: Boolean,
    cooldownType: CooldownType,
): String =
    if (isCooldown) {
        when (cooldownType) {
            CooldownType.TIME -> "Cooldown"
            CooldownType.GOAL -> "Cooldown"
            CooldownType.OTHER -> "Blocked"
        }
    } else {
        "Ready"
    }
