package com.bellako.kiwi.features.notifications.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.goals.screens.GoalsNotification
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private const val SHOW_TIME_MS = 4_000L
private const val ANIM_TIME_MS = 300L
private const val GAP_TIME_MS = 250L

@Composable
fun NotificationOverlay(
    notificationManager: NotificationManager,
    onGoalClick: (GoalNotificationType, List<IGoal>) -> Unit,
    onQuestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentEvent by remember { mutableStateOf<NotificationEvent?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notificationManager.notifications.collectLatest { event ->
            currentEvent = event
            visible = true

            delay(SHOW_TIME_MS)

            visible = false
            delay(ANIM_TIME_MS)

            currentEvent = null
            delay(GAP_TIME_MS)
        }
    }

    LaunchedEffect(Unit) {
        notificationManager.dismissRequests.collect {
            if (visible) {
                visible = false
                delay(ANIM_TIME_MS)
                currentEvent = null
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.large)),
        ) {
            AnimatedVisibility(
                visible = visible,
                enter =
                    slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(ANIM_TIME_MS.toInt()),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(ANIM_TIME_MS.toInt()),
                    ),
            ) {
                currentEvent?.let { event ->
                    NotificationContent(
                        event,
                        onGoalClick,
                        onQuestClick,
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationContent(
    event: NotificationEvent,
    onGoalClick: (GoalNotificationType, List<IGoal>) -> Unit,
    onQuestClick: () -> Unit,
) {
    when (event) {
        is NotificationEvent.Goal -> {
            GoalsNotification(
                type = event.type,
                goals = event.goals,
                onClick = {
                    onGoalClick(event.type, event.goals)
                },
            )
        }

        is NotificationEvent.Quest -> {
            // TODO Quest notification UI
        }

        is NotificationEvent.Generic -> {
            // TODO Generic notification UI
        }
    }
}
