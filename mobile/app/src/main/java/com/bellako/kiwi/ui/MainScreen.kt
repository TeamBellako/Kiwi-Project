package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.navigation.ScreenState
import com.bellako.kiwi.userSettings.UserSettingsScreen
import com.bellako.kiwi.userSettings.UserSettingsViewModel
import com.bellako.kiwi.users.UsersScreen
import com.bellako.kiwi.users.UsersViewModel

@Composable
fun MainScreen(viewModel: UsersViewModel = hiltViewModel()) {
    var currentScreen by remember { mutableStateOf<ScreenState>(ScreenState.Login) }

    when (currentScreen) {
        is ScreenState.Login -> UsersScreen(
            onLoginSuccess = { currentScreen = ScreenState.Settings },
            viewModel = viewModel
        )

        is ScreenState.Settings -> {
            val userSettingsViewModel : UserSettingsViewModel = hiltViewModel()
            UserSettingsScreen(
                onLogout = { currentScreen = ScreenState.Login},
                viewModel = userSettingsViewModel)
        }
    }
}
