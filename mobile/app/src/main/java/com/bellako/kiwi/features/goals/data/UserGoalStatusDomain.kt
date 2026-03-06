package com.bellako.kiwi.features.goals.data

data class UserGoalStatusDomain(
    override val id: Long,
    val goalId: Long,
    override val name: String,
    override val target: Int,
    override val action: String,
    override val type: GoalType,
    override val category: GoalCategory,
    val status: GoalStatus,
    override val reward: Int,
    val date: String = "",
    val value: Int = 0,
) : IGoal
