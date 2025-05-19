package com.bellako.kiwi.navigation

sealed class ScreenState {
    object Login: ScreenState()
    object Settings: ScreenState()
}