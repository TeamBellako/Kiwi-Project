package com.bellako.kiwi.common.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.modals.AppBarModal
import com.bellako.kiwi.common.screens.modals.DashboardModal
import com.bellako.kiwi.common.screens.modals.PermissionsRequestModal
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.model.PersonalityViewModel
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.screens.SettingsScreen
import com.bellako.kiwi.features.settings.model.SettingsViewModel
import com.bellako.kiwi.features.users.model.UsersViewModel
import com.bellako.kiwi.features.users.screens.LogInScreen
import com.bellako.kiwi.features.users.screens.SignUpScreen
import com.bellako.kiwi.features.users.screens.SignUpTestScreen
import com.bellako.kiwi.features.users.screens.SignUpWelcomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel(),
    metricsViewModel: MetricsViewModel = hiltViewModel()
) {
    Kiwi_AudioHandler()

    PermissionsRequestModal {
        AppScreen(usersViewModel, settingsViewModel, personalityViewModel, metricsViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppScreen(
    usersViewModel: UsersViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel(),
    metricsViewModel: MetricsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val route = currentBackStackEntry?.destination?.route
    val isLoginCompleted = usersViewModel.isLoginCompleted.collectAsState().value
    val isLoginScreen =
        route == ScreenRoutes.LOGIN ||
        route == ScreenRoutes.SIGNUP_WELCOME ||
        route == ScreenRoutes.SIGNUP ||
        route == ScreenRoutes.SIGNUP_TEST
    val isSettingsScreen = route == ScreenRoutes.SETTINGS

    Scaffold(
        bottomBar = {
            if (!isLoginScreen && isLoginCompleted) {
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
                        Kiwi_BackHandler()
                        LogInScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP_WELCOME) {
                        Kiwi_BackHandler()
                        SignUpWelcomeScreen(
                            viewModel = usersViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP) {
                        Kiwi_BackHandler()
                        SignUpScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.SIGNUP_TEST) {
                        Kiwi_BackHandler()
                        SignUpTestScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController
                        )
                    }

                    composable(ScreenRoutes.HOME) {
                        Kiwi_BackHandler()
                        MapScreen()
                    }

                    composable(ScreenRoutes.HELP) {
                        Kiwi_BackHandler()
                        HelpScreen(navController = navController)
                    }

                    composable(ScreenRoutes.SETTINGS) {
                        Kiwi_BackHandler()
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            navController = navController,
                            onLogout = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    usersViewModel.logout(context)
                                    navController.navigate(ScreenRoutes.LOGIN) {
                                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }

                if (!isLoginScreen && isLoginCompleted && !isSettingsScreen) {
                    Kiwi_BackHandler()
                    DashboardModal(metricsViewModel)
                }

                if (!isLoginScreen && isLoginCompleted) {
                    Kiwi_BackHandler()
                    Kiwi_LoggedInScreen(
                        settingsViewModel = settingsViewModel,
                        personalityViewModel = personalityViewModel,
                    )
                }
            }
        }
    )
}

@Composable
fun Kiwi_LoggedInScreen(
    settingsViewModel: ISettingsViewModel,
    personalityViewModel: IPersonalityViewModel,
) {
    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings()
    }
    LaunchedEffect(Unit) {
        personalityViewModel.loadPersonality()
    }
}

@Composable
private fun Kiwi_AudioHandler() {
    val lifecycleOwner = remember { ProcessLifecycleOwner.get() }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    AudioManager.onBackgroundResume()
                }
                Lifecycle.Event.ON_STOP -> {
                    AudioManager.onBackgroundEnter()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun Kiwi_BackHandler() {
    val activity = LocalActivity.current
    BackHandler(enabled = true) {
        activity?.moveTaskToBack(true)
    }
}
