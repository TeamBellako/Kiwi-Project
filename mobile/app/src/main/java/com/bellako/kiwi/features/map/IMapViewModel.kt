package com.bellako.kiwi.features.map

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.features.common.IBaseViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for the Map ViewModel that handles the zoomable and draggable map functionality.
 */
interface IMapViewModel : IBaseViewModel<MapUiState> {
    /**
     * The current UI state of the map
     */
    override val state: StateFlow<MapUiState>
    
    /**
     * Updates the map dimensions and viewport size
     * 
     * @param mapWidth Width of the map image in pixels
     * @param mapHeight Height of the map image in pixels
     * @param viewportWidth Width of the viewport in pixels
     * @param viewportHeight Height of the viewport in pixels
     */
    fun updateDimensions(
        mapWidth: Float,
        mapHeight: Float,
        viewportWidth: Float,
        viewportHeight: Float
    )
    
    /**
     * Updates the scale (zoom) of the map
     * 
     * @param scaleFactor The factor by which to multiply the current scale
     * @param centroid The center point of the zoom gesture
     */
    fun updateScale(scaleFactor: Float, centroid: Offset)
    
    /**
     * Updates the offset (position) of the map
     * 
     * @param delta The change in position
     */
    fun updateOffset(delta: Offset)
}