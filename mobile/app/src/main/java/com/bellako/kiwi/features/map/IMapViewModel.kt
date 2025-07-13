package com.bellako.kiwi.features.map

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.features.common.IBaseViewModel
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