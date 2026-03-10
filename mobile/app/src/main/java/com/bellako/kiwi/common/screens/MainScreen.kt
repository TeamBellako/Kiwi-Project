package com.bellako.kiwi.common.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
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
import com.bellako.kiwi.features.conversations.data.ConversationType
import com.bellako.kiwi.features.conversations.model.ConversationViewModel
import com.bellako.kiwi.features.conversations.screens.ConversationScreen
import com.bellako.kiwi.features.conversations.screens.DialogueScreen
import com.bellako.kiwi.features.dashboard.screens.DashboardScreen
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.GoalsViewModel
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalNotificationType
import com.bellako.kiwi.features.goals.screens.GoalsModal
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.model.MetricsViewModel
import com.bellako.kiwi.features.nodes.model.INodesViewModel
import com.bellako.kiwi.features.nodes.model.NodesViewModel
import com.bellako.kiwi.features.notifications.controller.NotificationEvent
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.notifications.screens.NotificationOverlay
import com.bellako.kiwi.features.objectives.ObjectivesScreen
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.model.PersonalityViewModel
import com.bellako.kiwi.features.quests.model.IQuestsViewModel
import com.bellako.kiwi.features.quests.model.QuestsViewModel
import com.bellako.kiwi.features.quests.screens.QuestNotificationType
import com.bellako.kiwi.features.settings.model.ISettingsViewModel
import com.bellako.kiwi.features.settings.model.SettingsViewModel
import com.bellako.kiwi.features.settings.screens.SettingsScreen
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.model.SkillsViewModel
import com.bellako.kiwi.features.skills.screen.SkillsScreen
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
    skillsViewModel: SkillsViewModel = hiltViewModel(),
    appBarViewModel: AppBarViewModel = hiltViewModel(),
    notificationManager: NotificationManager,
    conversationViewModel: ConversationViewModel = hiltViewModel(),
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
            skillsViewModel = skillsViewModel,
            appBarViewModel = appBarViewModel,
            notificationManager = notificationManager,
            conversationViewModel = conversationViewModel,
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
@Suppress("LongParameterList", "ComplexMethod", "MagicNumber")
private fun AppScreen(
    navController: NavHostController,
    usersViewModel: UsersViewModel,
    settingsViewModel: SettingsViewModel,
    personalityViewModel: PersonalityViewModel,
    metricsViewModel: MetricsViewModel,
    nodesViewModel: NodesViewModel,
    questsViewModel: QuestsViewModel,
    goalsViewModel: GoalsViewModel,
    skillsViewModel: SkillsViewModel,
    appBarViewModel: AppBarViewModel,
    notificationManager: NotificationManager,
    conversationViewModel: ConversationViewModel,
) {
    val isLoginCompleted = usersViewModel.isLoginCompleted.collectAsState().value
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val route = currentBackStackEntry?.destination?.route
    val isLoginScreen = isLoginScreen(route)

    val showDashboard = route == ScreenRoutes.HOME

    val activeConversation by conversationViewModel.active.collectAsState()
    val isConversationVisible by conversationViewModel.isVisible.collectAsState()

    LaunchedEffect(notificationManager) {
        notificationManager.notifications.collect { event ->
            when (event) {
                is NotificationEvent.Quest -> {
                    if (event.type == QuestNotificationType.QUEST_COMPLETED) {
                        appBarViewModel.onNewContent(ScreenRoutes.OBJECTIVES)
                    }
                }

                is NotificationEvent.Skill -> {
                    appBarViewModel.onNewContent(ScreenRoutes.SKILLS)
                }

                else -> {
                }
            }
        }
    }

    val goalsModalRequest = remember { mutableStateOf<Pair<GoalNotificationType, List<IGoal>>?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        skillsViewModel = skillsViewModel,
//                        onConversationRequest = { conversationId ->
//                            conversationViewModel.start(conversationId)
//                        },
                    )

                    if (showDashboard && !isConversationVisible) {
                        DashboardScreen(
                            usersViewModel = usersViewModel,
                            metricsViewModel = metricsViewModel,
                            personalityViewModel = personalityViewModel,
                            goalsViewModel = goalsViewModel,
                        )
                    }

                    if (!isLoginScreen && isLoginCompleted) {
                        Kiwi_LoggedInScreen(
                            settingsViewModel = settingsViewModel,
                            personalityViewModel = personalityViewModel,
                        )
                    }

                    LaunchedEffect(isLoginCompleted) {
                        if (isLoginCompleted) {
                            skillsViewModel.onUserLoggedIn()
                        }
                    }

                    AnimatedVisibility(
                        visible = isConversationVisible,
                        enter =
                            slideInVertically(
                                initialOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                            ) + fadeIn(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                        exit =
                            slideOutVertically(
                                targetOffsetY = { fullHeight -> fullHeight },
                                animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                            ) + fadeOut(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                    ) {
                        activeConversation?.let { conversation ->
                            Box(modifier = Modifier.matchParentSize()) {
                                if (conversation.type == ConversationType.SMALL) {
                                    DialogueScreen(
                                        conversation = conversation,
                                        viewModel = conversationViewModel,
                                    )
                                } else {
                                    ConversationScreen(
                                        conversation = conversation,
                                        viewModel = conversationViewModel,
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )

        // Overlay global de notificaciones — único colector, siempre activo
        if (!isLoginScreen && isLoginCompleted) {
            NotificationOverlay(
                notificationManager = notificationManager,
                onGoalClick = { type, goals ->
                    goalsModalRequest.value = type to goals
                    notificationManager.dismissCurrent()
                },
                onQuestClick = { type, quest, subquestId ->
                    if (type != QuestNotificationType.QUEST_COMPLETED) {
                        navController.navigate("OBJECTIVES/${quest.id}")
                    }
                    notificationManager.dismissCurrent()
                },
                onSkillClick = { _, skill ->
                    navController.navigate("SKILLS/${skill.id}")
                    notificationManager.dismissCurrent()
                },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(10f),
            )

            goalsModalRequest.value?.let { (type, goals) ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .zIndex(11f),
                ) {
                    GoalsModal(
                        goalModalType = type,
                        goals = goals,
                        goalsViewModel = goalsViewModel,
                        onDismiss = { goalsModalRequest.value = null },
                    )
                }
            }
        }
    }
}

private fun isLoginScreen(route: String?): Boolean =
    route == null ||
        route == ScreenRoutes.LOGIN ||
        route == ScreenRoutes.SIGNUP1_WELCOME ||
        route == ScreenRoutes.SIGNUP2_FORM ||
        route == ScreenRoutes.SIGNUP3_TEST ||
        route == ScreenRoutes.SIGNUP4_APPS

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
    skillsViewModel: ISkillsViewModel,
//    onConversationRequest: (Long) -> Unit = {},
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
                    goalsViewModel = goalsViewModel,
                    mapViewModel = hiltViewModel(),
//                    onConversationRequest = onConversationRequest,
                )
            }
        }

        composable(ScreenRoutes.OBJECTIVES) {
            AppScreenWrapper {
                ObjectivesScreen(
                    questsViewModel = questsViewModel,
                    goalsViewModel = goalsViewModel,
                )
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
                goalsViewModel = goalsViewModel,
            )
        }

        composable(ScreenRoutes.SKILLS) {
            AppScreenWrapper {
                SkillsScreen(skillsViewModel = skillsViewModel)
            }
        }

        composable(
            route = ScreenRoutes.SKILLS_FOCUS,
            arguments = listOf(navArgument("skillId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getLong("skillId")

            SkillsScreen(
                skillsViewModel = skillsViewModel,
                focusedSkillId = questId,
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
                WIPModalScreen(
                    navController = navController,
                )
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
