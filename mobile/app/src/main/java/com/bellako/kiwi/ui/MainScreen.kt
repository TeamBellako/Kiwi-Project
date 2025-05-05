package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.userSettings.ui.UserSettingsScreen
import com.bellako.kiwi.userSettings.viewModel.UserSettingsViewModel

@Composable
fun MainScreen() {
    val viewModel: UserSettingsViewModel = hiltViewModel()
    UserSettingsScreen(viewModel);
}
