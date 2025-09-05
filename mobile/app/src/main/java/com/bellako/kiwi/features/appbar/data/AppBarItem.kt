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
            icon = R.drawable.ic_fire,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_target,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_swords,
            route = ScreenRoutes.WIP,
        ),
        AppBarItem(
            icon = R.drawable.ic_person,
            route = ScreenRoutes.SETTINGS,
        ),
    )
