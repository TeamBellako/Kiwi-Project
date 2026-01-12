package com.bellako.kiwi.features.goals.data

object GoalDataMapper {
    fun toDomain(dto: GoalDTO): GoalDomain =
        GoalDomain(
            id = dto.id,
            target = dto.target,
            action = dto.action,
            type = dto.type,
            category = stringToCategory(dto.category),
            status = stringToStatus(dto.status),
            reward = dto.reward,
            date = dto.date,
            value = dto.value,
        )

    fun toDomain(state: GoalState): GoalDomain =
        GoalDomain(
            id = state.id,
            target = state.target,
            action = state.action,
            type = stringToType(state.type),
            category = stringToCategory(state.category),
            status = stringToStatus(state.status),
            reward = state.reward,
            date = state.date,
            value = state.value,
        )

    fun toState(domain: GoalDomain): GoalState =
        GoalState(
            id = domain.id,
            target = domain.target,
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            reward = domain.reward,
            value = domain.value,
            date = domain.date,
        )

    fun toState(dto: GoalDTO): GoalState = toState(toDomain(dto))

    fun toDTO(domain: GoalDomain): GoalDTO =
        GoalDTO(
            id = domain.id,
            target = domain.target,
            action = domain.action,
            type = domain.type,
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            reward = domain.reward,
            date = domain.date,
            value = domain.value,
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
            "IN_PROGRESS" -> GoalStatus.IN_PROGRESS
            else -> GoalStatus.IN_PROGRESS
        }

    private fun statusToString(status: GoalStatus): String =
        when (status) {
            GoalStatus.COMPLETED -> "COMPLETED"
            GoalStatus.NOT_COMPLETED -> "NOT_COMPLETED"
            GoalStatus.IN_PROGRESS -> "IN_PROGRESS"
        }
}
