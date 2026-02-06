package com.bellako.kiwi.features.dashboard.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_DraggableBar
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.common.utils.SECONDS_IN_HOUR
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.map.model.MapViewModel
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.metrics.model.MetricsProvider
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.nodes.tests.NodesFakeViewModel
import com.bellako.kiwi.features.notifications.controller.NotificationManager
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import java.time.LocalDate

const val MONTH_SLIDE_ANIM_DURATION = 300

const val STATE_HEIGHT_0 = 140
const val STATE_HEIGHT_1 = 270
const val STATE_HEIGHT_2 = 680
val STATES = listOf(STATE_HEIGHT_0, STATE_HEIGHT_1, STATE_HEIGHT_2)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    personalityViewModel: IPersonalityViewModel,
    goalsViewModel: IGoalsViewModel,
    showCalendarView: Boolean = false,
    initialLayout: DashboardLayout = DashboardLayout.HIDDEN,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val metricsState by metricsViewModel.state.collectAsState()
    val metricsIsLoading by metricsViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    val isLoading by remember { derivedStateOf { metricsIsLoading || personalityIsLoading } }

    val kiwiColors = LocalKiwiColors.current
    val shouldShowCalendarView = remember { mutableStateOf(showCalendarView) }

    val draggableStateIndex = remember { mutableIntStateOf(initialLayout.value) }

    LaunchedEffect(Unit) {
        listenToEvent(EventType.CHANGE_DASHBOARD_LAYOUT) { eventPayload ->
            val payload = eventPayload as EventPayload.ChangeDashboardLayoutPayload
            draggableStateIndex.intValue = payload.newLayout.value
        }
    }

    LaunchedEffect(draggableStateIndex.intValue) {
        if (draggableStateIndex.intValue == 0) {
            loadMetrics(dateToString(LocalDate.now()), metricsViewModel, personalityViewModel, context)
        }

        if (draggableStateIndex.intValue <= 1) {
            shouldShowCalendarView.value = false
        }
    }

    Kiwi_DraggableBar(
        modifier =
            Modifier
                .testTag(DashboardModalTestTags.DRAGGABLE_NODE),
        states = STATES,
        currentStateIndex = draggableStateIndex.intValue,
        onStateChange = { newIndex -> draggableStateIndex.intValue = newIndex },
        content = { currentStateIndex ->
            Column(
                modifier =
                    Modifier
                        .background(kiwiColors.color2)
                        .padding(
                            top = 0.dp,
                            bottom = getResponsiveSizeHeight(Spacing.medium),
                            start = getResponsiveSizeHeight(Spacing.medium),
                            end = getResponsiveSizeHeight(Spacing.medium),
                        ).fillMaxWidth()
                        .testTag(CommonTestTags.DASHBOARD_MODAL),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Header()

                if (currentStateIndex == 0) {
                    // TODO pedir de nuevo el dia actual
                    DashboardScreen0_Hidden()
                } else if (currentStateIndex <= 1) {
                    DashboardScreen1_Collapsed(
                        metricsState = metricsState!!,
                        isLoading = isLoading,
                        onCalendarViewClicked = {
                            shouldShowCalendarView.value = true
                            draggableStateIndex.intValue = 2
                        },
                    )
                } else if (currentStateIndex <= 2) {
                    DashboardScreen2_Expanded(
                        context = context,
                        coroutineScope = coroutineScope,
                        usersViewModel = usersViewModel,
                        metricsViewModel = metricsViewModel,
                        metricsState = metricsState!!,
                        personalityViewModel = personalityViewModel,
                        goalsViewModel = goalsViewModel,
                        shouldShowCalendarView = shouldShowCalendarView,
                        isLoading = isLoading,
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier =
                        Modifier
                            .background(kiwiColors.color2.copy(alpha = KIWI_DISABLED_ALPHA))
                            .fillMaxWidth()
                            .height(
                                getResponsiveSizeHeight(STATES[currentStateIndex]).dp -
                                    getResponsiveSizeHeight(100.dp), // appbar
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (currentStateIndex > 0) {
                        LoadingModal()
                    }
                }
            }
        },
    )
}

@Composable
fun ComposableEngagementMeasuring(layout: String) {
    DisposableEffect(Unit) {
        val composeTime = System.currentTimeMillis()
        onDispose {
            val visibleTime = System.currentTimeMillis() - composeTime
            firebaseLogEvent(
                FirebaseEventNames.DASHBOARD_LAYOUT_ENGAGEMENT,
                mapOf(
                    "layout" to layout,
                    "visible_time_ms" to visibleTime,
                ),
            )
        }
    }
}

@Composable
private fun Header() {
    Kiwi_Spacer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Kiwi_H3(
            KiwiTextArguments(
                ":: Daily Progress ::",
                TextAlign.Center,
                LocalKiwiColors.current.color6,
            ),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun loadMetrics(
    date: String,
    metricsViewModel: IMetricsViewModel,
    personalityViewModel: IPersonalityViewModel,
    context: Context,
) {
    if (date == metricsViewModel.state.value!!.date) {
        return
    }
    metricsViewModel.onDateChanged(stringToDate(date))

    val metricsState = metricsViewModel.state.value!!
    val personalityState = personalityViewModel.state.value!!

    var deviceMetrics = MetricsProvider.getDeviceMetrics(context, metricsState, personalityState)
    metricsViewModel.loadMetrics(date).fold(
        onSuccess = { _ ->
            if (deviceMetrics.currentGoodTimeSeconds < metricsState.currentGoodTimeSeconds) {
                deviceMetrics = deviceMetrics.copy(currentGoodTimeSeconds = metricsState.currentGoodTimeSeconds)
            }
            if (deviceMetrics.currentBadTimeSeconds < metricsState.currentBadTimeSeconds) {
                deviceMetrics = deviceMetrics.copy(currentBadTimeSeconds = metricsState.currentBadTimeSeconds)
            }
            if (deviceMetrics != metricsState) {
                metricsViewModel.updateMetrics(deviceMetrics)
            }
        },
        onFailure = { _ ->
            metricsViewModel.createMetrics(deviceMetrics)
        },
    )
}

@Composable
fun SelectedMetricsTime(
    maxSeconds: Int,
    currentSeconds: Int,
    validMetrics: Boolean,
    expanded: Boolean,
    tag: String,
) {
    val kiwiColors = LocalKiwiColors.current

    val currentString =
        if (validMetrics) {
            DateUtils.parseTimeSeconds(currentSeconds)
        } else {
            "No data"
        }

    val maxString =
        if (validMetrics) {
            "/" + DateUtils.parseTimeSeconds(maxSeconds)
        } else {
            ""
        }

    if (expanded) {
        Kiwi_H1(
            KiwiTextArguments(
                text = currentString,
                TextAlign.Center,
                color = kiwiColors.color9,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(tag),
            ),
        )

        Kiwi_Label2(
            KiwiTextArguments(
                maxString,
                TextAlign.Center,
                color = kiwiColors.color7D,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ),
        )
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Kiwi_Label2(
                KiwiTextArguments(
                    currentString,
                    TextAlign.Left,
                    color = kiwiColors.color9,
                    modifier =
                        Modifier
                            .testTag(tag),
                ),
            )

            Kiwi_Label2(
                KiwiTextArguments(
                    maxString,
                    TextAlign.Left,
                    color = kiwiColors.color7D,
                ),
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("EmptyFunctionBlock")
@Composable
fun DashboardModal_Preview_Hidden() {
    DashboardModal_Preview(false, DashboardLayout.HIDDEN)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("EmptyFunctionBlock")
@Composable
fun DashboardModal_Preview_Collapsed() {
    DashboardModal_Preview(false, DashboardLayout.COLLAPSED)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("EmptyFunctionBlock")
@Composable
fun DashboardModal_Preview_Expanded() {
    DashboardModal_Preview(false, DashboardLayout.EXPANDED)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("EmptyFunctionBlock")
@Composable
fun DashboardModal_Preview_Expanded_Calendar() {
    DashboardModal_Preview(true, DashboardLayout.EXPANDED)
}

@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardModal_Preview(
    showCalendarView: Boolean,
    initialLayout: DashboardLayout = DashboardLayout.HIDDEN,
    nodesViewModel: NodesFakeViewModel = NodesFakeViewModel(),
    goalsViewModel: GoalsFakeViewModel = GoalsFakeViewModel(),
) {
    val nav = rememberNavController()
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = nav)
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapScreen(
                        nodesViewModel = nodesViewModel,
                        navController = nav,
                        goalsViewModel = goalsViewModel,
                        mapViewModel = MapViewModel(),
                        notificationManager = NotificationManager(),
                    )
                    DashboardScreen(
                        usersViewModel =
                            UsersFakeViewModel(
                                UsersState(
                                    validUsersDTO().email,
                                    validUsersDTO().password,
                                    validUsersDTO().registerDate,
                                ),
                            ),
                        metricsViewModel =
                            MetricsFakeViewModel(
                                MetricsState(
                                    date = "2025-06-12",
                                    maxGoodTimeSeconds = 6 * SECONDS_IN_HOUR,
                                    currentGoodTimeSeconds = 1 * SECONDS_IN_HOUR,
                                    maxBadTimeSeconds = 6 * SECONDS_IN_HOUR,
                                    currentBadTimeSeconds = 2 * SECONDS_IN_HOUR,
                                ),
                            ),
                        personalityViewModel =
                            PersonalityFakeViewModel(
                                PersonalityState(
                                    validPersonalityDTO().realName,
                                    validPersonalityDTO().knightName,
                                    validPersonalityDTO().build,
                                    validPersonalityDTO().goodApps,
                                    validPersonalityDTO().badApps,
                                    validPersonalityDTO().neutralApps,
                                ),
                            ),
                        goalsViewModel = goalsViewModel,
                        showCalendarView,
                        initialLayout = initialLayout,
                    )
                }
            },
        )
    }
}

enum class DashboardLayout(
    val value: Int,
) {
    HIDDEN(0),
    COLLAPSED(1),
    EXPANDED(2),
}
