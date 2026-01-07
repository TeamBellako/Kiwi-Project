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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.audio.Kiwi_Music_Home
import com.bellako.kiwi.audio.Kiwi_Music_Login
import com.bellako.kiwi.audio.Kiwi_Music_Settings
import com.bellako.kiwi.audio.Kiwi_Music_SignUp
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.screens.modals.PermissionsModalScreen
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
import com.bellako.kiwi.features.appbar.model.AppBarViewModel
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.dashboard.screens.DashboardScreen
import com.bellako.kiwi.features.goals.model.GoalsViewModel
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.model.NodesViewModel
import com.bellako.kiwi.features.objectives.ObjectivesScreen
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.model.PersonalityViewModel
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.model.QuestNotificationEvent
import com.bellako.kiwi.features.quests.model.QuestsViewModel
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
    nodesViewModel: NodesViewModel = hiltViewModel(),
    questsViewModel: QuestsViewModel = hiltViewModel(),
    goalsViewModel: GoalsViewModel = hiltViewModel(),
    appBarViewModel: AppBarViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()

    Kiwi_AudioHandler()

    PermissionsModalScreen(
        navController = navController,
        exclusionRoutes =
            listOf(
                ScreenRoutes.LOGIN,
                ScreenRoutes.SIGNUP1_WELCOME,
                ScreenRoutes.SIGNUP2_FORM,
                ScreenRoutes.SIGNUP3_TEST,
            ),
    ) {
        AppScreen(
            navController = navController,
            usersViewModel = usersViewModel,
            settingsViewModel = settingsViewModel,
            personalityViewModel = personalityViewModel,
            metricsViewModel = metricsViewModel,
            nodesViewModel = nodesViewModel,
            questsViewModel = questsViewModel,
            goalsViewModel = goalsViewModel,
            appBarViewModel = appBarViewModel,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun AppScreenWrapper(screen: @Composable () -> Unit) {
    Kiwi_BackHandler()
    screen()
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun AppScreen(
    navController: NavHostController,
    usersViewModel: UsersViewModel,
    settingsViewModel: SettingsViewModel,
    personalityViewModel: PersonalityViewModel,
    metricsViewModel: MetricsViewModel,
    nodesViewModel: NodesViewModel,
    questsViewModel: QuestsViewModel,
    goalsViewModel: GoalsViewModel,
    appBarViewModel: AppBarViewModel,
) {
    val isLoginCompleted = usersViewModel.isLoginCompleted.collectAsState().value
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val route = currentBackStackEntry?.destination?.route
    val isLoginScreen =
        route == null ||
            route == ScreenRoutes.LOGIN ||
            route == ScreenRoutes.SIGNUP1_WELCOME ||
            route == ScreenRoutes.SIGNUP2_FORM ||
            route == ScreenRoutes.SIGNUP3_TEST ||
            route == ScreenRoutes.SIGNUP4_APPS

    val showDashboard = route == ScreenRoutes.HOME

    LaunchedEffect(Unit) {
        questsViewModel.getNotifications().collect { event ->
            when (event) {
                is QuestNotificationEvent.QuestCompleted -> {}
                else -> appBarViewModel.onNewContent(ScreenRoutes.OBJECTIVES)
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (!isLoginScreen && isLoginCompleted) {
                AppBarScreen(
                    navController = navController,
                    appBarViewModel = appBarViewModel,
                )
            }
        },
        content = { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                AppNavHost(
                    navController = navController,
                    usersViewModel = usersViewModel,
                    settingsViewModel = settingsViewModel,
                    personalityViewModel = personalityViewModel,
                    nodesViewModel = nodesViewModel,
                    questsViewModel = questsViewModel,
                    goalsViewModel = goalsViewModel,
                )

                if (showDashboard) {
                    DashboardScreen(
                        usersViewModel = usersViewModel,
                        metricsViewModel = metricsViewModel,
                        personalityViewModel = personalityViewModel,
                    )
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

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    navController: NavHostController,
    usersViewModel: UsersViewModel,
    settingsViewModel: ISettingsViewModel,
    personalityViewModel: IPersonalityViewModel,
    nodesViewModel: INodesViewModel,
    questsViewModel: IQuestsViewModel,
    goalsViewModel: IGoalsViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.LOGIN,
    ) {
        composable(ScreenRoutes.LOGIN) {
            AppScreenWrapper {
                Kiwi_Music_Login()
                LogInScreen(
                    usersViewModel = usersViewModel,
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SIGNUP1_WELCOME) {
            AppScreenWrapper {
                Kiwi_Music_Login()
                SignUpScreen1_Welcome(
                    viewModel = usersViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SIGNUP2_FORM) {
            AppScreenWrapper {
                Kiwi_Music_SignUp()
                SignUpScreen2_Form(
                    usersViewModel = usersViewModel,
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SIGNUP3_TEST) {
            AppScreenWrapper {
                Kiwi_Music_SignUp()
                SignUpScreen3_Test(
                    usersViewModel = usersViewModel,
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SIGNUP4_APPS) {
            AppScreenWrapper {
                Kiwi_Music_SignUp()
                SignUpScreen4_Apps(
                    personalityViewModel = personalityViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.HOME) {
            AppScreenWrapper {
                Kiwi_Music_Home()
                MapScreen(
                    nodesViewModel = nodesViewModel,
                    questsViewModel = questsViewModel,
                    navController = navController,
                    goalsViewModel = goalsViewModel,
                    mapViewModel = hiltViewModel(),
                )
            }
        }

        composable(ScreenRoutes.OBJECTIVES) {
            AppScreenWrapper {
                ObjectivesScreen(questsViewModel = questsViewModel)
            }
        }

        composable(
            route = ScreenRoutes.OBJECTIVES_FOCUS,
            arguments = listOf(navArgument("questId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId")

            ObjectivesScreen(
                questsViewModel = questsViewModel,
                focusedQuestId = questId,
            )
        }

        composable(ScreenRoutes.SETTINGS) {
            AppScreenWrapper {
                Kiwi_Music_Settings()
                SettingsScreen(
                    usersViewModel = usersViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.WIP) {
            AppScreenWrapper {
                Kiwi_Music_Home()
                WIPModalScreen()
            }
        }
    }
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
