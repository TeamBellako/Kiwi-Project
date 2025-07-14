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
import com.bellako.kiwi.features.personality.PersonalityViewModel
import com.bellako.kiwi.features.settings.SettingsScreen
import com.bellako.kiwi.features.settings.SettingsViewModel
import com.bellako.kiwi.features.users.UsersViewModel
import com.bellako.kiwi.features.users.login.LogInScreen
import com.bellako.kiwi.features.users.signup.SignUpScreen
import com.bellako.kiwi.features.users.signup.SignUpTestScreen
import com.bellako.kiwi.features.users.signup.SignUpWelcomeScreen
import com.bellako.kiwi.ui.modals.AppBarModal
import com.bellako.kiwi.ui.modals.DashboardModal
import com.bellako.kiwi.ui.modals.PermissionsRequestModal

object ScreenRoutes {
    const val LOGIN = "login"
    const val SIGNUP_WELCOME = "signup_welcome"
    const val SIGNUP = "signup"
    const val SIGNUP_TEST = "signup_test"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val HELP = "help"
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    usersViewModel: UsersViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel()
) {

    PermissionsRequestModal {
        AppScreen(usersViewModel, personalityViewModel)
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppScreen(
    usersViewModel: UsersViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel()
) {

    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val isLoginScreen = currentBackStackEntry?.destination?.route == ScreenRoutes.LOGIN

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
                    startDestination = ScreenRoutes.LOGIN,
                ) {
                    composable(ScreenRoutes.LOGIN) {
                        LogInScreen(
                            viewModel = usersViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP_WELCOME) {
                        SignUpWelcomeScreen(
                            viewModel = usersViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP) {
                        SignUpScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP_TEST) {
                        SignUpTestScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
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
                                navController.navigate(ScreenRoutes.LOGIN) {
                                    popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
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
