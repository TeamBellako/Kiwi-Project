package com.bellako.kiwi.features.skills.model

import com.bellako.kiwi.features.skills.data.EquipSkillDTO
import com.bellako.kiwi.features.skills.data.SkillDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ISkillsAPI {
    @GET("api/skills")
    suspend fun getAllSkills(): List<SkillDTO>

    @POST("api/skills/{skillId}/give")
    suspend fun giveSkill(
        @Path("skillId") skillId: Long,
    ): SkillDTO

    @POST("api/skills/{skillId}/levelup")
    suspend fun levelUpSkill(
        @Path("skillId") skillId: Long,
    ): SkillDTO

    @POST("api/skills/{skillId}/cooldown")
    suspend fun putOnCooldown(
        @Path("skillId") skillId: Long,
    ): SkillDTO

    @POST("api/skills/{skillId}/ready")
    suspend fun removeCooldown(
        @Path("skillId") skillId: Long,
    ): SkillDTO

    @POST("api/skills/{skillId}/equip")
    suspend fun equipSkill(
        @Path("skillId") skillId: Long,
        @Body equipSkillDTO: EquipSkillDTO,
    ): SkillDTO

    @POST("api/skills/{skillId}/unequip")
    suspend fun unequipSkill(
        @Path("skillId") skillId: Long,
    ): SkillDTO
}
