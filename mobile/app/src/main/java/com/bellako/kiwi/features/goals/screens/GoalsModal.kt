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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.modals.WIPPopUpScreen
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.launch

@Composable
@Suppress("MagicNumber", "LongMethod")
fun GoalsModal(
    goalModalType: GoalNotificationType,
    goals: List<IGoal>,
    goalsViewModel: IGoalsViewModel,
    onDismiss: () -> Unit = {},
) {
    var showWorkInProgressPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val header =
        if (goalModalType == GoalNotificationType.NEW) {
            "New Daily Challenges!"
        } else {
            "Yesterday's Challenge"
        }

    val body =
        if (goalModalType == GoalNotificationType.NEW) {
            "These are the new challenges that await for you today"
        } else {
            "You didn't checked these goals yesterday.\n Did you completed them?"
        }
    val kiwiColor = LocalKiwiColors.current

    @Suppress("MagicNumber")
    val buttonPercentage = 0.5f

    // Box con fondo difuminado
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeHeight(24.dp)),
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
                            fontWeight = FontWeight.Bold,
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
                    for (goal in goals) {
                        GoalComponent(
                            goal,
                            goalsViewModel,
                            Modifier.padding(horizontal = getResponsiveSizeWidth(10.dp)),
                        )
                        Kiwi_Spacer(Spacing.small)
                    }
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
                        ),
                    color = kiwiColor.color7D,
                    modifier =
                        Modifier
                            .weight(buttonPercentage),
                    onClick = { showWorkInProgressPopup = true },
                )
                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            if (goalModalType == GoalNotificationType.NEW) "Let's go!" else "Done",
                            color = kiwiColor.colorF,
                        ),
                    color = kiwiColor.color8,
                    modifier =
                        Modifier
                            .weight(buttonPercentage),
                    onClick = {
                        coroutineScope.launch {
                            var anyCompleted = false
                            if (goalModalType == GoalNotificationType.YESTERDAY) {
                                for (goal in goals) {
                                    if (goal is UserGoalStatusDomain) {
                                        if (goal.value == goal.target) {
                                            goalsViewModel.completeGoal(goalId = goal.id)
                                            anyCompleted = true
                                        } else {
                                            goalsViewModel.uncompleteGoal(goalId = goal.id)
                                        }
                                    }
                                }
                                goalsViewModel.invalidateGoalsInProgressCache()
                                if (anyCompleted) {
                                    EventBus.emitEvent(
                                        EventType.MAP_CONTENT_AVAILABLE,
                                        EventPayload.EmptyPayload(),
                                    )
                                }
                            }
                            onDismiss()
                        }
                    },
                )
            }
        }
    }

    // Popup de "Work in progress"
    if (showWorkInProgressPopup) {
        WIPPopUpScreen(onDismiss = { showWorkInProgressPopup = false })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsModal_Preview(goalsViewModel: GoalsFakeViewModel = GoalsFakeViewModel()) {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            GoalsModal(
                goalModalType = GoalNotificationType.YESTERDAY,
                goals =
                    listOf(
                        UserGoalStatusDomain(
                            1,
                            1,
                            "Programa el modal lo mejor que sepas",
                            1000,
                            "Programa el modal lo mejor que sepas",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.COMPLETED,
                            1000,
                            value = 5,
                            onCompletedEvent = "_",
                            onCompletedEntityId = 0,
                        ),
                        UserGoalStatusDomain(
                            2,
                            2,
                            "Esto está fuera de tu alcance",
                            1000,
                            "Esto está fuera de tu alcance",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.NOT_COMPLETED,
                            1000,
                            value = 5,
                            onCompletedEvent = "_",
                            onCompletedEntityId = 0,
                        ),
                    ),
                goalsViewModel = goalsViewModel,
            )
        }
    }
}
