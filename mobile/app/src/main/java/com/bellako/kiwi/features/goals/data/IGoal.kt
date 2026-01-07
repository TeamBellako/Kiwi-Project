package com.bellako.kiwi.features.goals.data

interface IGoal {
    val id: String
    val objective: String
    val description: String
    val type: GoalType
    val category: GoalCategory
    val points: Int
}
