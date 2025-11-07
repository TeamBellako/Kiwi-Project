package com.bellako.kiwi.features.goals.data

object GoalDataMapper {
    fun toDomain(dto: GoalDTO): GoalDomain =
        GoalDomain(
            id = dto.id,
            objective = dto.objective.toString(),
            description = dto.description,
            type = dto.type,
            category = stringToCategory(dto.category),
            status = stringToStatus(dto.status),
            points = dto.points,
        )

    fun toDomain(state: GoalState): GoalDomain =
        GoalDomain(
            id = state.id,
            objective = state.objective.toString(),
            description = state.description,
            type = stringToType(state.type),
            category = stringToCategory(state.category),
            status = stringToStatus(state.status),
            points = state.points,
        )

    fun toState(domain: GoalDomain): GoalState =
        GoalState(
            id = domain.id,
            objective = domain.objective.toLongOrNull() ?: 0L,
            description = domain.description,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            points = domain.points,
        )

    fun toState(dto: GoalDTO): GoalState = toState(toDomain(dto))

    fun toDTO(domain: GoalDomain): GoalDTO =
        GoalDTO(
            id = domain.id,
            objective = domain.objective.toLongOrNull() ?: 0L,
            description = domain.description,
            type = domain.type,
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            points = domain.points,
        )

    fun toDTO(state: GoalState): GoalDTO = toDTO(toDomain(state))

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
            else -> GoalCategory.DAILY_CHALLENGES
        }

    private fun categoryToString(category: GoalCategory): String =
        when (category) {
            GoalCategory.DAILY_CHALLENGES -> "DAILY_CHALLENGES"
            GoalCategory.APP_USAGE -> "APP_USAGE"
        }

    private fun stringToStatus(status: String?): GoalStatus =
        when (status?.uppercase()) {
            "COMPLETED" -> GoalStatus.COMPLETED
            "NOT_COMPLETED" -> GoalStatus.NOT_COMPLETED
            "REVIEW" -> GoalStatus.REVIEW
            else -> GoalStatus.REVIEW
        }

    private fun statusToString(status: GoalStatus): String =
        when (status) {
            GoalStatus.COMPLETED -> "COMPLETED"
            GoalStatus.NOT_COMPLETED -> "NOT_COMPLETED"
            GoalStatus.REVIEW -> "REVIEW"
        }
}
