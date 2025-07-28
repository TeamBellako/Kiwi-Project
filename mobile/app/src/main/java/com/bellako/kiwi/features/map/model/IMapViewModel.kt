package com.bellako.kiwi.features.map.model

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.common.model.IBaseViewModel
import com.bellako.kiwi.features.map.data.MapState
import kotlinx.coroutines.flow.StateFlow

interface IMapViewModel : IBaseViewModel<MapState> {
    override val state: StateFlow<MapState>

    fun updateDimensions(
        mapWidth: Float,
        mapHeight: Float,
        viewportWidth: Float,
        viewportHeight: Float
    )
    fun updateScale(scaleFactor: Float, centroid: Offset)
    fun updateOffset(delta: Offset)
}