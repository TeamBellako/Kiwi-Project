package com.bellako.kiwi.features.combat.model

import com.bellako.kiwi.features.combat.data.CombatDTO
import com.bellako.kiwi.features.combat.data.CombatTurnResultDTO
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ICombatAPI {
    @GET("api/combat/active")
    suspend fun getActiveCombat(): CombatDTO?

    @POST("api/combat/start/{combatConfigId}")
    suspend fun startOrResumeCombat(
        @Path("combatConfigId") combatConfigId: Long,
    ): CombatDTO

    @POST("api/combat/{combatId}/skill/{skillId}")
    suspend fun executeTurn(
        @Path("combatId") combatId: Long,
        @Path("skillId") skillId: Long,
    ): CombatTurnResultDTO

    @POST("api/combat/{combatId}/timeout")
    suspend fun timeoutCombat(
        @Path("combatId") combatId: Long,
    ): CombatTurnResultDTO

    @POST("api/combat/{combatId}/abandon")
    suspend fun abandonCombat(
        @Path("combatId") combatId: Long,
    ): CombatTurnResultDTO

    @POST("api/combat/{combatId}/barks/{triggerId}/fired")
    suspend fun markBarkFired(
        @Path("combatId") combatId: Long,
        @Path("triggerId") triggerId: Long,
    )
}
