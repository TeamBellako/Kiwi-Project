package com.bellako.kiwi.features.dashboard.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalState
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.GoalsListState
import com.bellako.kiwi.features.goals.model.GoalsViewModel
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.screens.GoalComponent
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

val LocalGoalsViewModel = compositionLocalOf<IGoalsViewModel?> { null }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen2_Expanded(
    context: Context,
    coroutineScope: CoroutineScope,
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    metricsState: MetricsState,
    personalityViewModel: IPersonalityViewModel,
    shouldShowCalendarView: MutableState<Boolean>,
    isLoading: Boolean,
) {
    ComposableEngagementMeasuring("expanded")
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_Spacer(Spacing.xSmall)

        SelectedDayText(metricsState)

        if (shouldShowCalendarView.value) {
            CalendarMonthView(
                context = context,
                isLoading = isLoading,
                coroutineScope = coroutineScope,
                usersViewModel = usersViewModel,
                metricsViewModel = metricsViewModel,
                metricsState = metricsState,
                shouldShowCalendarView = shouldShowCalendarView,
                personalityViewModel = personalityViewModel,
            )
        } else {
            Kiwi_Spacer(Spacing.small)

            @Suppress("MagicNumber")
            CurrentDayIndicator(getResponsiveSizeHeight(180.dp), 0.7f, 0.20f)

            CalendarWeekView(
                context = context,
                coroutineScope = coroutineScope,
                usersViewModel = usersViewModel,
                metricsViewModel = metricsViewModel,
                metricsState = metricsState,
                personalityViewModel = personalityViewModel,
                isLoading = isLoading,
            ) {
                shouldShowCalendarView.value = true
            }

            ExpandedProgressBox(metricsState)
        }
    }
}

@Composable
private fun ExpandedProgressBox(state: MetricsState) {
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

            ExpandedSummaryCard(state)

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
                ExpandedMetricProgressTitle("Good Apps Time")
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
                ExpandedMetricProgressTitle("Evil Apps Time")
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
private fun ExpandedSummaryCard(metricsState: MetricsState) {
    val localViewModel = LocalGoalsViewModel.current
    val viewModel = localViewModel ?: hiltViewModel<GoalsViewModel>()
    var goals by remember { mutableStateOf<List<GoalDomain>>(emptyList()) }

    LaunchedEffect(metricsState.date) {
        val result = viewModel.getGoalsByDate(metricsState.date)
        if (result.isSuccess) {
            goals = result.getOrNull() ?: emptyList()
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        goals.forEach { goal ->
            key(goal.id) {
                GoalComponent(goal)
//                ExpandedGoalComponent(goal)
                Kiwi_Spacer(Spacing.xSmall)
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
    val mockViewModel =
        remember {
            object : IGoalsViewModel {
                override val state: StateFlow<GoalsListState> = MutableStateFlow(GoalsListState())

                override fun onDateChanged(newDate: LocalDate) {}

                override suspend fun createGoals(
                    date: String,
                    goals: List<GoalState>,
                ): Result<Unit> = Result.success(Unit)

                override suspend fun completeGoal(goalId: String): Result<Unit> = Result.success(Unit)

                override suspend fun uncompleteGoal(goalId: String): Result<Unit> = Result.success(Unit)

                override suspend fun loadAllGoals(): Result<Unit> = Result.success(Unit)

                override suspend fun getGoalsByDate(date: String) =
                    Result.success(
                        listOf<GoalDomain>(
                            GoalDomain(
                                "1",
                                "Test objective",
                                "Test description",
                                GoalType.EXERCISE,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.NOT_COMPLETED,
                                100,
                                0.5f,
                            ),
                            GoalDomain(
                                "2",
                                "Test objective 2",
                                "Test description 2",
                                GoalType.MEDITATION,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.NOT_COMPLETED,
                                200,
                                0.7f,
                            ),
                        ),
                    )

                override suspend fun getGoalsInProgress() = Result.success(emptyList<GoalDomain>())
            }
        }

    CompositionLocalProvider(LocalGoalsViewModel provides mockViewModel) {
        DashboardModal_Preview(false, 2)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 360, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Large Phone", widthDp = 412, heightDp = 915)
@Suppress("MagicNumber", "EmptyFunctionBlock")
@Composable
fun DashboardModal2_Expanded_Calendar_Preview() {
    val mockViewModel =
        remember {
            object : IGoalsViewModel {
                override val state: StateFlow<GoalsListState> = MutableStateFlow(GoalsListState())

                override fun onDateChanged(newDate: LocalDate) {}

                override suspend fun createGoals(
                    date: String,
                    goals: List<GoalState>,
                ): Result<Unit> = Result.success(Unit)

                override suspend fun completeGoal(goalId: String): Result<Unit> = Result.success(Unit)

                override suspend fun uncompleteGoal(goalId: String): Result<Unit> = Result.success(Unit)

                override suspend fun loadAllGoals(): Result<Unit> = Result.success(Unit)

                override suspend fun getGoalsByDate(date: String) =
                    Result.success(
                        listOf<GoalDomain>(
                            GoalDomain(
                                "1",
                                "Test objective",
                                "Test description",
                                GoalType.EXERCISE,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.NOT_COMPLETED,
                                100,
                                0.5f,
                            ),
                            GoalDomain(
                                "2",
                                "Test objective 2",
                                "Test description 2",
                                GoalType.MEDITATION,
                                GoalCategory.DAILY_CHALLENGES,
                                GoalStatus.NOT_COMPLETED,
                                200,
                                0.7f,
                            ),
                        ),
                    )

                override suspend fun getGoalsInProgress() = Result.success(emptyList<GoalDomain>())
            }
        }

    CompositionLocalProvider(LocalGoalsViewModel provides mockViewModel) {
        DashboardModal_Preview(true, 2)
    }
}
