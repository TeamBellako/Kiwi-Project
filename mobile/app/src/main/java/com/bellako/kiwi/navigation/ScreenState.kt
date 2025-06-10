package com.bellako.kiwi.navigation

sealed class ScreenState {
    object Home: ScreenState()
    object Login: ScreenState()
    object Settings: ScreenState()
    object Help: ScreenState()
}