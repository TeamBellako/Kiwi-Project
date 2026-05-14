package com.bellako.kiwi.features.dashboard.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.common.services.eventbus.listenToEvent
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalComponent
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.CoroutineScope

val LocalGoalsViewModel = compositionLocalOf<IGoalsViewModel?> { null }

@Suppress("MagicNumber")
private fun Modifier.featherHorizontalEdges(): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            drawRect(
                brush =
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.12f to Color.Black,
                        0.88f to Color.Black,
                        1f to Color.Transparent,
                    ),
                blendMode = BlendMode.DstIn,
            )
        }

@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen2_Expanded(
    context: Context,
    coroutineScope: CoroutineScope,
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    metricsState: MetricsState,
    personalityViewModel: IPersonalityViewModel,
    goalsViewModel: IGoalsViewModel,
    shouldShowCalendarView: MutableState<Boolean>,
    isLoading: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    var dailyGoalProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(metricsState) {
        dailyGoalProgress = goalsViewModel.getDailyGoalsProgress(metricsState.date)
    }

    val currentDate by rememberUpdatedState(metricsState.date)
    LaunchedEffect(Unit) {
        listenToEvent(EventType.DAILY_GOALS_UPDATED) {
            dailyGoalProgress = goalsViewModel.getDailyGoalsProgress(currentDate)
        }
    }

    val animatedDailyGoalProgress by animateFloatAsState(
        targetValue = dailyGoalProgress,
        animationSpec = tween(durationMillis = 600),
        label = "dailyGoalProgress",
    )

    var appUsageProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(metricsState) {
        appUsageProgress = metricsState.getAppUsageProgress()
    }

    val animatedAppUsageProgress by animateFloatAsState(
        targetValue = appUsageProgress,
        animationSpec = tween(durationMillis = 600),
        label = "appUsageProgress",
    )

    val dayTransitionDirection = remember { mutableIntStateOf(0) }

    val dayTransitionSpec: AnimatedContentTransitionScope<String>.() -> ContentTransform = {
        val direction = dayTransitionDirection.intValue
        if (direction == 0) {
            fadeIn(animationSpec = tween(DAY_TRANSITION_ANIM_DURATION)) togetherWith
                fadeOut(animationSpec = tween(DAY_TRANSITION_ANIM_DURATION))
        } else {
            (
                slideInHorizontally(
                    animationSpec = tween(DAY_TRANSITION_ANIM_DURATION),
                    initialOffsetX = { fullWidth -> fullWidth * direction },
                ) + fadeIn(animationSpec = tween(DAY_TRANSITION_ANIM_DURATION))
            ) togetherWith
                (
                    slideOutHorizontally(
                        animationSpec = tween(DAY_TRANSITION_ANIM_DURATION),
                        targetOffsetX = { fullWidth -> -fullWidth * direction },
                    ) + fadeOut(animationSpec = tween(DAY_TRANSITION_ANIM_DURATION))
                )
        }
    }

    ComposableEngagementMeasuring("expanded")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_Spacer(Spacing.xSmall)

        AnimatedContent(
            targetState = metricsState.date,
            transitionSpec = dayTransitionSpec,
            label = "selectedDayText",
        ) { _ ->
            SelectedDayText(metricsState)
        }

        Crossfade(
            targetState = shouldShowCalendarView.value,
            animationSpec = tween(DAY_TRANSITION_ANIM_DURATION),
            label = "calendarWeekToggle",
        ) { showCalendar ->
            if (showCalendar) {
                CalendarMonthView(
                    context = context,
                    isLoading = isLoading,
                    coroutineScope = coroutineScope,
                    usersViewModel = usersViewModel,
                    metricsViewModel = metricsViewModel,
                    metricsState = metricsState,
                    shouldShowCalendarView = shouldShowCalendarView,
                    personalityViewModel = personalityViewModel,
                    goalsViewModel = goalsViewModel,
                    dayTransitionDirection = dayTransitionDirection,
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Kiwi_Spacer(Spacing.small)

                    AnimatedContent(
                        targetState = metricsState.date,
                        transitionSpec = dayTransitionSpec,
                        label = "currentDayIndicator",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .featherHorizontalEdges(),
                        contentAlignment = Alignment.Center,
                    ) { _ ->
                        with(sharedTransitionScope) {
                            @Suppress("MagicNumber")
                            CurrentDayIndicator(
                                size = getResponsiveSizeHeight(180.dp),
                                dailyGoalsProgress = animatedDailyGoalProgress,
                                appUsageProgress = animatedAppUsageProgress,
                                modifier =
                                    Modifier.sharedElement(
                                        rememberSharedContentState(key = "currentDayHeart"),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    ),
                            )
                        }
                    }

                    CalendarWeekView(
                        context = context,
                        coroutineScope = coroutineScope,
                        usersViewModel = usersViewModel,
                        metricsViewModel = metricsViewModel,
                        metricsState = metricsState,
                        personalityViewModel = personalityViewModel,
                        isLoading = isLoading,
                        goalsViewModel = goalsViewModel,
                        dayTransitionDirection = dayTransitionDirection,
                    ) {
                        shouldShowCalendarView.value = true
                    }

                    AnimatedContent(
                        targetState = metricsState.date,
                        transitionSpec = dayTransitionSpec,
                        label = "expandedProgressBox",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .featherHorizontalEdges(),
                        contentAlignment = Alignment.TopCenter,
                    ) { _ ->
                        ExpandedProgressBox(metricsState, goalsViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedProgressBox(
    state: MetricsState,
    goalsViewModel: IGoalsViewModel,
) {
    Box(
        modifier =
            Modifier
                .padding(horizontal = getResponsiveSizeHeight(Spacing.large))
                .wrapContentHeight()
                .clip(RoundedCornerShape(getResponsiveSizeHeight(40.dp)))
                .background(LocalKiwiColors.current.color3),
    ) {
        Column {
            Kiwi_Spacer()

            ExpandedMetricsProgress(state)

            Kiwi_Spacer(Spacing.small)

            ExpandedSummaryCard(state, goalsViewModel)

            Kiwi_Spacer(Spacing.small)
        }
    }
}

@Composable
private fun ExpandedMetricProgressTitle(title: String) {
    Kiwi_P2(
        KiwiTextArguments(
            title,
            TextAlign.Center,
            LocalKiwiColors.current.color9,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ),
    )
}

@Composable
private fun ExpandedMetricsProgress(state: MetricsState) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Column {
                ExpandedMetricProgressTitle("Good Apps")
                SelectedMetricsTime(
                    state.maxGoodTimeSeconds,
                    state.currentGoodTimeSeconds,
                    state.currentGoodTimeSeconds > 0 || state.currentBadTimeSeconds > 0,
                    true,
                    DashboardModalTestTags.GOOD_TIME,
                )
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column {
                ExpandedMetricProgressTitle("Evil Apps")
                SelectedMetricsTime(
                    state.maxBadTimeSeconds,
                    state.currentBadTimeSeconds,
                    state.currentGoodTimeSeconds > 0 || state.currentBadTimeSeconds > 0,
                    true,
                    DashboardModalTestTags.BAD_TIME,
                )
            }
        }
    }
}

@Composable
private fun ExpandedSummaryCard(
    metricsState: MetricsState,
    goalsViewModel: IGoalsViewModel,
) {
    var goals by remember { mutableStateOf<List<UserGoalStatusDomain>>(emptyList()) }

    LaunchedEffect(metricsState.date) {
        val result = goalsViewModel.getGoalsByDate(metricsState.date)
        if (result.isSuccess) {
            goals = result.getOrNull() ?: emptyList()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = getResponsiveSizeWidth(Spacing.medium)),
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                "Challenges",
                TextAlign.Center,
                LocalKiwiColors.current.color6,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
            ),
        )

        if (goals.isEmpty()) {
            Kiwi_Spacer(Spacing.small)

            @Suppress("MagicNumber")
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(11555f / (2113f * 2f))
                        .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp)))
                        .background(LocalKiwiColors.current.color2B),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_P2(
                    KiwiTextArguments(
                        "No Daily Challenges",
                        TextAlign.Center,
                        LocalKiwiColors.current.color7D,
                    ),
                )
            }
        } else {
            goals.forEach { goal ->
                key(goal.id) {
                    GoalComponent(goal, goalsViewModel)
//                ExpandedGoalComponent(goal)
                    Kiwi_Spacer(Spacing.xSmall)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 360, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Large Phone", widthDp = 412, heightDp = 915)
@Suppress("MagicNumber", "EmptyFunctionBlock")
@Composable
fun DashboardModal2_Preview_Expanded() {
    val fakeGoalsViewModel = remember { GoalsFakeViewModel() }
    DashboardModal_Preview(false, DashboardLayout.EXPANDED, goalsViewModel = fakeGoalsViewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 360, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Large Phone", widthDp = 412, heightDp = 915)
@Suppress("MagicNumber", "EmptyFunctionBlock")
@Composable
fun DashboardModal2_Expanded_Calendar_Preview() {
    val fakeGoalsViewModel = remember { GoalsFakeViewModel() }
    DashboardModal_Preview(true, DashboardLayout.EXPANDED, goalsViewModel = fakeGoalsViewModel)
}
