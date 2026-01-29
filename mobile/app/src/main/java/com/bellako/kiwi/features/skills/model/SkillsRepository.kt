package com.bellako.kiwi.features.skills.model

import com.bellako.kiwi.features.skills.data.EquipSkillDTO
import com.bellako.kiwi.features.skills.data.SkillDTO

class SkillsRepository(
    private val api: ISkillsAPI,
) {
    suspend fun getAllSkills(): List<SkillDTO> = api.getAllSkills()

    suspend fun giveSkill(skillId: Long): SkillDTO = api.giveSkill(skillId)

    suspend fun levelUpSkill(skillId: Long): SkillDTO = api.levelUpSkill(skillId)

    suspend fun putOnCooldown(skillId: Long): SkillDTO = api.putOnCooldown(skillId)

    suspend fun removeCooldown(skillId: Long): SkillDTO = api.removeCooldown(skillId)

    suspend fun equipSkill(
        skillId: Long,
        equipSkillDTO: EquipSkillDTO,
    ): SkillDTO = api.equipSkill(skillId, equipSkillDTO)

    suspend fun unequipSkill(skillId: Long): SkillDTO = api.unequipSkill(skillId)
}
