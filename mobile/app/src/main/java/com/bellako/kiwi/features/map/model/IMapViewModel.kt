package com.bellako.kiwi.features.map.model

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.map.data.MapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IMapViewModel : IBaseViewModel<MapState> {
    override val state: StateFlow<MapState>

    val previousState: MutableStateFlow<MapState>

    fun updatePreviousState()

    fun updateScale(
        scaleFactor: Float,
        centroid: Offset,
    )

    fun updateOffset(delta: Offset)
}
