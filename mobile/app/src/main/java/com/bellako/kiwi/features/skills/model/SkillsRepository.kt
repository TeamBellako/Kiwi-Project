package com.bellako.kiwi.features.skills.model

import com.bellako.kiwi.features.skills.data.EquipSkillDTO
import com.bellako.kiwi.features.skills.data.SkillDataMapper
import com.bellako.kiwi.features.skills.data.SkillDomain

class SkillsRepository(
    private val api: ISkillsAPI,
) {
    suspend fun getAllSkills(): List<SkillDomain> = api.getAllSkills().map { SkillDataMapper.toDomain(it) }

    suspend fun giveSkill(skillId: Long): SkillDomain = SkillDataMapper.toDomain(api.giveSkill(skillId))

    suspend fun levelUpSkill(skillId: Long): SkillDomain = SkillDataMapper.toDomain(api.levelUpSkill(skillId))

    suspend fun putOnCooldown(skillId: Long): SkillDomain = SkillDataMapper.toDomain(api.putOnCooldown(skillId))

    suspend fun removeCooldown(skillId: Long): SkillDomain = SkillDataMapper.toDomain(api.removeCooldown(skillId))

    suspend fun equipSkill(
        skillId: Long,
        equipSkillDTO: EquipSkillDTO,
    ): SkillDomain = SkillDataMapper.toDomain(api.equipSkill(skillId, equipSkillDTO))

    suspend fun unequipSkill(skillId: Long): SkillDomain = SkillDataMapper.toDomain(api.unequipSkill(skillId))
}
