package com.bellako.kiwi.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.features.metrics.MetricsViewModel
import com.bellako.kiwi.features.settings.SettingsScreen
import com.bellako.kiwi.features.settings.SettingsViewModel
import com.bellako.kiwi.features.users.UsersScreen
import com.bellako.kiwi.features.users.UsersViewModel
import com.bellako.kiwi.ui.modals.AppBarModal
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.modals.PermissionsRequestModal

object ScreenRoutes {
    const val HOME = "home"
    const val USERS = "users"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(usersViewModel: UsersViewModel = hiltViewModel()) {
    PermissionsRequestModal {
        AppScreen(usersViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppScreen(usersViewModel: UsersViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isLoginScreen = currentBackStackEntry?.destination?.route == ScreenRoutes.USERS

    Scaffold(
        bottomBar = {
            if (!isLoginScreen && usersViewModel.isLoginCompleted.value) {
                AppBarModal(navController = navController)
            }
        },
        content = { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.USERS,
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

                if (!isLoginScreen && usersViewModel.isLoginCompleted.value) {
                    val metricsViewModel: MetricsViewModel = hiltViewModel()
                    DashboardModal(metricsViewModel)
                }
            }
        }
    )
}
