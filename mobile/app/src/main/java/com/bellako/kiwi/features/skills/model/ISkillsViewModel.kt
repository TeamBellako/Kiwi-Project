package com.bellako.kiwi.features.skills.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.data.SkillsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

sealed class SkillNotificationEvent {
    data class SkillGiven(
        val skill: SkillDomain,
    ) : SkillNotificationEvent()

    data class SkillLevelUp(
        val skill: SkillDomain,
    ) : SkillNotificationEvent()

    data class SkillCooldownFinished(
        val skill: SkillDomain,
    ) : SkillNotificationEvent()
}

interface ISkillsViewModel : IBaseViewModel<SkillsState> {
    fun getNotifications(): SharedFlow<SkillNotificationEvent>

    suspend fun notifySkillGiven(skill: SkillDomain)

    suspend fun notifyCooldownFinished(skill: SkillDomain)

    fun loadAllSkills()

    fun giveSkill(skillId: Long)

    fun levelUpSkill(skillId: Long)

    fun putOnCooldown(skillId: Long)

    fun removeCooldown(skillId: Long)

    fun equipSkill(skillId: Long)

    fun unequipSkill(skillId: Long)

    fun updateGoalProgress(
        skillId: Long,
        goalId: Long,
        newProgress: Int,
    )
}
