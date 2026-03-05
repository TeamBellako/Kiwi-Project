package com.bellako.kiwi.features.map.data

import androidx.compose.ui.graphics.Color
import com.bellako.kiwi.R

object MapsInfo {
    val MindVeil: MapInfo =
        MapInfo(
            mapResourceId = R.drawable.mindveil_4k,
            maxZoom = 8f,
            backgroundColor = Color.White,
            mapId = 1,
        )

    val Testing: MapInfo =
        MapInfo(
            mapResourceId = R.drawable.map_switch_test,
            maxZoom = 4f,
            backgroundColor = Color.Red,
            mapId = 2,
        )

    val mapsList = listOf(MindVeil, Testing)

    fun findMapById(id: Int): MapInfo = mapsList.find { it.mapId == id } ?: MindVeil
}
