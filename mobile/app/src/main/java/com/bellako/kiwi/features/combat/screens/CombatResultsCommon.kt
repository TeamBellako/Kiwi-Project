package com.bellako.kiwi.features.combat.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_P3
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val SKILL_CARD_RADIUS = 14.dp
private val SKILL_ICON_SIZE = 36.dp
private val LOG_TOGGLE_RADIUS = 10.dp
private val CHEVRON_SIZE = 14.dp
private const val CHEVRON_OPEN_ROTATION = 180f
private const val CHEVRON_CLOSED_ROTATION = 0f

internal data class SkillUsedSummary(
    val name: String,
    val count: Int,
    val iconRes: Int?,
    val equivalentText: String?,
)

internal fun buildSkillUsedSummary(
    combat: CombatDomain,
    skillsByName: Map<String, SkillDomain>,
): List<SkillUsedSummary> {
    val counts =
        combat.log
            .asSequence()
            .filter { it.actor == CombatActor.USER && it.actionType == CombatActionType.SKILL_USED }
            .mapNotNull { it.skillName?.takeIf { name -> name.isNotBlank() } }
            .groupingBy { it }
            .eachCount()

    return counts.entries.map { (name, count) ->
        val skill = skillsByName[name]
        SkillUsedSummary(
            name = name,
            count = count,
            iconRes = skill?.icon,
            equivalentText = (skill as? SkillDomain.Goal)?.let { goalEquivalentText(it, count) },
        )
    }
}

private fun goalEquivalentText(
    skill: SkillDomain.Goal,
    count: Int,
): String = "Equivalent to ${count * skill.goalData.target} ${skill.goalData.action}"

@Composable
internal fun SkillsUsedHeader(
    isLogOpen: Boolean,
    onToggleLog: () -> Unit,
) {
    val colors = LocalKiwiColors.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (isLogOpen) CHEVRON_OPEN_ROTATION else CHEVRON_CLOSED_ROTATION,
        label = "skills_log_chevron",
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Kiwi_H3(
            KiwiTextArguments(
                text = "Skills Used",
                color = colors.colorF,
                fontWeight = FontWeight.Bold,
            ),
        )
        Box(modifier = Modifier.weight(1f))
        Row(
            modifier =
                Modifier
                    .background(
                        color = colors.color3A,
                        shape = RoundedCornerShape(getResponsiveSizeHeight(LOG_TOGGLE_RADIUS)),
                    ).border(
                        width = getResponsiveSizeHeight(1.dp),
                        color = colors.color5C,
                        shape = RoundedCornerShape(getResponsiveSizeHeight(LOG_TOGGLE_RADIUS)),
                    ).clickable(onClick = onToggleLog)
                    .padding(
                        horizontal = getResponsiveSizeWidth(Spacing.medium),
                        vertical = getResponsiveSizeHeight(Spacing.small),
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Kiwi_Label2(
                KiwiTextArguments(
                    text = if (isLogOpen) "Close" else "Log",
                    color = colors.color7A,
                    italic = true,
                ),
            )
            Spacer(modifier = Modifier.size(getResponsiveSizeWidth(Spacing.xSmall)))
            Kiwi_Image(
                painterResourceId = R.drawable.ic_dialogue_arrow,
                alt = if (isLogOpen) "Close combat log" else "Open combat log",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(CHEVRON_SIZE))
                        .rotate(chevronRotation),
            )
        }
    }
}

@Composable
internal fun SkillsUsedList(skillsUsed: List<SkillUsedSummary>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
    ) {
        items(skillsUsed, key = { it.name }) { entry ->
            SkillUsedRow(entry)
        }
    }
}

@Composable
private fun SkillUsedRow(entry: SkillUsedSummary) {
    val colors = LocalKiwiColors.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(
                    color = colors.color3A,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(SKILL_CARD_RADIUS)),
                ).border(
                    width = getResponsiveSizeHeight(1.dp),
                    color = colors.color5C,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(SKILL_CARD_RADIUS)),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkillInfoColumn(
            entry = entry,
            modifier = Modifier.weight(1f),
        )

        if (entry.equivalentText != null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(getResponsiveSizeHeight(1.dp))
                        .background(colors.color5C),
            )
            EquivalentColumn(
                text = entry.equivalentText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SkillInfoColumn(
    entry: SkillUsedSummary,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    Row(
        modifier =
            modifier.padding(
                horizontal = getResponsiveSizeWidth(Spacing.medium),
                vertical = getResponsiveSizeHeight(Spacing.small),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(getResponsiveSizeHeight(SKILL_ICON_SIZE))
                    .background(color = colors.color5, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (entry.iconRes != null) {
                Kiwi_Image(
                    painterResourceId = entry.iconRes,
                    alt = entry.name,
                    modifier = Modifier.size(getResponsiveSizeHeight(SKILL_ICON_SIZE - 12.dp)),
                )
            }
        }

        Spacer(modifier = Modifier.size(getResponsiveSizeWidth(Spacing.small)))

        Column {
            Kiwi_Label2(
                KiwiTextArguments(
                    text = entry.name,
                    color = colors.colorF,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Kiwi_P3(
                KiwiTextArguments(
                    text = "x${entry.count}",
                    color = colors.color7A,
                ),
            )
        }
    }
}

@Composable
private fun EquivalentColumn(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    Box(
        modifier =
            modifier.padding(
                horizontal = getResponsiveSizeWidth(Spacing.medium),
                vertical = getResponsiveSizeHeight(Spacing.small),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_P3(
            KiwiTextArguments(
                text = text,
                color = colors.color7A,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}
