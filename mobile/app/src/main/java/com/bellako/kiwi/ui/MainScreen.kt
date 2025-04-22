package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bellako.kiwi.usersettings.UserSettingsScreen
import com.bellako.kiwi.usersettings.UserSettingsViewModel

@Composable
fun MainScreen() {
    val viewModel: UserSettingsViewModel = viewModel()
    UserSettingsScreen(viewModel);
}
