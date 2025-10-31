package com.bellako.kiwi.features.appbar.data

import com.bellako.kiwi.R
import com.bellako.kiwi.common.data.ScreenRoutes

data class AppBarItem(
    val icon: Int,
    val route: String,
    val enabled: Boolean = true,
)

val appBarItems =
    listOf(
        AppBarItem(
            icon = R.drawable.ic_map,
            route = ScreenRoutes.HOME,
        ),
        AppBarItem(
            icon = R.drawable.ic_skills,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_objectives,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_troops,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_settings,
            route = ScreenRoutes.SETTINGS,
        ),
    )
