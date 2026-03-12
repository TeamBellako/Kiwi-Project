package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.features.tips.data.TipDTO
import retrofit2.http.Path

class TipsRepository(
    private val api: ITipsAPI,
) {
    suspend fun getTip(
        @Path("id") id: Long,
    ): TipDTO = api.getTip()
}
