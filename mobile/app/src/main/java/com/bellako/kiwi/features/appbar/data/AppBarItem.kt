package com.bellako.kiwi.features.appbar.data

data class AppBarItem(
    val icon: Int,
    val route: String,
    val enabled: Boolean = true,
    val hasNewContent: Boolean = false,
)
