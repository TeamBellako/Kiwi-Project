package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_P1
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

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

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .height(IntrinsicSize.Min)
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
                    .matchParentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .padding(start = getResponsiveSizeWidth(18.dp))
                        .width(getResponsiveSizeWidth(68.dp)),
            ) {
                Kiwi_Image(
                    painter = painterResource(id = goalIcon(goals[0].type)),
                    alt = "Goal icon",
                    colorFilter = ColorFilter.tint(kiwiColor.colorF),
                    modifier =
                        Modifier
                            .size(getResponsiveSizeWidth(30.dp))
                            .offset(x = getResponsiveSizeWidth(-9.5.dp), y = getResponsiveSizeWidth(-2.5.dp)),
                    alignment = Alignment.Center,
                )
                if (goals.size > 1) {
                    Kiwi_Image(
                        painter = painterResource(id = goalIcon(goals[1].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeWidth(30.dp))
                                .offset(x = getResponsiveSizeWidth(9.5.dp), y = getResponsiveSizeWidth(2.5.dp)),
                        alignment = Alignment.Center,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeWidth(Spacing.small)),
            ) {
                Kiwi_P1(
                    KiwiTextArguments(
                        header,
                        color = kiwiColor.colorF,
                    ),
                )
                Kiwi_Spacer(getResponsiveSizeWidth(Spacing.xSmall))
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
                                id = 1,
                                name = "Programa el modal lo mejor que sepas",
                                target = 2,
                                action = "Programa el modal lo mejor que sepas",
                                type = GoalType.PRODUCTIVITY,
                                category = GoalCategory.DAILY_CHALLENGES,
                                reward = 1000,
                                onCompletedEvent = "_",
                                onCompletedEntityId = 0,
                            ),
                            GoalDomain(
                                id = 2,
                                name = "Esto está fuera de tu alcance",
                                target = 10,
                                action = "Esto está fuera de tu alcance",
                                type = GoalType.PRODUCTIVITY,
                                category = GoalCategory.DAILY_CHALLENGES,
                                reward = 1000,
                                onCompletedEvent = "_",
                                onCompletedEntityId = 0,
                            ),
                        ),
                )
            }
        }
    }
}
