package com.bellako.kiwi.features.notifications.controller

import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.quests.data.QuestDomain
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.screen.SkillNotificationType

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

    data class Skill(
        val type: SkillNotificationType,
        val skill: SkillDomain,
    ) : NotificationEvent()

    data class Generic(
        val message: String,
    ) : NotificationEvent()
}
