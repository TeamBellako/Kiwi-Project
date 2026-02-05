package com.bellako.kiwi.features.goals.data

data class SuggestedGoalDomain(
    override val id: Long,
    override val target: Int,
    override val action: String,
    override val type: GoalType,
    override val category: GoalCategory,
    override val reward: Int,
) : IGoal
