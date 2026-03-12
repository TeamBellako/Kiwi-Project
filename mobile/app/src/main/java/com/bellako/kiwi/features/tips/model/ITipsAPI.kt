package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.features.tips.data.TipDTO
import retrofit2.http.GET

interface ITipsAPI {
    @GET("api/skills/{id}")
    suspend fun getTip(): TipDTO
}
