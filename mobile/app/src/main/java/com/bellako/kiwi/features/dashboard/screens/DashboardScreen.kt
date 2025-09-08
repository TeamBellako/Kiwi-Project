package com.bellako.kiwi.features.dashboard.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P1
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_DraggableBar
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.common.utils.DAYS_IN_WEEK
import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.common.utils.SECONDS_IN_HOUR
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.map.screens.MapScreen
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.metrics.model.MetricsProvider
import com.bellako.kiwi.features.metrics.tests.MetricsFakeViewModel
import com.bellako.kiwi.features.personality.data.PersonalityState
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityFakeViewModel
import com.bellako.kiwi.features.personality.tests.PersonalityTestFactory.validPersonalityDTO
import com.bellako.kiwi.features.users.data.UsersState
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.features.users.tests.UsersFakeViewModel
import com.bellako.kiwi.features.users.tests.UsersTestFactory.validUsersDTO
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getScreenHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

const val MONTH_SLIDE_ANIM_DURATION = 300
const val DAY_DISABLED_ALPHA = 0.3f

const val STATE_HEIGHT_0 = 150
const val STATE_HEIGHT_1 = 260
const val STATE_HEIGHT_2 = 650
val STATES = listOf(STATE_HEIGHT_0, STATE_HEIGHT_1, STATE_HEIGHT_2)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    personalityViewModel: IPersonalityViewModel,
    showCalendarView: Boolean = false,
    initialStateIndex: Int = 0,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val metricsState by metricsViewModel.state.collectAsState()
    val metricsIsLoading by metricsViewModel.isLoading.collectAsState()
    val personalityIsLoading by personalityViewModel.isLoading.collectAsState()

    val isLoading by remember { derivedStateOf { metricsIsLoading || personalityIsLoading } }

    LaunchedEffect(Unit) {
        loadMetrics(dateToString(LocalDate.now()), metricsViewModel, personalityViewModel, context)
    }

    val shouldShowCalendarView = remember { mutableStateOf(showCalendarView) }

    Kiwi_DraggableBar(
        modifier = Modifier.testTag(DashboardModalTestTags.DRAGGABLE_NODE),
        states = STATES,
        content = { currentStateIndex ->
            Column(
                modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
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
                    HiddenContent()
                } else if (currentStateIndex <= 1) {
                    CollapsedContent(
                        metricsState = metricsState!!,
                        isLoading = isLoading,
                        onCalendarViewClicked = {
                            shouldShowCalendarView.value = true
                        },
                    )
                } else if (currentStateIndex <= 2) {
                    ExpandedContent(
                        context = context,
                        coroutineScope = coroutineScope,
                        usersViewModel = usersViewModel,
                        metricsViewModel = metricsViewModel,
                        metricsState = metricsState!!,
                        personalityViewModel = personalityViewModel,
                        shouldShowCalendarView = shouldShowCalendarView,
                        isLoading = isLoading,
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
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
        initialStateIndex = initialStateIndex,
    )
}

@Composable
private fun ComposableEngagementMeasuring(layout: String) {
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
private fun HiddenContent() {
    ComposableEngagementMeasuring("hidden")
}

@Composable
private fun CollapsedContent(
    metricsState: MetricsState,
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    ComposableEngagementMeasuring("collapsed")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CollapsedSummaryCard(
            metricsState = metricsState,
            isLoading = isLoading,
            onCalendarViewClicked = onCalendarViewClicked,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpandedContent(
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
            CurrentDayIndicator()
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
private fun HeaderLine() {
    val linePadding = getResponsiveSizeHeight(20.dp)
    Kiwi_HorizontalLine(
        40.dp,
        2.dp,
        MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(start = linePadding, end = linePadding),
    )
}

@Composable
private fun Header() {
    Kiwi_Spacer()
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderLine()
        Kiwi_H3(
            KiwiTextArguments(
                "Daily Progress",
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary,
            ),
        )
        HeaderLine()
    }
    Kiwi_Spacer()
}

@Composable
private fun CurrentDayIndicator() {
    Kiwi_Image(
        R.drawable.ph_dashboard_heart,
        "Current day indicator",
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarWeekView(
    context: Context,
    coroutineScope: CoroutineScope,
    usersViewModel: IUsersViewModel,
    metricsState: MetricsState,
    metricsViewModel: IMetricsViewModel,
    personalityViewModel: IPersonalityViewModel,
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    val date = stringToDate(metricsState.date)
    val currentDayOfWeek = date.dayOfWeek.value % DAYS_IN_WEEK
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayOfWeek) }
    val startOfWeek = date.minusDays(currentDayOfWeek.toLong())

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.medium)),
        ) {
            Row(
                modifier =
                    Modifier
                        .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.xSmall)),
            ) {
                for (index in 0 until DAYS_IN_WEEK) {
                    val day = startOfWeek.plusDays(index.toLong())
                    val isSelected = selectedDayIndex.intValue == index

                    Box(modifier = Modifier.weight(1f)) {
                        CalendarDayView(
                            usersViewModel = usersViewModel,
                            isLoading = isLoading,
                            day = day,
                            isSelected = isSelected,
                            onClicked = {
                                selectedDayIndex.intValue = index
                                selectDay(
                                    coroutineScope,
                                    metricsViewModel,
                                    metricsState,
                                    personalityViewModel,
                                    context,
                                    startOfWeek.plusDays(index.toLong()),
                                )
                            },
                            testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + index,
                        )
                    }
                }
            }

            ShowCalendarViewButton(
                isLoading = isLoading,
                onCalendarViewClicked = onCalendarViewClicked,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarMonthView(
    context: Context,
    isLoading: Boolean,
    coroutineScope: CoroutineScope,
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    metricsState: MetricsState,
    personalityViewModel: IPersonalityViewModel,
    modifier: Modifier = Modifier,
    shouldShowCalendarView: MutableState<Boolean>,
) {
    val selectedMonth = remember { mutableStateOf(YearMonth.from(stringToDate(metricsState.date))) }

    var transitionDirection by remember { mutableIntStateOf(0) } // -1 = previous, 1 = next
    var totalDragOffsetX by remember { mutableFloatStateOf(0f) }

    val gestureModifier =
        Modifier.pointerInput(selectedMonth) {
            detectDragGestures(
                onDragEnd = {
                    val dragThreshold = 100f
                    when {
                        totalDragOffsetX > dragThreshold -> {
                            transitionDirection = -1
                            selectYearMonth(selectedMonth, selectedMonth.value.minusMonths(1))
                        }
                        totalDragOffsetX < -dragThreshold -> {
                            transitionDirection = 1
                            selectYearMonth(selectedMonth, selectedMonth.value.plusMonths(1))
                        }
                    }
                    totalDragOffsetX = 0f
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    totalDragOffsetX += dragAmount.x
                },
            )
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .fillMaxWidth()
                .height(getResponsiveSizeHeight(300.dp))
                .then(gestureModifier)
                .testTag(DashboardModalTestTags.CALENDAR_VIEW),
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                text = dateToString(selectedMonth.value),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier =
                    Modifier
                        .testTag(DashboardModalTestTags.SELECTED_MONTH_TEXT),
            ),
        )
        Kiwi_Spacer(Spacing.large)

        AnimatedContent(
            targetState = selectedMonth.value,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(MONTH_SLIDE_ANIM_DURATION),
                    initialOffsetX = { fullWidth -> fullWidth * transitionDirection },
                ) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(MONTH_SLIDE_ANIM_DURATION),
                        targetOffsetX = { fullWidth -> -fullWidth * transitionDirection },
                    )
            },
            label = "CalendarMonthTransition",
        ) { displayedMonth ->
            val startOfMonth = displayedMonth.atDay(1)
            val endOfMonth = displayedMonth.atEndOfMonth()
            val startDayOfWeek = startOfMonth.dayOfWeek.value % DAYS_IN_WEEK
            val totalDays = startDayOfWeek + endOfMonth.dayOfMonth
            val totalWeeks = ceil(totalDays / DAYS_IN_WEEK.toFloat()).toInt()

            Column {
                for (weekIndex in 0 until totalWeeks) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(4.dp)),
                    ) {
                        for (dayOfWeek in 0 until DAYS_IN_WEEK) {
                            val dayIndex = weekIndex * DAYS_IN_WEEK + dayOfWeek
                            val dayOffset = dayIndex - startDayOfWeek
                            val dayDate = startOfMonth.plusDays(dayOffset.toLong())

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (dayDate in startOfMonth..endOfMonth) {
                                    CalendarDayView(
                                        usersViewModel = usersViewModel,
                                        isLoading = isLoading,
                                        day = dayDate,
                                        isSelected = stringToDate(metricsState.date) == dayDate,
                                        onClicked = {
                                            selectDay(
                                                coroutineScope,
                                                metricsViewModel,
                                                metricsState,
                                                personalityViewModel,
                                                context,
                                                dayDate,
                                            )
                                            shouldShowCalendarView.value = false
                                        },
                                        testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + dayDate.dayOfMonth,
                                    )
                                } else {
                                    Kiwi_Spacer()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarDayView(
    usersViewModel: IUsersViewModel,
    isLoading: Boolean,
    canSelectBeforeRegisterDate: Boolean = true,
    day: LocalDate,
    isSelected: Boolean,
    onClicked: () -> Unit,
    testTag: String,
) {
    val isDayEnabled =
        !day.isAfter(LocalDate.now()) &&
            (canSelectBeforeRegisterDate || !day.isBefore(usersViewModel.getRegisterDate()))

    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(getResponsiveSizeHeight(12.dp)))
                .border(
                    width = if (isSelected) getResponsiveSizeHeight(2.dp) else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else Color.Transparent,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(12.dp)),
                ).padding(vertical = getResponsiveSizeHeight(Spacing.xSmall))
                .clickable(
                    enabled = !isLoading && isDayEnabled,
                    onClick = onClicked,
                ).testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val contentAlpha = if (isDayEnabled) 1f else DAY_DISABLED_ALPHA
            Kiwi_P2(
                KiwiTextArguments(
                    day.dayOfMonth.toString(),
                    color = MaterialTheme.colorScheme.inversePrimary,
                    modifier = Modifier.alpha(contentAlpha),
                ),
            )

            Kiwi_Image(
                R.drawable.ph_dashboard_day_empty,
                "Dashboard day indicator",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(50.dp))
                        .alpha(contentAlpha),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun selectDay(
    coroutineScope: CoroutineScope,
    metricsViewModel: IMetricsViewModel,
    metricsState: MetricsState,
    personalityViewModel: IPersonalityViewModel,
    context: Context,
    newDay: LocalDate,
) {
    firebaseLogEvent(
        FirebaseEventNames.DASHBOARD_SEE_DAY,
        mapOf(
            "day_old" to metricsState.date,
            "day_new" to dateToString(newDay),
        ),
    )

    coroutineScope.launch {
        loadMetrics(dateToString(newDay), metricsViewModel, personalityViewModel, context)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun selectYearMonth(
    selectedMonth: MutableState<YearMonth>,
    newMonth: YearMonth,
) {
    firebaseLogEvent(
        FirebaseEventNames.DASHBOARD_SEE_MONTH,
        mapOf(
            "month_old" to dateToString(selectedMonth.value),
            "month_new" to dateToString(newMonth),
        ),
    )

    selectedMonth.value = newMonth
}

@RequiresApi(Build.VERSION_CODES.O)
private suspend fun loadMetrics(
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
private fun ExpandedProgressBox(state: MetricsState) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(getResponsiveSizeHeight(40.dp)))
                .background(MaterialTheme.colorScheme.surface)
                .padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Column {
            ExpandedMetricsProgress(state)

            Kiwi_Spacer()

            ExpandedSummaryCard()
        }
    }
}

@Composable
private fun ExpandedMetricProgressTitle(title: String) {
    Kiwi_H3(
        KiwiTextArguments(
            title,
            TextAlign.Center,
            MaterialTheme.colorScheme.secondary,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ),
    )
}

@Composable
private fun SelectedMetricsTime(
    maxSeconds: Int,
    currentSeconds: Int,
    validMetrics: Boolean,
    expanded: Boolean,
    tag: String,
) {
    val textArguments =
        KiwiAnnotatedStringArguments(
            buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                    if (validMetrics) {
                        append(DateUtils.parseTimeSeconds(currentSeconds))
                    }
                }
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))) {
                    if (validMetrics) {
                        append(" / " + DateUtils.parseTimeSeconds(maxSeconds))
                    } else {
                        append("No data")
                    }
                }
            },
            if (expanded) TextAlign.Center else TextAlign.Left,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(tag),
        )

    if (expanded) {
        Kiwi_AnnotatedString_P1(textArguments)
    } else {
        Kiwi_AnnotatedString_P2(textArguments)
    }
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
private fun CollapsedSummaryCard(
    metricsState: MetricsState,
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .padding(horizontal = getResponsiveSizeHeight(Spacing.xLarge))
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(getResponsiveSizeHeight(40.dp)))
                .padding(getResponsiveSizeHeight(Spacing.medium)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.width(getResponsiveSizeHeight(60.dp)),
            ) {
                CurrentDayIndicator()
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = getResponsiveSizeHeight(Spacing.small)),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    SelectedMetricsTime(
                        metricsState.maxGoodTimeSeconds,
                        metricsState.currentGoodTimeSeconds,
                        metricsState.currentGoodTimeSeconds > 0 || metricsState.currentBadTimeSeconds > 0,
                        false,
                        DashboardModalTestTags.GOOD_TIME,
                    )

                    Kiwi_Spacer(Spacing.xSmall)

                    SelectedMetricsTime(
                        metricsState.maxBadTimeSeconds,
                        metricsState.currentBadTimeSeconds,
                        metricsState.currentGoodTimeSeconds > 0 || metricsState.currentBadTimeSeconds > 0,
                        false,
                        DashboardModalTestTags.BAD_TIME,
                    )
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(getResponsiveSizeHeight(52.dp)),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ShowCalendarViewButton(
                isLoading,
                onCalendarViewClicked,
            )
        }
    }
}

@Composable
private fun ShowCalendarViewButton(
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    Kiwi_Image(
        R.drawable.calendar,
        "Show Calendar View Button",
        Modifier
            .size(getResponsiveSizeHeight(30.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                enabled = !isLoading,
            ) {
                onCalendarViewClicked()
            }.testTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON),
    )
}

@Composable
private fun ExpandedSummaryCard() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Kiwi_H3(
            KiwiTextArguments(
                "Challenges",
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
            ),
        )

        Kiwi_Spacer(Spacing.small)

        ExpandedQuestProgress(
            "Use Duolingo For 20 Minutes",
            R.drawable.ph_quest_01,
            0.5f,
        )

        Kiwi_Spacer()

        ExpandedQuestProgress(
            "Do 3 Sets Of 10 Push-Ups",
            R.drawable.ph_quest_02,
            0.8f,
        )
    }
}

@Composable
private fun ExpandedQuestProgress(
    title: String,
    imageRes: Int,
    progress: Float,
) {
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp)))
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.inversePrimary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = getResponsiveSizeHeight(4.dp),
                color = MaterialTheme.colorScheme.tertiary,
            )

            Kiwi_Image(
                imageRes,
                "Quest Indicator For: $title",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(20.dp)),
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_P2(
                KiwiTextArguments(
                    title,
                    TextAlign.Center,
                    MaterialTheme.colorScheme.secondary,
                    modifier =
                        Modifier
                            .padding(getResponsiveSizeHeight(Spacing.small)),
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
@Composable
fun DashboardModalHidden_Preview() {
    DashboardModalPreview(false, 0)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalCollapsed_Preview() {
    DashboardModalPreview(false, 1)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalExpanded_Preview() {
    DashboardModalPreview(false, 2)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalCalendar_Preview() {
    DashboardModalPreview(true, 2)
}

@SuppressLint("ViewModelConstructorInComposable")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DashboardModalPreview(
    showCalendarView: Boolean,
    initialStateIndex: Int = 0,
) {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapScreen()
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
                                ),
                            ),
                        showCalendarView,
                        initialStateIndex,
                    )
                }
            },
        )
    }
}
