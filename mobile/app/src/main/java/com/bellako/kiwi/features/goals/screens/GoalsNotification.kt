package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

const val ICON_GOAL_WEIGHT = 0.25f

@Composable
fun GoalsNotification(
    type: GoalNotificationType,
    goals: List<IGoal>,
    onClick: () -> Unit = {},
) {
    val header =
        if (type == GoalNotificationType.NEW) {
            "New Daily Goals!"
        } else {
            "Yesterday's Challenge"
        }
    val body =
        if (type == GoalNotificationType.NEW) {
            "What can you accomplish today?"
        } else {
            "Let's check what you accomplished"
        }

    val kiwiColor = LocalKiwiColors.current

    Column {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .clickable { onClick() },
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
                        Modifier.weight(ICON_GOAL_WEIGHT),
                ) {
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[0].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeWidth(30.dp))
                                .offset(x = getResponsiveSizeWidth(-12.5.dp), y = getResponsiveSizeHeight(-2.5.dp)),
                        alignment = Alignment.Center,
                    )
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[1].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeWidth(30.dp))
                                .offset(x = getResponsiveSizeWidth(6.5.dp), y = getResponsiveSizeHeight(2.5.dp)),
                        alignment = Alignment.Center,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .weight(1f - ICON_GOAL_WEIGHT)
                            .padding(start = getResponsiveSizeHeight(Spacing.medium)),
                ) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            header,
                            color = kiwiColor.colorF,
                        ),
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.xSmall))
                    Kiwi_Label3(
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

enum class GoalNotificationType {
    NEW,
    YESTERDAY,
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsNotification_Card_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
            ) {
                GoalsNotification(
                    type = GoalNotificationType.NEW,
                    goals =
                        listOf(
                            GoalDomain(
                                1,
                                2,
                                "Programa el modal lo mejor que sepas",
                                GoalType.PRODUCTIVITY,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.COMPLETED,
                                1000,
                                value = 2,
                            ),
                            GoalDomain(
                                2,
                                10,
                                "Esto está fuera de tu alcance",
                                GoalType.PRODUCTIVITY,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.NOT_COMPLETED,
                                1000,
                                value = 5,
                            ),
                        ),
                )
            }
        }
    }
}
