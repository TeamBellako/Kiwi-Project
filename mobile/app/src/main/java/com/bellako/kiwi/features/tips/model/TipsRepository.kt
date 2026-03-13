package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.features.tips.data.TipDTO

class TipsRepository(
    private val api: ITipsAPI,
) {
    suspend fun getTip(id: Long): TipDTO = api.getTip(id)
}
