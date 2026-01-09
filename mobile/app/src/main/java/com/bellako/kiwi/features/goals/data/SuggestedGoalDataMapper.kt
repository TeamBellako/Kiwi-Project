package com.bellako.kiwi.features.goals.data

object SuggestedGoalDataMapper {
    fun toDomain(dto: SuggestedGoalDTO): SuggestedGoalDomain =
        SuggestedGoalDomain(
            id = dto.id,
            objective = dto.objective.toInt(),
            description = dto.description,
            type = dto.type,
            category = stringToCategory(dto.category),
            points = dto.points,
        )

    fun toDTO(domain: SuggestedGoalDomain): SuggestedGoalDTO =
        SuggestedGoalDTO(
            id = domain.id,
            objective = domain.objective.toInt(),
            description = domain.description,
            type = domain.type,
            category = categoryToString(domain.category),
            points = domain.points,
        )

    fun toGoalState(
        domain: SuggestedGoalDomain,
        date: String,
    ): GoalState =
        GoalState(
            id = domain.id,
            objective = domain.objective.toInt(),
            description = domain.description,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = "IN_PROGRESS",
            points = domain.points,
            date = date,
        )

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
