package com.bellako.kiwi.features.combat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
import com.bellako.kiwi.ui.KiwiColorsData
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import androidx.compose.ui.text.AnnotatedString.Builder as AnnotatedStringBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LOG_RADIUS = 12.dp
private val LOG_INNER_PADDING = 16.dp
// The player as the sentence subject ("You have used...") vs. as the object
// ("...against you").
private const val USER_NAME_PLACEHOLDER = "You"
private const val USER_NAME_PLACEHOLDER_TARGET = "you"
private const val LOG_BG_ALPHA = 0.85f

sealed class CombatLogEntry {
    data class Action(
        val text: AnnotatedString,
    ) : CombatLogEntry()

    data class TimeSeparator(
        val timestampMillis: Long,
    ) : CombatLogEntry()

    data class Intro(
        val text: AnnotatedString,
    ) : CombatLogEntry()
}

@Composable
fun CombatLog(
    entries: List<CombatLogEntry>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKiwiColors.current
    val listState = rememberLazyListState()
    val blinkAlpha = rememberBlinkAlpha()

    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
            blinkAlpha.blink()
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .combatPanel(
                    bgColor = colors.color3A.copy(alpha = LOG_BG_ALPHA),
                    borderColor = colors.color5C,
                    radius = LOG_RADIUS,
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
            itemsIndexed(entries) { index, entry ->
                val entryModifier =
                    if (index == entries.lastIndex) {
                        Modifier.alpha(blinkAlpha.value)
                    } else {
                        Modifier
                    }
                when (entry) {
                    is CombatLogEntry.Action ->
                        Kiwi_AnnotatedString_P2(
                            KiwiAnnotatedStringArguments(
                                text = entry.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().then(entryModifier),
                            ),
                        )

                    is CombatLogEntry.Intro ->
                        Kiwi_AnnotatedString_P2(
                            KiwiAnnotatedStringArguments(
                                text = entry.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().then(entryModifier),
                            ),
                        )

                    is CombatLogEntry.TimeSeparator ->
                        Column(modifier = Modifier.fillMaxWidth().then(entryModifier)) {
                            Kiwi_Spacer(Spacing.xSmall)
                            Kiwi_HorizontalLine_Text(
                                text = formatLogTime(entry.timestampMillis),
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

    // A timestamp separator opens each turn group — i.e. whenever the backend
    // turn time changes. Actions with no timestamp (a combat's initial log)
    // simply get no separator.
    var lastTimestamp: Long? = null
    actions.forEach { action ->
        val timestamp = action.createdAt
        if (timestamp != null && timestamp != lastTimestamp) {
            result += CombatLogEntry.TimeSeparator(timestamp)
            lastTimestamp = timestamp
        }
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

@Suppress("CyclomaticComplexMethod")
private fun formatAction(
    action: CombatActionDomain,
    enemyName: String,
    colors: KiwiColorsData,
): AnnotatedString {
    val actorIsUser = action.actor == CombatActor.USER
    val actorName = if (actorIsUser) USER_NAME_PLACEHOLDER else enemyName
    val targetActor = if (actorIsUser) CombatActor.ENEMY else CombatActor.USER
    val targetName = if (actorIsUser) enemyName else USER_NAME_PLACEHOLDER_TARGET
    return buildAnnotatedString {
        when (action.actionType) {
            CombatActionType.SKILL_USED -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(if (actorIsUser) " have used " else " has used ", colors)
                action.skillName?.let { appendSkill(it, action.actor, colors) }
                appendNarrative(" against ", colors)
                appendActor(targetName, targetActor, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.ACTOR_BLOCKED_BY_STATE -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(" is blocked by ", colors)
                appendStatus(action.stateName, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.SKILL_REPEAT_BY_STATE -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(" repeats ", colors)
                action.skillName?.let { appendSkill(it, action.actor, colors) }
                appendNarrative(" because of ", colors)
                appendStatus(action.stateName, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.ACTOR_DAMAGED_BY_STATE -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(" suffers from ", colors)
                appendStatus(action.stateName, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.BLOCKED_SKILLS_BY_STATE -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative("'s skills are blocked by ", colors)
                appendStatus(action.stateName, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.RELEASED_SKILLS_BY_STATE -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative("'s skills are released from ", colors)
                appendStatus(action.stateName, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.SKIP -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(" skipped the turn.", colors)
            }

            CombatActionType.ACTOR_SKIPPED_BY_TURNS -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative("'s turn was skipped!", colors)
            }

            CombatActionType.STATUS_TURN_REDUCED -> {
                appendStatus(action.stateName, colors)
                appendNarrative(" weakens on ", colors)
                appendActor(actorName, action.actor, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.STATUS_FINISHED -> {
                appendStatus(action.stateName, colors)
                appendNarrative(" wears off on ", colors)
                appendActor(actorName, action.actor, colors)
                appendNarrative(".", colors)
            }

            CombatActionType.TIMEOUT -> appendNarrative("The battle timed out.", colors)
            CombatActionType.ABANDON -> {
                appendActor(actorName, action.actor, colors)
                appendNarrative(" abandoned the battle.", colors)
            }
        }
    }
}

private fun AnnotatedStringBuilder.appendActor(
    name: String,
    actor: CombatActor,
    colors: KiwiColorsData,
) {
    val color = if (actor == CombatActor.ENEMY) colors.colorPurple else colors.color7A
    withStyle(SpanStyle(color = color, fontStyle = FontStyle.Italic)) {
        append(name)
    }
}

private fun AnnotatedStringBuilder.appendSkill(
    name: String,
    actor: CombatActor,
    colors: KiwiColorsData,
) {
    val color = if (actor == CombatActor.ENEMY) colors.colorR else colors.color8A
    withStyle(SpanStyle(color = color, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)) {
        append(name)
    }
}

// Connective, narrative glue — everything that isn't an actor, skill or
// status. Uses the regular button text colour so it reads as plain prose.
private fun AnnotatedStringBuilder.appendNarrative(
    text: String,
    colors: KiwiColorsData,
) {
    withStyle(SpanStyle(color = colors.colorF)) {
        append(text)
    }
}

// Wall-clock time a turn was resolved, e.g. "18:05h", for the log separators.
private fun formatLogTime(timestampMillis: Long): String =
    SimpleDateFormat("HH:mm'h'", Locale.getDefault()).format(Date(timestampMillis))

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
                val firstTurnTime = System.currentTimeMillis()
                val secondTurnTime = firstTurnTime + 3_600_000L
                buildCombatLogEntries(
                    actions =
                        listOf(
                            CombatTestFactory.skillUsedAction(skillName = "Smite", createdAt = firstTurnTime),
                            CombatTestFactory.skillUsedAction(
                                actor = CombatActor.ENEMY,
                                skillName = "Insomnia",
                                createdAt = secondTurnTime,
                            ),
                            CombatTestFactory.skillUsedAction(skillName = "Wind", createdAt = secondTurnTime),
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
