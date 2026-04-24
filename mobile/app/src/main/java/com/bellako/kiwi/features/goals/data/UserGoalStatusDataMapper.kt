package com.bellako.kiwi.features.goals.data

object UserGoalStatusDataMapper {
    fun toDomain(dto: UserGoalStatusDTO): UserGoalStatusDomain =
        UserGoalStatusDomain(
            id = dto.id,
            goalId = dto.goalId,
            name = dto.name,
            target = dto.target,
            action = dto.action,
            type = stringToType(dto.type),
            category = stringToCategory(dto.category),
            status = stringToStatus(dto.status),
            reward = dto.reward,
            date = dto.date,
            value = dto.value,
            onCompletedEvent = dto.onCompletedEvent,
            onCompletedEntityId = dto.onCompletedEntityId,
        )

    fun toDomain(state: GoalState): UserGoalStatusDomain =
        UserGoalStatusDomain(
            id = state.id,
            goalId = state.goalId,
            name = state.name,
            target = state.target,
            action = state.action,
            type = stringToType(state.type),
            category = stringToCategory(state.category),
            status = stringToStatus(state.status),
            reward = state.reward,
            date = state.date,
            value = state.value,
            onCompletedEvent = state.onCompletedEvent,
            onCompletedEntityId = state.onCompletedEntityId,
        )

    fun toState(domain: UserGoalStatusDomain): GoalState =
        GoalState(
            id = domain.id,
            goalId = domain.goalId,
            name = domain.name,
            target = domain.target,
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            reward = domain.reward,
            value = domain.value,
            date = domain.date,
            onCompletedEvent = domain.onCompletedEvent,
            onCompletedEntityId = domain.onCompletedEntityId,
        )

    fun toState(dto: UserGoalStatusDTO): GoalState = toState(toDomain(dto))

    fun toDTO(domain: UserGoalStatusDomain): UserGoalStatusDTO =
        UserGoalStatusDTO(
            id = domain.id,
            goalId = domain.goalId,
            name = domain.name,
            target = domain.target,
            action = domain.action,
            type = typeToString(domain.type),
            category = categoryToString(domain.category),
            status = statusToString(domain.status),
            reward = domain.reward,
            date = domain.date,
            value = domain.value,
            onCompletedEvent = domain.onCompletedEvent,
            onCompletedEntityId = domain.onCompletedEntityId,
        )

    fun toDTO(state: GoalState): UserGoalStatusDTO = toDTO(toDomain(state))

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
