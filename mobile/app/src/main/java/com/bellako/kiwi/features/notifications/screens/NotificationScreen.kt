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
import androidx.compose.ui.platform.LocalContext
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.goals.screens.GoalsNotification
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.screens.QuestNotification
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
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
    onQuestClick: (QuestNotificationType, QuestDomain, Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentEvent by remember { mutableStateOf<NotificationEvent?>(null) }
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(currentEvent) {
        val event = currentEvent ?: return@LaunchedEffect

        when (event) {
            is NotificationEvent.Goal -> {
                // TODO
            }

            is NotificationEvent.Quest -> {
                when (event.type) {
                    QuestNotificationType.NEW ->
                        AudioManager.playSFX(context, R.raw.snd_ui_newquest)

                    QuestNotificationType.QUEST_COMPLETED,
                    QuestNotificationType.SUBQUEST_COMPLETED,
                    ->
                        AudioManager.playSFX(context, R.raw.snd_ui_questcompleted)

                    QuestNotificationType.SUBQUEST_FAILED ->
                        AudioManager.playSFX(context, R.raw.snd_ui_questfailed)
                }
            }

            is NotificationEvent.Generic -> {
                // TODO
            }
        }
    }

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
                    when (event) {
                        is NotificationEvent.Goal -> {
                            GoalNotificationContent(
                                event,
                                onGoalClick,
                            )
                        }

                        is NotificationEvent.Quest -> {
                            QuestNotificationContent(
                                event = event,
                                onQuestClick = onQuestClick,
                            )
                        }

                        is NotificationEvent.Generic -> {
                            // TODO
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalNotificationContent(
    event: NotificationEvent.Goal,
    onGoalClick: (GoalNotificationType, List<IGoal>) -> Unit,
) {
    GoalsNotification(
        type = event.type,
        goals = event.goals,
        onClick = {
            onGoalClick(event.type, event.goals)
        },
    )
}

@Composable
fun QuestNotificationContent(
    event: NotificationEvent.Quest,
    onQuestClick: (QuestNotificationType, QuestDomain, Int?) -> Unit,
) {
    QuestNotification(
        name =
            when (event.type) {
                QuestNotificationType.NEW -> event.quest.name
                QuestNotificationType.QUEST_COMPLETED -> event.quest.name
                QuestNotificationType.SUBQUEST_COMPLETED,
                QuestNotificationType.SUBQUEST_FAILED,
                -> {
                    event.quest.subquests
                        .firstOrNull { it.id == event.subquestId }
                        ?.name ?: event.quest.name
                }
            },
        questIcon = event.quest.icon,
        type = event.type,
        onClick = {
            onQuestClick(event.type, event.quest, event.subquestId)
        },
    )
}
