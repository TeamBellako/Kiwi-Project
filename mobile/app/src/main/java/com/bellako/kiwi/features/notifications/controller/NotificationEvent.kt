package com.bellako.kiwi.features.notifications.controller

import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.screens.GoalNotificationType

sealed class NotificationEvent {
    data class Quest(
        val questId: Long,
    ) : NotificationEvent()

    data class Goal(
        val type: GoalNotificationType,
        val goals: List<IGoal>,
    ) : NotificationEvent()

    data class Generic(
        val message: String,
    ) : NotificationEvent()
}
