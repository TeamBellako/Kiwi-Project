package com.bellako.kiwi.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.features.settings.SettingsScreen
import com.bellako.kiwi.features.settings.SettingsViewModel
import com.bellako.kiwi.features.users.UsersScreen
import com.bellako.kiwi.features.users.UsersViewModel
import com.bellako.kiwi.ui.modals.AppBarModal

object ScreenRoutes {
    const val HOME = "home"
    const val USERS = "users"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@Composable
fun MainScreen(usersViewModel : UsersViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBarModal(navController = navController) },
        content = { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = ScreenRoutes.USERS,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(ScreenRoutes.USERS) {
                    UsersScreen(
                        viewModel = usersViewModel,
                        navController = navController
                    )
                }

                composable(ScreenRoutes.HOME) {
                    HomeScreen()
                }

                composable(ScreenRoutes.HELP) {
                    HelpScreen(navController = navController)
                }

                composable(ScreenRoutes.SETTINGS) {
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        navController = navController,
                        onLogout = {
                            usersViewModel.logout()
                            navController.navigate(ScreenRoutes.USERS) {
                                popUpTo(ScreenRoutes.USERS) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )
}