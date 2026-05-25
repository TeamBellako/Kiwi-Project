package com.bellako.kiwi.features.map.data

import androidx.compose.ui.graphics.Color
import com.bellako.kiwi.R

object MapsInfo {
    val MindVeil: MapInfo =
        MapInfo(
            mapTitle = "MINDVEIL",
            mapResourceId = R.drawable.mindveil_4k,
            maxZoom = 8f,
            backgroundColor = Color.White,
            mapId = 0,
        )

    val VigilarisCastle: MapInfo =
        MapInfo(
            mapTitle = "VIGILARIS CASTLE",
            mapResourceId = R.drawable.vigilaris_castle,
            maxZoom = 4f,
            backgroundColor = Color.White,
            mapId = 1,
        )

    val mapsList = listOf(MindVeil, VigilarisCastle)

    fun findMapById(id: Int): MapInfo = mapsList.find { it.mapId == id } ?: MindVeil
}
