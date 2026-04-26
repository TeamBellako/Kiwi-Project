package com.bellako.kiwi.features.combat.data

object CombatDataMapper {
    fun toDomain(dto: CombatDTO): CombatDomain =
        CombatDomain(
            id = dto.id,
            combatConfigId = dto.combatConfigId,
            turnNumber = dto.turnNumber,
            endsAt = dto.endsAt,
            combatStatus = enumValueOf<CombatGeneralStatus>(dto.combatStatus),
            enemyName = dto.enemyName,
            enemySprite = dto.enemySprite,
            user = toDomain(dto.user),
            enemy = toDomain(dto.enemy),
            log = dto.log.map { toDomain(it) },
        )

    fun toDomain(dto: CombatTurnResultDTO): CombatTurnResultDomain =
        CombatTurnResultDomain(
            combatId = dto.combatId,
            turnNumber = dto.turnNumber,
            actions = dto.actions.map { toDomain(it) },
            combatStatus = enumValueOf<CombatGeneralStatus>(dto.combatStatus),
            onCompletedEvent = dto.onCompletedEvent,
            onCompletedEntityId = dto.onCompletedEntityId,
        )

    fun toDomain(dto: CombatActorDTO): CombatActorDomain =
        CombatActorDomain(
            stats = toDomain(dto.stats),
            elementalMultipliers = dto.elementalMultipliers.map { toDomain(it) },
            statusResistances = dto.statusResistances.map { toDomain(it) },
            activeStatus = dto.activeStatus.map { toDomain(it) },
        )

    fun toDomain(dto: CombatStatsDTO): CombatStatsDomain =
        CombatStatsDomain(
            currentHp = dto.currentHp,
            maxHp = dto.maxHp,
            patk = dto.patk,
            matk = dto.matk,
            pdef = dto.pdef,
            mdef = dto.mdef,
            acc = dto.acc,
            eva = dto.eva,
            lck = dto.lck,
        )

    fun toDomain(dto: ElementMultiplierDTO): ElementMultiplierDomain =
        ElementMultiplierDomain(
            elementId = dto.elementId,
            name = dto.name,
            icon = dto.icon,
            description = dto.description,
            multiplier = dto.multiplier,
        )

    fun toDomain(dto: StatusResistanceDTO): StatusResistanceDomain =
        StatusResistanceDomain(
            stateId = dto.stateId,
            stateName = dto.stateName,
            stateDescription = dto.stateDescription,
            stateIcon = dto.stateIcon,
            resistance = dto.resistance,
        )

    fun toDomain(dto: CombatActiveStatusDTO): CombatActiveStatusDomain =
        CombatActiveStatusDomain(
            stateId = dto.stateId,
            name = dto.name,
            icon = dto.icon,
            description = dto.description,
            remainingTurns = dto.remainingTurns,
            value = dto.value,
        )

    fun toDomain(dto: CombatActionDTO): CombatActionDomain =
        CombatActionDomain(
            actor = enumValueOf<CombatActor>(dto.actor),
            actionType = enumValueOf<CombatActionType>(dto.actionType),
            stateName = dto.stateName,
            stateId = dto.stateId,
            stateEffectValue = dto.stateEffectValue,
            blockedSkills = dto.blockedSkills,
            skillName = dto.skillName,
            skillEffectsResults = dto.skillEffectsResults.map { toDomain(it) },
        )

    fun toDomain(dto: SkillEffectResultDTO): SkillEffectResultDomain =
        SkillEffectResultDomain(
            typeResult = enumValueOf<SkillEffectResultType>(dto.typeResult),
            target = enumValueOf<CombatActor>(dto.target),
            statAffected = dto.statAffected,
            value = dto.value,
            critic = dto.critic,
            appliedStatus = dto.appliedStatus?.let { toDomain(it) },
        )

    fun toDTO(domain: CombatDomain): CombatDTO =
        CombatDTO(
            id = domain.id,
            combatConfigId = domain.combatConfigId,
            turnNumber = domain.turnNumber,
            endsAt = domain.endsAt,
            combatStatus = domain.combatStatus.name,
            enemyName = domain.enemyName,
            enemySprite = domain.enemySprite,
            user = toDTO(domain.user),
            enemy = toDTO(domain.enemy),
            log = domain.log.map { toDTO(it) },
        )

    fun toDTO(domain: CombatTurnResultDomain): CombatTurnResultDTO =
        CombatTurnResultDTO(
            combatId = domain.combatId,
            turnNumber = domain.turnNumber,
            actions = domain.actions.map { toDTO(it) },
            combatStatus = domain.combatStatus.name,
            onCompletedEvent = domain.onCompletedEvent,
            onCompletedEntityId = domain.onCompletedEntityId,
        )

    fun toDTO(domain: CombatActorDomain): CombatActorDTO =
        CombatActorDTO(
            stats = toDTO(domain.stats),
            elementalMultipliers = domain.elementalMultipliers.map { toDTO(it) },
            statusResistances = domain.statusResistances.map { toDTO(it) },
            activeStatus = domain.activeStatus.map { toDTO(it) },
        )

    fun toDTO(domain: CombatStatsDomain): CombatStatsDTO =
        CombatStatsDTO(
            currentHp = domain.currentHp,
            maxHp = domain.maxHp,
            patk = domain.patk,
            matk = domain.matk,
            pdef = domain.pdef,
            mdef = domain.mdef,
            acc = domain.acc,
            eva = domain.eva,
            lck = domain.lck,
        )

    fun toDTO(domain: ElementMultiplierDomain): ElementMultiplierDTO =
        ElementMultiplierDTO(
            elementId = domain.elementId,
            name = domain.name,
            icon = domain.icon,
            description = domain.description,
            multiplier = domain.multiplier,
        )

    fun toDTO(domain: StatusResistanceDomain): StatusResistanceDTO =
        StatusResistanceDTO(
            stateId = domain.stateId,
            stateName = domain.stateName,
            stateDescription = domain.stateDescription,
            stateIcon = domain.stateIcon,
            resistance = domain.resistance,
        )

    fun toDTO(domain: CombatActiveStatusDomain): CombatActiveStatusDTO =
        CombatActiveStatusDTO(
            stateId = domain.stateId,
            name = domain.name,
            icon = domain.icon,
            description = domain.description,
            remainingTurns = domain.remainingTurns,
            value = domain.value,
        )

    fun toDTO(domain: CombatActionDomain): CombatActionDTO =
        CombatActionDTO(
            actor = domain.actor.name,
            actionType = domain.actionType.name,
            stateName = domain.stateName,
            stateId = domain.stateId,
            stateEffectValue = domain.stateEffectValue,
            blockedSkills = domain.blockedSkills,
            skillName = domain.skillName,
            skillEffectsResults = domain.skillEffectsResults.map { toDTO(it) },
        )

    fun toDTO(domain: SkillEffectResultDomain): SkillEffectResultDTO =
        SkillEffectResultDTO(
            typeResult = domain.typeResult.name,
            target = domain.target.name,
            statAffected = domain.statAffected,
            value = domain.value,
            critic = domain.critic,
            appliedStatus = domain.appliedStatus?.let { toDTO(it) },
        )
}
