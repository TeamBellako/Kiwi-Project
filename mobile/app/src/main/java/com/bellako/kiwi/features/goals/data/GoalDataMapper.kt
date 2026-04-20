package com.bellako.kiwi.features.goals.data

object GoalDataMapper {
    fun toDomain(dto: GoalDTO): GoalDomain =
        GoalDomain(
            id = dto.id,
            name = dto.name,
            target = dto.target,
            action = dto.action,
            type = stringToType(dto.type),
            category = stringToCategory(dto.category),
            reward = dto.reward,
        )

    fun toDTO(domain: GoalDomain): GoalDTO =
        GoalDTO(
            id = domain.id,
            name = domain.name,
            target = domain.target,
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            reward = domain.reward,
        )

    fun toUserGoalStatusState(
        domain: GoalDomain,
        date: String,
    ): GoalState =
        GoalState(
            id = 0,
            goalId = domain.id,
            name = domain.name,
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

    private fun typeToString(type: GoalType): String =
        when (type) {
            GoalType.EXERCISE -> "EXERCISE"
            GoalType.SLEEP -> "SLEEP"
            GoalType.MEDITATION -> "MEDITATION"
            GoalType.NUTRITION -> "NUTRITION"
            GoalType.PRODUCTIVITY -> "PRODUCTIVITY"
        }

    private fun stringToCategory(category: String?): GoalCategory =
        when (category?.uppercase()) {
            "DAILY_CHALLENGES" -> GoalCategory.DAILY_CHALLENGES
            "APP_USAGE" -> GoalCategory.APP_USAGE
            "SKILL" -> GoalCategory.SKILL
            else -> GoalCategory.DAILY_CHALLENGES
        }

    private fun categoryToString(category: GoalCategory): String =
        when (category) {
            GoalCategory.DAILY_CHALLENGES -> "DAILY_CHALLENGES"
            GoalCategory.APP_USAGE -> "APP_USAGE"
            GoalCategory.SKILL -> "SKILL"
        }
}
