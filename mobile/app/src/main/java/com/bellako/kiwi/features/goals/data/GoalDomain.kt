package com.bellako.kiwi.features.goals.data

data class GoalDomain(
    override val id: Long,
    override val name: String,
    override val target: Int,
    override val action: String,
    override val type: GoalType,
    override val category: GoalCategory,
    override val reward: Int,
) : IGoal
