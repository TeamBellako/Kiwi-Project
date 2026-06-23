package com.bellako.kiwi.features.quests.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.rememberTextWidthScale
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.SubquestDomain
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

private const val SUBQUEST_TITLE_LINE_HEIGHT = 1.2f

// ACTIVE QUESTS
@Composable
fun Quest(
    quest: QuestDomain,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    val kiwiColors = LocalKiwiColors.current
    var expanded by remember(isExpanded) {
        mutableStateOf(isExpanded)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // QUEST HEADER
        Box(
            modifier =
                Modifier
                    .clickable { expanded = !expanded }
                    .zIndex(1f),
        ) {
            // Background image
            Kiwi_Image(
                if (expanded) {
                    R.drawable.quest_bg_selected
                } else {
                    R.drawable.quest_bg
                },
                "Quest background",
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
                        questIcon(quest.icon),
                        "Quest Icon",
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(65.dp)),
                    )
                }

                // TEXT
                Column {
                    Kiwi_H1(
                        KiwiTextArguments(
                            text = quest.name,
                        ),
                    )

                    Kiwi_P2(
                        KiwiTextArguments(
                            color = kiwiColors.color7A,
                            text = quest.description,
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

        // SUBQUEST LIST (EXPANDED)
        AnimatedVisibility(visible = expanded) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-getResponsiveSizeHeight(11.dp)))
                        .zIndex(0f),
            ) {
                // Background image
                Kiwi_Image(
                    R.drawable.subquest_bg,
                    "Subquest background",
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter,
                )

                Column(
                    modifier =
                        Modifier
                            .padding(
                                vertical = getResponsiveSizeHeight(Spacing.medium),
                                horizontal = getResponsiveSizeHeight(28.dp),
                            ),
                ) {
                    quest.subquests.forEachIndexed { index, subquest ->
                        Subquest(
                            subquest = subquest,
                            isLast = index == quest.subquests.lastIndex,
                        )
                    }

                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.medium))
                }
            }
        }
    }
}

@Composable
fun Subquest(
    subquest: SubquestDomain,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = getResponsiveSizeHeight(Spacing.xSmall)),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        // STATUS ICON
        Column(
            verticalArrangement = Arrangement.Top,
        ) {
            Kiwi_Image(
                subquestStatusIcon(subquest.status),
                "Subquest Status Icon",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(20.dp)),
            )
            if (!isLast) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(getResponsiveSizeHeight(3.dp))
                            .height(getResponsiveSizeHeight(40.dp))
                            .padding(top = getResponsiveSizeHeight(3.dp))
                            .background(kiwiColors.color0A),
                )
            }
        }

        // TEXT
        Column {
            Box(
                modifier = Modifier.height(getResponsiveSizeHeight(20.dp)),
                contentAlignment = Alignment.CenterStart,
            ) {
                SubquestTitle(subquest.name)
            }

            Kiwi_Label1(
                KiwiTextArguments(
                    text = subquestStatusText(subquest.status),
                    color = kiwiColors.color7A,
                ),
            )
        }
    }
}

// Subquest title rendered with font padding disabled and a centered line height so the
// glyph sits at the true vertical center, aligning it with the status icon's y-axis.
@Composable
private fun SubquestTitle(text: String) {
    val scale = rememberTextWidthScale()
    val style = MaterialTheme.typography.labelLarge

    Text(
        text = text,
        color = Color.White,
        style =
            style.copy(
                fontSize = (style.fontSize.value * scale).sp,
                lineHeight = (style.fontSize.value * scale * SUBQUEST_TITLE_LINE_HEIGHT).sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle =
                    LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None,
                    ),
            ),
    )
}

// HELPERS
@DrawableRes
fun questIcon(questIcon: Int): Int =
    when (questIcon) {
        1 -> R.drawable.ic_quest_comet
        2 -> R.drawable.ic_quest_star
        else -> R.drawable.ic_quest_star
    }

@DrawableRes
private fun subquestStatusIcon(status: SubquestStatus): Int =
    when (status) {
        SubquestStatus.COMPLETED -> R.drawable.ic_dropdown_tick
        SubquestStatus.FAILED -> R.drawable.ic_dropdown_fail
        SubquestStatus.ACTIVE -> R.drawable.ic_dropdown_location
        SubquestStatus.LOCKED -> R.drawable.ic_dropdown_lock
    }

private fun subquestStatusText(status: SubquestStatus): String =
    when (status) {
        SubquestStatus.COMPLETED -> "Completed"
        SubquestStatus.FAILED -> "Failed"
        SubquestStatus.ACTIVE -> "Current Objective"
        SubquestStatus.LOCKED -> "Upcoming"
    }
