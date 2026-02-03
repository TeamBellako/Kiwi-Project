package com.bellako.kiwi.features.notifications.controller

import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.screens.QuestNotificationType

sealed class NotificationEvent {
    data class Quest(
        val type: QuestNotificationType,
        val quest: QuestDomain,
        val subquestId: Int? = null,
    ) : NotificationEvent()

    data class Goal(
        val type: GoalNotificationType,
        val goals: List<IGoal>,
    ) : NotificationEvent()

    data class Generic(
        val message: String,
    ) : NotificationEvent()
}
