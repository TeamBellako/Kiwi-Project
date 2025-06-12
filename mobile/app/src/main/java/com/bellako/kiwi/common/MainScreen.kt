package com.bellako.kiwi.common

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.settings.SettingsScreen
import com.bellako.kiwi.settings.SettingsViewModel
import com.bellako.kiwi.login.LoginScreen
import com.bellako.kiwi.login.LoginViewModel

object ScreenRoutes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@Composable
fun MainScreen(loginViewModel : LoginViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenRoutes.LOGIN) {

        composable(ScreenRoutes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                navController = navController
            )
        }

        composable(ScreenRoutes.HOME) {
            HomeScreen(
                navController = navController,
                onLogout = {
                    loginViewModel.logout()
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
            val settingsViewModel : SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                navController = navController
            )
        }
    }
}
