package com.bellako.kiwi.features.map.data

import androidx.compose.ui.geometry.Offset
import com.bellako.kiwi.R

data class MapState(
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val mapWidthPx: Float = 0f,
    val mapHeightPx: Float = 0f,
    val viewportWidthPx: Float = 0f,
    val viewportHeightPx: Float = 0f,
    val selectedNodeId: Long? = null,
    val playerNode: Long = 0,
    val isFocusingNode: Boolean = false,
    val mapResourceId: Int = R.drawable.mindveil_4k,
)
