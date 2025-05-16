package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.userSettings.ui.UserSettingsScreen
import com.bellako.kiwi.userSettings.viewModel.UserSettingsViewModel
import com.bellako.kiwi.users.IUsersViewModel
import com.bellako.kiwi.users.UsersFakeViewModel
import com.bellako.kiwi.users.UsersScreen
import com.bellako.kiwi.users.UsersState
import com.bellako.kiwi.users.UsersViewModel

@Composable
fun MainScreen() {
    val viewModel : UsersViewModel = hiltViewModel()
    UsersScreen(viewModel)
}
