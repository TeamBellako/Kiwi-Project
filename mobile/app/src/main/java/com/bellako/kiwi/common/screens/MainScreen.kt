package com.bellako.kiwi.common.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import com.bellako.kiwi.audio.Kiwi_Music_Conversation
import com.bellako.kiwi.audio.Kiwi_Music_Home
import com.bellako.kiwi.audio.Kiwi_Music_Login
import com.bellako.kiwi.audio.Kiwi_Music_Settings
import com.bellako.kiwi.audio.Kiwi_Music_SignUp
import com.bellako.kiwi.common.data.ScreenRoutes
import com.bellako.kiwi.common.screens.modals.PermissionsModalScreen
import com.bellako.kiwi.common.screens.modals.WIPModalScreen
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.features.appbar.model.AppBarViewModel
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.combat.model.CombatViewModel
import com.bellako.kiwi.features.combat.screens.CombatFlowScreen
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
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.nodes.screens.NodeEntryVeilOverlay
import com.bellako.kiwi.features.nodes.screens.rememberNodeEntryTransitionController
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
import com.bellako.kiwi.features.settings.screens.SettingsAccountScreen
import com.bellako.kiwi.features.settings.screens.SettingsAudioScreen
import com.bellako.kiwi.features.settings.screens.SettingsScreen
import com.bellako.kiwi.features.settings.screens.SettingsSupportScreen
import com.bellako.kiwi.features.skills.model.ISkillsViewModel
import com.bellako.kiwi.features.skills.model.SkillsViewModel
import com.bellako.kiwi.features.skills.screen.SkillsScreen
import com.bellako.kiwi.features.tips.model.TipsViewModel
import com.bellako.kiwi.features.tips.screen.TipScreen
import com.bellako.kiwi.features.users.model.UsersViewModel
import com.bellako.kiwi.features.users.screens.LogInScreen
import com.bellako.kiwi.features.users.screens.SignUpScreen1_Welcome
import com.bellako.kiwi.features.users.screens.SignUpScreen2_Form
import com.bellako.kiwi.features.users.screens.SignUpScreen3_Test
import com.bellako.kiwi.features.users.screens.SignUpScreen4_Apps
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private const val INITIAL_LOADING_MIN_DISPLAY_MS = 1500L
private const val INITIAL_LOADING_SETTLE_MS = 400L
private const val INITIAL_LOADING_MAX_DISPLAY_MS = 15_000L
private const val PROGRESS_FILL_DURATION_MS = 4500
private const val PROGRESS_HOLD_TARGET = 0.9f
private const val PROGRESS_FINISH_DURATION_MS = 300
private const val SCREEN_TRANSITION_MS = 400
private const val MAP_TRANSITION_MS = 550
private const val MAP_REVEAL_MS = 800

@Suppress("LongParameterList")
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
    combatViewModel: CombatViewModel = hiltViewModel(),
    tipsViewModel: TipsViewModel = hiltViewModel(),
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
            combatViewModel = combatViewModel,
            tipsViewModel = tipsViewModel,
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
@Suppress("LongParameterList", "ComplexMethod", "MagicNumber", "LongMethod")
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
    combatViewModel: CombatViewModel,
    tipsViewModel: TipsViewModel,
) {
    val isLoginCompleted = usersViewModel.isLoginCompleted.collectAsState().value
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val route = currentBackStackEntry?.destination?.route
    val isLoginScreen = isLoginScreen(route)

    val showDashboard = route == ScreenRoutes.HOME

    val activeConversation by conversationViewModel.active.collectAsState()
    val isConversationVisible by conversationViewModel.isVisible.collectAsState()

    // A no-background conversation sits over the map as a dialogue overlay
    // (Small dialogues, and the rare FULL conversation that ships without a
    // background). The map stays visible behind, so any selected-node action
    // button is also visible — MapScreen hides it while this is true.
    val isDialogueOverlaid by remember {
        derivedStateOf {
            isConversationVisible && activeConversation?.background.isNullOrBlank()
        }
    }

    val activeCombat by combatViewModel.active.collectAsState()
    val isCombatVisible by combatViewModel.isVisible.collectAsState()
    val isCombatTurnPlaying by combatViewModel.isTurnPlaying.collectAsState()
    val activeBark by combatViewModel.activeBark.collectAsState()
    val hasResolvedCombatOnStartup by combatViewModel.hasResolvedCombatOnStartup.collectAsState()
    val skillsState by skillsViewModel.state.collectAsState()

    val isTipVisible by tipsViewModel.isVisible.collectAsState()

    val usersIsLoading by usersViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()
    val settingsIsLoading by settingsViewModel.isLoading.collectAsState()
    val skillsIsLoading by skillsViewModel.isLoading.collectAsState()
    val combatIsLoading by combatViewModel.isLoading.collectAsState()
    val goalsIsLoading by goalsViewModel.isLoading.collectAsState()
    val questsIsLoading by questsViewModel.isLoading.collectAsState()
    val metricsIsLoading by metricsViewModel.isLoading.collectAsState()
    val nodesIsLoading by nodesViewModel.isLoading.collectAsState()
    val anyFeatureLoading by remember {
        derivedStateOf {
            usersIsLoading || personalityIsLoading || settingsIsLoading ||
                skillsIsLoading || combatIsLoading || goalsIsLoading ||
                questsIsLoading || metricsIsLoading || nodesIsLoading
        }
    }

    // Curtain stays up not just until feature data settles, but until the
    // combat-resume question has been answered — otherwise on a cold start
    // with an active combat the curtain drops while the map is still showing
    // and combat overlay/music are still racing to mount.
    val isStartupSettled by remember {
        derivedStateOf {
            !anyFeatureLoading && hasResolvedCombatOnStartup
        }
    }

    // Map music must NOT play while we're still figuring out whether to
    // resume a combat. Once that's resolved: if combat is visible, combat
    // music plays via CombatFlowScreen; if a conversation is visible, the
    // overlay fades the music out via Kiwi_Music_Conversation; otherwise
    // map music starts here.
    val shouldPlayMapMusic by remember {
        derivedStateOf {
            hasResolvedCombatOnStartup && !isCombatVisible && !isConversationVisible
        }
    }

    // Raised by the auth / sign-up screens the instant a map-bound action
    // begins (manual log in, auto log in once stored credentials are found,
    // the app-selection Confirm) — never at app launch or during sign-up
    // steps. Lowered here once the map's data has finished loading.
    val showAppLoading by usersViewModel.showAppLoading.collectAsState()

    LaunchedEffect(showAppLoading) {
        if (!showAppLoading) return@LaunchedEffect
        delay(INITIAL_LOADING_MIN_DISPLAY_MS)
        withTimeoutOrNull(INITIAL_LOADING_MAX_DISPLAY_MS) {
            while (true) {
                snapshotFlow { isStartupSettled }.first { it }
                val resumed =
                    withTimeoutOrNull(INITIAL_LOADING_SETTLE_MS) {
                        snapshotFlow { isStartupSettled }.first { !it }
                    }
                if (resumed == null) break
            }
        }
        usersViewModel.setShowAppLoading(false)
    }

    val loadingProgress = remember { Animatable(0f) }
    LaunchedEffect(showAppLoading) {
        if (showAppLoading) {
            loadingProgress.snapTo(0f)
            loadingProgress.animateTo(
                targetValue = PROGRESS_HOLD_TARGET,
                animationSpec = tween(durationMillis = PROGRESS_FILL_DURATION_MS, easing = EaseOut),
            )
        } else if (loadingProgress.value > 0f && loadingProgress.value < 1f) {
            loadingProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = PROGRESS_FINISH_DURATION_MS, easing = EaseInOut),
            )
        }
    }

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

    val revealEventScope = rememberCoroutineScope()

    val nodeEntryTransition = rememberNodeEntryTransitionController()
    // The viewmodels invoke the veil runner from viewModelScope, which has no
    // MonotonicFrameClock — calling Animatable.animateTo from there throws.
    // Bridge through a Compose-aware scope (backed by AndroidUiDispatcher) so
    // the animation work runs in a context that supplies the frame clock.
    val veilAnimationScope = rememberCoroutineScope()

    // Wire the same veil controller as the exit animation for conversation
    // and combat completion: when the user finishes a conversation or wins a
    // combat, the screen is dismissed under the veil rather than lerping off.
    // Set once on first composition (the controller is a stable remembered
    // instance, so the runner closure does not need to be re-installed).
    LaunchedEffect(nodeEntryTransition) {
        val veilRunner: suspend (suspend () -> Unit) -> Unit = { finalize ->
            veilAnimationScope
                .async {
                    nodeEntryTransition.enter()
                    finalize()
                    nodeEntryTransition.fadeOut()
                }.await()
        }
        conversationViewModel.setExitVeilRunner(veilRunner)
        combatViewModel.setExitVeilRunner(veilRunner)
    }

    DisposableEffect(nodeEntryTransition) {
        onDispose {
            conversationViewModel.setExitVeilRunner(null)
            combatViewModel.setExitVeilRunner(null)
        }
    }

    CompositionLocalProvider(LocalNodeEntryTransition provides nodeEntryTransition) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = LocalKiwiColors.current.color2,
                bottomBar = {
                    if (!isLoginScreen && isLoginCompleted) {
                        AppBarScreen(
                            navController = navController,
                            appBarViewModel = appBarViewModel,
                        )
                    }
                },
                content = { paddingValues ->
                    // Screens are sized to the area above the bar so their layouts
                    // stay centered. The bar's rounded corners reveal the Scaffold's
                    // containerColor (color2) underneath, which matches the screen
                    // backgrounds — so the overlay still reads as one continuous
                    // surface bleeding under the bar.
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
                            isCombatActive = activeCombat != null,
                            shouldPlayMapMusic = shouldPlayMapMusic,
                            isDialogueOverlaid = isDialogueOverlaid,
                        )

                        AnimatedVisibility(
                            visible = showDashboard && !isConversationVisible && !isCombatVisible,
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
                                combatViewModel.tryResumeActive()
                            }
                        }

                        AnimatedVisibility(
                            visible = isConversationVisible,
                            enter =
                                slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                                ) + fadeIn(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                            // The completion path runs the veil first via the runner,
                            // so this slide-out plays under a fully-opaque veil and
                            // is invisible to the user. It is kept as a fallback for
                            // the no-runner case (previews / tests).
                            exit =
                                slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                                ) + fadeOut(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                        ) {
                            activeConversation?.let { conversation ->
                                Box(modifier = Modifier.matchParentSize()) {
                                    Kiwi_Music_Conversation()
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

                        AnimatedVisibility(
                            visible = isCombatVisible && showDashboard,
                            enter =
                                slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                                ) + fadeIn(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                            // Victory routes through the veil runner — this slide-out
                            // is hidden under the opaque veil. Abandon / defeat keep
                            // the slide-out visible (they don't set the runner).
                            exit =
                                slideOutVertically(
                                    targetOffsetY = { fullHeight -> fullHeight },
                                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                                ) + fadeOut(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
                        ) {
                            activeCombat?.let { combat ->
                                CombatFlowScreen(
                                    combat = combat,
                                    deckSkills = skillsState?.deckSkills ?: emptyList(),
                                    isTurnPlaying = isCombatTurnPlaying,
                                    activeBark = activeBark,
                                    onBarkDismiss = combatViewModel::dismissBark,
                                    onConfirmAbandon = combatViewModel::confirmAbandon,
                                    onSkillClick = { skillId, skillName ->
                                        combatViewModel.executeTurn(skillId, skillName)
                                    },
                                    onApplyGoalProgress = { skillId, goalId, newProgress ->
                                        skillsViewModel.updateGoalProgress(skillId, goalId, newProgress)
                                    },
                                    onDismiss = combatViewModel::dismiss,
                                    onVictoryContinue = combatViewModel::onVictoryContinue,
                                )
                            }
                        }

                        TipModal(isTipVisible, tipsViewModel)

                        // Veil for the node-entry transition. Lives inside the
                        // Scaffold's content area on purpose so it covers the map
                        // / conversation / combat but leaves the bottom nav bar
                        // visible.
                        NodeEntryVeilOverlay(
                            controller = nodeEntryTransition,
                            modifier = Modifier.zIndex(50f),
                        )
                    }
                },
            )

            // Overlay global de notificaciones — único colector, siempre activo.
            // Suppressed while the loading curtain is up so a cold-start (combat
            // resume or otherwise) doesn't surface a queue of pop-ups before the
            // user has actually landed on a screen.
            if (!isLoginScreen && isLoginCompleted && !showAppLoading) {
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

            LoginLoadingScreen(
                visible = showAppLoading,
                progress = loadingProgress.value,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .zIndex(100f),
                onExitComplete = {
                    revealEventScope.launch {
                        EventBus.emitEvent(EventType.MAP_REVEAL, EventPayload.EmptyPayload())
                    }
                },
            )
        }
    }
}

@Composable
private fun TipModal(
    isTipVisible: Boolean,
    tipsViewModel: TipsViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = isTipVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
            exit = fadeOut(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
            )
        }

        AnimatedVisibility(
            visible = isTipVisible,
            enter =
                scaleIn(
                    initialScale = 0.6f,
                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                ) + fadeIn(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
            exit =
                scaleOut(
                    targetScale = 0.3f,
                    animationSpec = tween(durationMillis = 400, easing = EaseInOut),
                ) + fadeOut(animationSpec = tween(durationMillis = 400, easing = EaseInOut)),
        ) {
            TipScreen(tipsViewModel)
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

// The Map (HOME) is the anchor screen: any transition to/from it is vertical —
// the other screen rides up from / slides down to the bottom while the map holds
// still behind it. Every other screen is a horizontal sibling, ordered by its
// position in the bottom nav bar.
private val NAVBAR_ORDER =
    listOf(
        ScreenRoutes.HOME,
        ScreenRoutes.SKILLS,
        ScreenRoutes.OBJECTIVES,
        ScreenRoutes.WIP,
        ScreenRoutes.SETTINGS,
    )

private fun isMapRoute(route: String?): Boolean = route == ScreenRoutes.HOME

// The apps screen is reused as a "change apps" settings sub-screen. When it is
// reached from Settings (and popped back to it), we want the same rise-from-
// bottom / slide-down feel as the map transitions, distinct from the plain
// fade used during the sign-up flow.
private fun isSettingsSubScreenTransition(
    initial: String?,
    target: String?,
): Boolean {
    val settingsSubScreens =
        setOf(
            ScreenRoutes.SIGNUP4_APPS,
            ScreenRoutes.SETTINGS_ACCOUNT,
            ScreenRoutes.SETTINGS_AUDIO,
            ScreenRoutes.SETTINGS_SUPPORT,
        )
    return (initial == ScreenRoutes.SETTINGS && target in settingsSubScreens) ||
        (initial in settingsSubScreens && target == ScreenRoutes.SETTINGS)
}

// The login screen rides up from / slides down to the bottom over the app — the
// same vertical language as the map, but as a foreground curtain. This only
// covers login <-> a real nav-bar screen (logging in to HOME, logging out to
// LOGIN from Settings); transitions between login and the sign-up flow keep
// their plain fade. The full loading curtain still covers a cold-start login, so
// this lerp is only ever seen for an in-session log in / out.
private fun isLoginAppTransition(
    initial: String?,
    target: String?,
): Boolean =
    (initial == ScreenRoutes.LOGIN && navIndex(target) >= 0) ||
        (target == ScreenRoutes.LOGIN && navIndex(initial) >= 0)

// The sign-up welcome → account-form step slides sideways (and slides back on
// pop), distinct from the plain fade the rest of the sign-up flow uses. Both
// routes sit outside NAVBAR_ORDER, so direction is read from the route pair
// rather than nav indices.
private fun isWelcomeFormTransition(
    initial: String?,
    target: String?,
): Boolean =
    (initial == ScreenRoutes.SIGNUP1_WELCOME && target == ScreenRoutes.SIGNUP2_FORM) ||
        (initial == ScreenRoutes.SIGNUP2_FORM && target == ScreenRoutes.SIGNUP1_WELCOME)

// Focus variants share their base screen's slot so deep links order correctly.
private fun navIndex(route: String?): Int =
    when (route) {
        ScreenRoutes.SKILLS_FOCUS -> NAVBAR_ORDER.indexOf(ScreenRoutes.SKILLS)
        ScreenRoutes.OBJECTIVES_FOCUS -> NAVBAR_ORDER.indexOf(ScreenRoutes.OBJECTIVES)
        else -> NAVBAR_ORDER.indexOf(route)
    }

private fun screenOffsetSpec(durationMs: Int = SCREEN_TRANSITION_MS) = tween<IntOffset>(durationMillis = durationMs, easing = EaseInOut)

private fun screenFadeSpec(durationMs: Int = SCREEN_TRANSITION_MS) = tween<Float>(durationMillis = durationMs, easing = EaseInOut)

private fun screenEnter(
    initial: String?,
    target: String?,
): EnterTransition {
    val from = navIndex(initial)
    val to = navIndex(target)
    return when {
        // Settings → sub-screens (Change Apps, Account, Audio, Support) rise from the
        // bottom; popping back reveals Settings underneath instantly while the
        // sub-screen slides down.
        isSettingsSubScreenTransition(initial, target) -> {
            if (target != ScreenRoutes.SETTINGS) {
                slideInVertically(animationSpec = screenOffsetSpec(MAP_TRANSITION_MS)) { it }
            } else {
                EnterTransition.None
            }
        }

        // Sign-up welcome ↔ account form: slide toward the form going forward,
        // back toward welcome on pop.
        isWelcomeFormTransition(initial, target) -> {
            if (target == ScreenRoutes.SIGNUP2_FORM) {
                slideInHorizontally(animationSpec = screenOffsetSpec()) { it } +
                    fadeIn(animationSpec = screenFadeSpec())
            } else {
                slideInHorizontally(animationSpec = screenOffsetSpec()) { -it } +
                    fadeIn(animationSpec = screenFadeSpec())
            }
        }

        // Login curtain: the entering screen (login on logout, the app on an
        // in-session login) lifts up from the bottom over the one it replaces.
        isLoginAppTransition(initial, target) -> {
            slideInVertically(animationSpec = screenOffsetSpec(MAP_TRANSITION_MS)) { it }
        }

        // Login / sign-up / any non-nav screen: keep a plain fade.
        from < 0 || to < 0 -> {
            fadeIn(animationSpec = screenFadeSpec())
        }

        // Going to the map: it is already rendered behind (this is a pop), so it
        // just appears instantly while the leaving screen slides down to reveal it.
        isMapRoute(target) -> {
            EnterTransition.None
        }

        // Leaving the map: the new screen rises from the bottom at full opacity.
        isMapRoute(initial) -> {
            slideInVertically(animationSpec = screenOffsetSpec(MAP_TRANSITION_MS)) { it }
        }

        // Horizontal siblings: enter from the side we are moving toward.
        to > from -> {
            slideInHorizontally(animationSpec = screenOffsetSpec()) { it } +
                fadeIn(animationSpec = screenFadeSpec())
        }

        else -> {
            slideInHorizontally(animationSpec = screenOffsetSpec()) { -it } +
                fadeIn(animationSpec = screenFadeSpec())
        }
    }
}

private fun screenExit(
    initial: String?,
    target: String?,
): ExitTransition {
    val from = navIndex(initial)
    val to = navIndex(target)
    return when {
        // Settings → sub-screens: Settings holds still while the sub-screen rises
        // over it; on the way back the sub-screen slides down to reveal Settings.
        isSettingsSubScreenTransition(initial, target) -> {
            if (initial != ScreenRoutes.SETTINGS) {
                slideOutVertically(animationSpec = screenOffsetSpec(MAP_REVEAL_MS)) { it }
            } else {
                ExitTransition.None
            }
        }

        // Sign-up welcome ↔ account form: the leaving screen slides off toward
        // the side the new one enters from.
        isWelcomeFormTransition(initial, target) -> {
            if (initial == ScreenRoutes.SIGNUP1_WELCOME) {
                slideOutHorizontally(animationSpec = screenOffsetSpec()) { -it } +
                    fadeOut(animationSpec = screenFadeSpec())
            } else {
                slideOutHorizontally(animationSpec = screenOffsetSpec()) { it } +
                    fadeOut(animationSpec = screenFadeSpec())
            }
        }

        // The screen being covered by the rising login curtain (or the app it
        // reveals) holds still underneath while the other screen lerps over it.
        isLoginAppTransition(initial, target) -> {
            ExitTransition.None
        }

        from < 0 || to < 0 -> {
            fadeOut(animationSpec = screenFadeSpec())
        }

        // Going to the map: the leaving screen slides straight down at full opacity,
        // slower so the reveal reads clearly.
        isMapRoute(target) -> {
            slideOutVertically(animationSpec = screenOffsetSpec(MAP_REVEAL_MS)) { it }
        }

        // Leaving the map: the map holds still while the new screen rises over it.
        isMapRoute(initial) -> {
            ExitTransition.None
        }

        // Horizontal siblings: leave toward the opposite side.
        to > from -> {
            slideOutHorizontally(animationSpec = screenOffsetSpec()) { -it } +
                fadeOut(animationSpec = screenFadeSpec())
        }

        else -> {
            slideOutHorizontally(animationSpec = screenOffsetSpec()) { it } +
                fadeOut(animationSpec = screenFadeSpec())
        }
    }
}

@Composable
private fun AnimatedContentScope.MapTransitionTopRadius(content: @Composable () -> Unit) {
    val cornerRadius = getResponsiveSizeHeight(50.dp)
    val radius by transition.animateDp(
        transitionSpec = { tween(durationMillis = MAP_TRANSITION_MS, easing = EaseInOut) },
        label = "screenTopRadius",
    ) { state ->
        if (state == EnterExitState.Visible) 0.dp else cornerRadius
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = radius, topEnd = radius)),
    ) {
        content()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
@Suppress("LongMethod")
fun AppNavHost(
    navController: NavHostController,
    usersViewModel: UsersViewModel,
    settingsViewModel: ISettingsViewModel,
    personalityViewModel: IPersonalityViewModel,
    nodesViewModel: INodesViewModel,
    questsViewModel: IQuestsViewModel,
    goalsViewModel: IGoalsViewModel,
    skillsViewModel: ISkillsViewModel,
    isCombatActive: Boolean = false,
    shouldPlayMapMusic: Boolean = true,
    isDialogueOverlaid: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.LOGIN,
        enterTransition = { screenEnter(initialState.destination.route, targetState.destination.route) },
        exitTransition = { screenExit(initialState.destination.route, targetState.destination.route) },
        popEnterTransition = { screenEnter(initialState.destination.route, targetState.destination.route) },
        popExitTransition = { screenExit(initialState.destination.route, targetState.destination.route) },
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
                    skillsViewModel = skillsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SIGNUP4_APPS) {
            AppScreenWrapper {
                Kiwi_Music_SignUp()
                SignUpScreen4_Apps(
                    usersViewModel = usersViewModel,
                    personalityViewModel = personalityViewModel,
                    goalsViewModel = goalsViewModel,
                    nodesViewModel = nodesViewModel,
                    skillsViewModel = skillsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.HOME) {
            AppScreenWrapper {
                // Gated so a cold-start combat resume doesn't flash map music
                // before CombatFlowScreen takes over the audio.
                if (shouldPlayMapMusic) {
                    Kiwi_Music_Home()
                }
                MapScreen(
                    nodesViewModel = nodesViewModel,
                    goalsViewModel = goalsViewModel,
                    mapViewModel = hiltViewModel(),
                    usersViewModel = usersViewModel,
                    isDialogueOverlaid = isDialogueOverlaid,
                )
            }
        }

        composable(ScreenRoutes.OBJECTIVES) {
            MapTransitionTopRadius {
                AppScreenWrapper {
                    ObjectivesScreen(
                        questsViewModel = questsViewModel,
                        goalsViewModel = goalsViewModel,
                    )
                }
            }
        }

        composable(
            route = ScreenRoutes.OBJECTIVES_FOCUS,
            arguments = listOf(navArgument("questId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId")

            MapTransitionTopRadius {
                ObjectivesScreen(
                    questsViewModel = questsViewModel,
                    focusedQuestId = questId,
                    goalsViewModel = goalsViewModel,
                )
            }
        }

        composable(ScreenRoutes.SKILLS) {
            MapTransitionTopRadius {
                AppScreenWrapper {
                    SkillsScreen(
                        skillsViewModel = skillsViewModel,
                        isDeckLocked = isCombatActive,
                    )
                }
            }
        }

        composable(
            route = ScreenRoutes.SKILLS_FOCUS,
            arguments = listOf(navArgument("skillId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getLong("skillId")

            MapTransitionTopRadius {
                SkillsScreen(
                    skillsViewModel = skillsViewModel,
                    focusedSkillId = questId,
                    isDeckLocked = isCombatActive,
                )
            }
        }

        composable(ScreenRoutes.SETTINGS) {
            AppScreenWrapper {
                Kiwi_Music_Settings()
                SettingsScreen(
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SETTINGS_ACCOUNT) {
            AppScreenWrapper {
                SettingsAccountScreen(
                    usersViewModel = usersViewModel,
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SETTINGS_AUDIO) {
            AppScreenWrapper {
                SettingsAudioScreen(
                    settingsViewModel = settingsViewModel,
                    navController = navController,
                )
            }
        }

        composable(ScreenRoutes.SETTINGS_SUPPORT) {
            AppScreenWrapper {
                SettingsSupportScreen(
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
