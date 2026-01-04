package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalModalType
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun GoalsModal(
    goalModalType: GoalModalType,
    goals: List<GoalDomain>,
) {
    val header =
        if (goalModalType == GoalModalType.NEW) {
            "New Daily Challenges!"
        } else {
            "Yesterday's Challenge"
        }

    val body =
        if (goalModalType == GoalModalType.NEW) {
            "These are the new challenges that await for you today"
        } else {
            "You didn't checked these goals yesterday.\n Did you completed them?"
        }
    val kiwiColor = LocalKiwiColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.padding(top = getResponsiveSizeHeight(24.dp)).padding(horizontal = getResponsiveSizeHeight(24.dp)),
        ) {
            Image(
                painter = painterResource(id = R.drawable.goals_modal),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Kiwi_H1(
                    KiwiTextArguments(
                        header,
                        TextAlign.Center,
                        bold = true,
                        modifier =
                            Modifier.padding(
                                top = getResponsiveSizeHeight(Spacing.medium),
                                bottom = getResponsiveSizeHeight(Spacing.small),
                            ),
                    ),
                )
                Kiwi_Spacer(Spacing.medium)
                Kiwi_P2(
                    KiwiTextArguments(
                        body,
                        TextAlign.Center,
                        color = kiwiColor.color6,
                        modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.medium)),
                    ),
                )
                Kiwi_Spacer(Spacing.large)
//                Column(modifier = Modifier.padding(horizontal = getResponsiveSizeHeight(Spacing.medium)))
//                {
                for (goal in goals) {
                    GoalComponent(goal)
                    Kiwi_Spacer(Spacing.small)
                }
//                }
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 0.dp),
            horizontalArrangement =
                androidx.compose.foundation.layout.Arrangement
                    .spacedBy(12.dp),
        ) {
            Kiwi_FixedSizeButton(
                textArguments =
                    KiwiTextArguments(
                        "Modify",
                        color = kiwiColor.colorF,
                        bold = false,
                    ),
                color = kiwiColor.color7D,
                modifier =
                    Modifier
                        .weight(0.5f),
                onClick = {},
            )
            Kiwi_FixedSizeButton(
                textArguments =
                    KiwiTextArguments(
                        if (goalModalType == GoalModalType.NEW) "Let's go!" else "Done",
                        color = kiwiColor.colorF,
                        bold = false,
                    ),
                color = kiwiColor.color8,
                modifier =
                    Modifier
                        .weight(0.5f),
                onClick = {},
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsModal_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            GoalsModal(
                goalModalType = GoalModalType.NEW,
                goals =
                    listOf(
                        GoalDomain(
                            "1",
                            "Hacer el modal",
                            "Programa el modal lo mejor que sepas",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.COMPLETED,
                            1000,
                            progress = 1f,
                        ),
                        GoalDomain(
                            "2",
                            "Haz que sea bonito",
                            "Esto está fuera de tu alcance",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.NOT_COMPLETED,
                            1000,
                            progress = 0.5f,
                        ),
                    ),
            )
        }
    }
}
