package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.AnnotatedString.Builder as AnnotatedStringBuilder
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine_Text
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.KiwiColorsData
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private val LOG_RADIUS = 12.dp
private val LOG_INNER_PADDING = 16.dp
private const val USER_NAME_PLACEHOLDER = "You"

sealed class CombatLogEntry {
    data class Action(val text: AnnotatedString) : CombatLogEntry()

    data class TurnSeparator(val turnNumber: Int) : CombatLogEntry()

    data class Intro(val text: AnnotatedString) : CombatLogEntry()
}

@Composable
fun CombatLog(
    entries: List<CombatLogEntry>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    val listState = rememberLazyListState()

    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = colors.color3A,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(LOG_RADIUS)),
                ).border(
                    width = getResponsiveSizeHeight(1.dp),
                    color = colors.color5C,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(LOG_RADIUS)),
                ),
    ) {
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = getResponsiveSizeWidth(LOG_INNER_PADDING),
                        vertical = getResponsiveSizeHeight(LOG_INNER_PADDING),
                    ),
            verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
        ) {
            items(entries) { entry ->
                when (entry) {
                    is CombatLogEntry.Action ->
                        Kiwi_AnnotatedString_P2(
                            KiwiAnnotatedStringArguments(
                                text = entry.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ),
                        )

                    is CombatLogEntry.Intro ->
                        Kiwi_AnnotatedString_P2(
                            KiwiAnnotatedStringArguments(
                                text = entry.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ),
                        )

                    is CombatLogEntry.TurnSeparator -> {
                        Kiwi_Spacer(Spacing.xSmall)
                        Kiwi_HorizontalLine_Text(
                            text = "Turn ${entry.turnNumber}",
                            color = colors.color5C,
                            textColor = colors.color7A,
                        )
                        Kiwi_Spacer(Spacing.xSmall)
                    }
                }
            }
        }
    }
}

fun buildCombatLogEntries(
    actions: List<CombatActionDomain>,
    enemyName: String,
    combatStatus: CombatGeneralStatus,
    colors: KiwiColorsData,
    introMessages: List<String> = emptyList(),
): List<CombatLogEntry> {
    val result = mutableListOf<CombatLogEntry>()

    introMessages.forEach { intro ->
        result +=
            CombatLogEntry.Intro(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
                        append(intro)
                    }
                },
            )
    }

    actions.forEach { action ->
        result += CombatLogEntry.Action(formatAction(action, enemyName, colors))
    }

    if (combatStatus != CombatGeneralStatus.ONGOING) {
        val outcome =
            when (combatStatus) {
                CombatGeneralStatus.USER_WON -> "You won the battle!"
                CombatGeneralStatus.USER_LOST -> "You were defeated."
                CombatGeneralStatus.ONGOING -> ""
            }
        result +=
            CombatLogEntry.Intro(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.color8A, fontWeight = FontWeight.Bold)) {
                        append(outcome)
                    }
                },
            )
    }

    return result
}

private fun formatAction(
    action: CombatActionDomain,
    enemyName: String,
    colors: KiwiColorsData,
): AnnotatedString {
    val actorName = if (action.actor == CombatActor.USER) USER_NAME_PLACEHOLDER else enemyName
    return buildAnnotatedString {
        when (action.actionType) {
            CombatActionType.SKILL_USED -> {
                appendActor(actorName, colors)
                append(" used ")
                action.skillName?.let { appendSkill(it, colors) }
                append("!")
            }

            CombatActionType.ACTOR_BLOCKED_BY_STATE -> {
                appendActor(actorName, colors)
                append(" is blocked by ")
                appendStatus(action.stateName, colors)
                append(".")
            }

            CombatActionType.SKILL_REPEAT_BY_STATE -> {
                appendActor(actorName, colors)
                append(" repeats ")
                action.skillName?.let { appendSkill(it, colors) }
                append(" because of ")
                appendStatus(action.stateName, colors)
                append(".")
            }

            CombatActionType.ACTOR_DAMAGED_BY_STATE -> {
                appendActor(actorName, colors)
                append(" suffers from ")
                appendStatus(action.stateName, colors)
                append(".")
            }

            CombatActionType.BLOCKED_SKILLS_BY_STATE -> {
                appendActor(actorName, colors)
                append("'s skills are blocked by ")
                appendStatus(action.stateName, colors)
                append(".")
            }

            CombatActionType.RELEASED_SKILLS_BY_STATE -> {
                appendActor(actorName, colors)
                append("'s skills are released from ")
                appendStatus(action.stateName, colors)
                append(".")
            }

            CombatActionType.SKIP -> {
                appendActor(actorName, colors)
                append(" skipped the turn.")
            }

            CombatActionType.STATUS_TURN_REDUCED -> {
                appendStatus(action.stateName, colors)
                append(" weakens on ")
                appendActor(actorName, colors)
                append(".")
            }

            CombatActionType.STATUS_FINISHED -> {
                appendStatus(action.stateName, colors)
                append(" wears off on ")
                appendActor(actorName, colors)
                append(".")
            }

            CombatActionType.TIMEOUT -> append("The battle timed out.")
            CombatActionType.ABANDON -> {
                appendActor(actorName, colors)
                append(" abandoned the battle.")
            }
        }
    }
}

private fun AnnotatedStringBuilder.appendActor(
    name: String,
    colors: KiwiColorsData,
) {
    withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
        append(name)
    }
}

private fun AnnotatedStringBuilder.appendSkill(
    name: String,
    colors: KiwiColorsData,
) {
    withStyle(SpanStyle(color = colors.color8A, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
        append(name)
    }
}

private fun AnnotatedStringBuilder.appendStatus(
    name: String?,
    colors: KiwiColorsData,
) {
    val safeName = name ?: "an effect"
    withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
        append(safeName)
    }
}

@Preview(name = "Medium Phone", widthDp = 392, heightDp = 600)
@Composable
@Suppress("MagicNumber")
fun CombatLog_Preview() {
    Kiwi_Theme {
        val colors = LocalKiwiColors.current
        val entries =
            remember {
                buildCombatLogEntries(
                    actions =
                        listOf(
                            CombatTestFactory.skillUsedAction(skillName = "Smite"),
                            CombatTestFactory.skillUsedAction(actor = CombatActor.ENEMY, skillName = "Insomnia"),
                            CombatTestFactory.skillUsedAction(skillName = "Wind"),
                        ),
                    enemyName = "Procrastinogre",
                    combatStatus = CombatGeneralStatus.ONGOING,
                    colors = colors,
                    introMessages =
                        listOf(
                            "The battle has begun!",
                            "Procrastinogre stands before the coming ruin.",
                        ),
                )
            }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(LocalKiwiColors.current.color2)
                    .padding(Spacing.medium),
        ) {
            Column {
                CombatLog(entries = entries)
                Kiwi_Spacer()
                Kiwi_P2(KiwiTextArguments("(preview)", color = LocalKiwiColors.current.color7A))
            }
        }
    }
}
