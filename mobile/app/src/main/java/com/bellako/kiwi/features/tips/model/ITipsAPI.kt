package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.features.tips.data.TipDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ITipsAPI {
    @GET("api/tips/{id}")
    suspend fun getTip(
        @Path("id") id: Long,
    ): TipDTO
}

class FakeTipsAPI : ITipsAPI {
    override suspend fun getTip(
        @Path("id") id: Long,
    ): TipDTO = TipDTO(id, "Mock Tip", "This is a mock tip for preview purposes.", readMoreURL = "")
}
