package com.bellako.kiwi.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.HelpScreen
import com.bellako.kiwi.common.HomeScreen
import com.bellako.kiwi.userSettings.UserSettingsScreen
import com.bellako.kiwi.userSettings.UserSettingsViewModel
import com.bellako.kiwi.users.UsersScreen
import com.bellako.kiwi.users.UsersViewModel

object ScreenRoutes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@Composable
fun MainScreen(usersViewModel : UsersViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {

        composable(ScreenRoutes.LOGIN) {
            UsersScreen(
                viewModel = usersViewModel,
                navController = navController
            )
        }

        composable(ScreenRoutes.HOME) {
            HomeScreen(
                navController = navController,
                onLogout = {
                    usersViewModel.logout()
                    navController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.HELP) {
            HelpScreen(
                navController = navController
            )
        }

        composable(ScreenRoutes.SETTINGS) {
            val userSettingsViewModel : UserSettingsViewModel = hiltViewModel()
            UserSettingsScreen(
                viewModel = userSettingsViewModel,
                navController = navController
            )
        }
    }
}
