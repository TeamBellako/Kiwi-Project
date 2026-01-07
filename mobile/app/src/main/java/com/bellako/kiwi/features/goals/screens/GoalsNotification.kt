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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P3
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
fun GoalsNotification(
    type: GoalModalType,
    goals: List<GoalDomain>,
) {
    val header =
        if (type == GoalModalType.NEW) {
            "New Daily Goals!"
        } else {
            "Yesterday's Challenge"
        }
    val body =
        if (type == GoalModalType.NEW) {
            "What can you accomplish today?"
        } else {
            "Let's check what you accomplished"
        }

    val kiwiColor = LocalKiwiColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = getResponsiveSizeHeight(Spacing.large),
                    ).padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
        ) {
            Image(
                painter = painterResource(id = R.drawable.goals_notification),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.medium))
                        .padding(vertical = getResponsiveSizeHeight(Spacing.large)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier.weight(0.25f).padding(
//                            top = getResponsiveSizeHeight(Spacing.xSmall),
                            end = getResponsiveSizeHeight(Spacing.medium),
//                            bottom = getResponsiveSizeHeight(Spacing.xSmall),
                        ),
                ) {
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[0].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(30.dp))
                                .offset(x = getResponsiveSizeHeight((-4).dp)),
                        alignment = Alignment.Center,
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.small))
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[1].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(30.dp))
                                .offset(x = getResponsiveSizeHeight(14.dp)),
                        alignment = Alignment.Center,
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .weight(0.75f)
                            .padding(start = getResponsiveSizeHeight(Spacing.medium)),
                ) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            header,
                            color = kiwiColor.colorF,
                        ),
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.xSmall))
                    Kiwi_P3(
                        KiwiTextArguments(
                            body,
                            color = kiwiColor.color6,
                        ),
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsNotification_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            GoalsNotification(
                type = GoalModalType.NEW,
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
