package com.bellako.kiwi.features.goals.data

data class GoalDomain(
    override val id: String,
    override val objective: Int,
    override val description: String,
    override val type: GoalType,
    override val category: GoalCategory,
    val status: GoalStatus,
    override val points: Int,
    val progress: Float = 0f,
    val date: String = "",
) : IGoal
