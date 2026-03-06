package com.bellako.kiwi.features.goals.data

interface IGoal {
    val id: Long
    val name: String
    val target: Int
    val action: String
    val type: GoalType
    val category: GoalCategory
    val reward: Int
}
