package com.bellako.kiwi.features.combat.model

import com.bellako.kiwi.features.combat.data.CombatDataMapper
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatTurnResultDomain

class CombatRepository(
    private val api: ICombatAPI,
) {
    suspend fun getActiveCombat(): CombatDomain? {
        val dto = api.getActiveCombat() ?: return null
        return CombatDataMapper.toDomain(dto)
    }

    suspend fun startOrResumeCombat(combatConfigId: Long): CombatDomain = CombatDataMapper.toDomain(api.startOrResumeCombat(combatConfigId))

    suspend fun executeTurn(
        combatId: Long,
        skillId: Long,
    ): CombatTurnResultDomain = CombatDataMapper.toDomain(api.executeTurn(combatId, skillId))

    suspend fun timeoutCombat(combatId: Long): CombatTurnResultDomain = CombatDataMapper.toDomain(api.timeoutCombat(combatId))

    suspend fun abandonCombat(combatId: Long): CombatTurnResultDomain = CombatDataMapper.toDomain(api.abandonCombat(combatId))

    suspend fun markBarkFired(
        combatId: Long,
        triggerId: Long,
    ) = api.markBarkFired(combatId, triggerId)
}
