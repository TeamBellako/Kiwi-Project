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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.audio.Kiwi_Music_Home
import com.bellako.kiwi.audio.Kiwi_Music_SignUp
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.screens.modals.SupportModalScreen
import com.bellako.kiwi.common.screens.modals.PermissionsModalScreen
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.model.PersonalityViewModel
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.model.SettingsViewModel
import com.bellako.kiwi.features.settings.screens.SettingsScreen
import com.bellako.kiwi.features.users.model.UsersViewModel
import com.bellako.kiwi.features.users.screens.LogInScreen
import com.bellako.kiwi.features.users.screens.SignUpScreen1_Welcome
import com.bellako.kiwi.features.users.screens.SignUpScreen2_Form
import com.bellako.kiwi.features.users.screens.SignUpScreen3_Test
import com.bellako.kiwi.features.users.screens.SignUpScreen4_Apps

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    usersViewModel: UsersViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel(),
    metricsViewModel: MetricsViewModel = hiltViewModel(),
) {
    Kiwi_AudioHandler()

    PermissionsModalScreen {
        AppScreen(usersViewModel, settingsViewModel, personalityViewModel, metricsViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppScreen(
    usersViewModel: UsersViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    personalityViewModel: PersonalityViewModel = hiltViewModel(),
    metricsViewModel: MetricsViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val route = currentBackStackEntry?.destination?.route
    val isLoginCompleted = usersViewModel.isLoginCompleted.collectAsState().value
    val isLoginScreen =
        route == ScreenRoutes.LOGIN ||
            route == ScreenRoutes.SIGNUP1_WELCOME ||
            route == ScreenRoutes.SIGNUP2_FORM ||
            route == ScreenRoutes.SIGNUP3_TEST ||
            route == ScreenRoutes.SIGNUP4_APPS

    val showDashboard = route == ScreenRoutes.HOME

    Scaffold(
        bottomBar = {
            if (!isLoginScreen && isLoginCompleted) {
                AppBarScreen(navController = navController)
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
                        Kiwi_Music_Home()
                        LogInScreen(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController,
                        )
                    }

                    composable(ScreenRoutes.SIGNUP1_WELCOME) {
                        Kiwi_BackHandler()
                        Kiwi_Music_SignUp()
                        SignUpScreen1_Welcome(
                            viewModel = usersViewModel,
                            navController = navController,
                        )
                    }

                    composable(ScreenRoutes.SIGNUP2_FORM) {
                        Kiwi_BackHandler()
                        Kiwi_Music_SignUp()
                        SignUpScreen2_Form(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController,
                        )
                    }

                    composable(ScreenRoutes.SIGNUP3_TEST) {
                        Kiwi_BackHandler()
                        Kiwi_Music_SignUp()
                        SignUpScreen3_Test(
                            usersViewModel = usersViewModel,
                            personalityViewModel = personalityViewModel,
                            navController = navController,
                        )
                    }

                    composable(ScreenRoutes.SIGNUP4_APPS) {
                        Kiwi_BackHandler()
                        Kiwi_Music_SignUp()
                        SignUpScreen4_Apps(
                            personalityViewModel = personalityViewModel,
                            navController = navController,
                        )
                    }

                    composable(ScreenRoutes.HOME) {
                        Kiwi_BackHandler()
                        Kiwi_Music_Home()
                        MapScreen()
                    }

                    composable(ScreenRoutes.HELP) {
                        Kiwi_BackHandler()
                        Kiwi_Music_Home()
                        SupportModalScreen(navController = navController)
                    }

                    composable(ScreenRoutes.SETTINGS) {
                        Kiwi_BackHandler()
                        Kiwi_Music_Home()
                        SettingsScreen(
                            usersViewModel = usersViewModel,
                            settingsViewModel = settingsViewModel,
                            navController = navController,
                        )
                    }
                }

                if (showDashboard) {
                    DashboardScreen(metricsViewModel, personalityViewModel)
                }

                if (!isLoginScreen && isLoginCompleted) {
                    Kiwi_LoggedInScreen(
                        settingsViewModel = settingsViewModel,
                        personalityViewModel = personalityViewModel,
                    )
                }
            }
        },
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
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    AudioManager.onBackgroundResume()
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    AudioManager.onBackgroundEnter()
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
