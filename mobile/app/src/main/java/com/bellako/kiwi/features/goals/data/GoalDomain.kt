package com.bellako.kiwi.features.goals.data

data class GoalDomain(
    override val id: Long,
    override val name: String,
    override val target: Int,
    override val action: String,
    override val type: GoalType,
    override val category: GoalCategory,
    override val reward: Int,
) : IGoal {
    override fun resolveAction(): String {
        val regex = Regex("@[a-zA-Z0-9_]+")
        var result = action

        regex.findAll(action).forEach { match ->
            result = result.replace(match.value, target.toString())
        }

        return result
    }
}
