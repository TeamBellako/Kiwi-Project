package com.bellako.kiwi.features.goals.data

data class SuggestedGoalDomain(
    override val id: String,
    override val objective: Int,
    override val description: String,
    override val type: GoalType,
    override val category: GoalCategory,
    override val points: Int,
) : IGoal
