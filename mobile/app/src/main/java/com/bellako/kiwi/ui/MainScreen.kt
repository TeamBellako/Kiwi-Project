package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.usersettings.UserSettingsScreen
import com.bellako.kiwi.usersettings.UserSettingsViewModel

@Composable
fun MainScreen() {
    val viewModel: UserSettingsViewModel = hiltViewModel()
    UserSettingsScreen(viewModel);
}
