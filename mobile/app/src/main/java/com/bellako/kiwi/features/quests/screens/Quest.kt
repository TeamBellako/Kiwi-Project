package com.bellako.kiwi.features.quests.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.data.QuestStatus
import com.bellako.kiwi.features.quests.data.SubquestDomain
import com.bellako.kiwi.features.quests.data.SubquestStatus
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun Quest(
    quest: QuestDomain,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    val kiwiColors = LocalKiwiColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // QUEST HEADER
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .zIndex(1f),
        ) {
            // Background image
            Kiwi_Image(
                R.drawable.card_goals_selected,
                "Quest background",
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
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
                        questIconFor(quest),
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
                        .zIndex(0f)
            ) {
                // Background image
                Kiwi_Image(
                    R.drawable.dropdown_bg,
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
                            )
                ) {
                    quest.subquests.forEachIndexed { index, subquest ->
                        SubquestItem(
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
fun SubquestItem(
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
            Kiwi_Label1(
                KiwiTextArguments(
                    text = subquest.name,
                ),
            )

            Kiwi_Label1(
                KiwiTextArguments(
                    text = subquestStatusText(subquest.status),
                    color = kiwiColors.color7A,
                ),
            )
        }
    }
}

// HELPERS
@DrawableRes
fun questIconFor(quest: QuestDomain): Int =
    when (quest.status) { // TODO cambiar para usar enum de la BBDD
        QuestStatus.ACTIVE -> R.drawable.ic_goal_star
        QuestStatus.COMPLETED -> R.drawable.ic_goal_star
    }

@DrawableRes
fun subquestStatusIcon(status: SubquestStatus): Int =
    when (status) {
        SubquestStatus.COMPLETED -> R.drawable.ic_dropdown_tick
        SubquestStatus.FAILED -> R.drawable.ic_daily_challenges_plus // TODO icono fail
        SubquestStatus.ACTIVE -> R.drawable.ic_dropdown_location
        SubquestStatus.LOCKED -> R.drawable.ic_dropdown_lock
    }

fun subquestStatusText(status: SubquestStatus): String =
    when (status) {
        SubquestStatus.COMPLETED -> "Completed"
        SubquestStatus.FAILED -> "Failed"
        SubquestStatus.ACTIVE -> "Current Objective"
        SubquestStatus.LOCKED -> "Upcoming"
    }
