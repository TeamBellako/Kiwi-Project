package com.bellako.kiwi.features.goals.data

object SuggestedGoalDataMapper {
    fun toDomain(dto: SuggestedGoalDTO): SuggestedGoalDomain =
        SuggestedGoalDomain(
            id = dto.id,
            target = dto.target.toInt(),
            action = dto.action,
            type = stringToType(dto.type),
            category = stringToCategory(dto.category),
            reward = dto.reward,
        )

    fun toDTO(domain: SuggestedGoalDomain): SuggestedGoalDTO =
        SuggestedGoalDTO(
            id = domain.id,
            target = domain.target.toLong(),
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            reward = domain.reward,
        )

    fun toGoalState(
        domain: SuggestedGoalDomain,
        date: String,
    ): GoalState =
        GoalState(
            id = domain.id,
            target = domain.target,
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = "IN_PROGRESS",
            reward = domain.reward,
            date = date,
        )

    private fun stringToType(type: String?): GoalType =
        when (type?.uppercase()) {
            "EXERCISE" -> GoalType.EXERCISE
            "SLEEP" -> GoalType.SLEEP
            "MEDITATION" -> GoalType.MEDITATION
            "NUTRITION" -> GoalType.NUTRITION
            "PRODUCTIVITY" -> GoalType.PRODUCTIVITY
            else -> GoalType.EXERCISE
        }

    private fun stringToCategory(category: String?): GoalCategory =
        when (category?.uppercase()) {
            "DAILY_CHALLENGES" -> GoalCategory.DAILY_CHALLENGES
            "APP_USAGE" -> GoalCategory.APP_USAGE
            else -> GoalCategory.DAILY_CHALLENGES
        }

    private fun categoryToString(category: GoalCategory): String =
        when (category) {
            GoalCategory.DAILY_CHALLENGES -> "DAILY_CHALLENGES"
            GoalCategory.APP_USAGE -> "APP_USAGE"
        }

    private fun typeToString(type: GoalType): String =
        when (type) {
            GoalType.EXERCISE -> "EXERCISE"
            GoalType.SLEEP -> "SLEEP"
            GoalType.MEDITATION -> "MEDITATION"
            GoalType.NUTRITION -> "NUTRITION"
            GoalType.PRODUCTIVITY -> "PRODUCTIVITY"
        }
}
