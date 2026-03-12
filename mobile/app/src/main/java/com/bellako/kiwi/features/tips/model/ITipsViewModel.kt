package com.bellako.kiwi.features.tips.model

import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.tips.data.TipState

interface ITipsViewModel : IBaseViewModel<TipState> {
    fun getTip(id: Long)
}
