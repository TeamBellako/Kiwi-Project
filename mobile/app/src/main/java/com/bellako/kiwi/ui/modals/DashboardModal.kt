package com.bellako.kiwi.ui.modals

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.features.metrics.IMetricsViewModel
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsProvider
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.features.metrics.MetricsUtils
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_AnchoredDraggable
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedStringArguments
import com.bellako.kiwi.ui.components.Kiwi_H3
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_P2
import com.bellako.kiwi.ui.components.Kiwi_P3
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.features.map.MapScreen
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.ui.components.Kiwi_HorizontalLine
import com.bellako.kiwi.ui.tags.DashboardModalTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.ui.theme.Spacing
import com.bellako.kiwi.ui.theme.getResponsiveRelativeSize
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

enum class DashboardModalState {
    EXPANDED,
    COLLAPSED,
    HIDDEN
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardModal(
    viewModel: IMetricsViewModel,
    initialState: DashboardModalState = DashboardModalState.COLLAPSED,
    showCalendarView: Boolean = false
) {
    val anchors = listOf(
        DashboardModalState.EXPANDED to getResponsiveRelativeSize(900).toFloat(),
        DashboardModalState.COLLAPSED to getResponsiveRelativeSize(270).toFloat(),
        DashboardModalState.HIDDEN to getResponsiveRelativeSize(160).toFloat()
    )

    val metricsState by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val loadResult = viewModel.loadMetrics(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        metricsState?.let {
            if (loadResult.isFailure) viewModel.createMetrics(it)

            MetricsProvider.getMetrics(context, LocalDate.now())?.let { currentMetrics ->
                viewModel.updateMetrics(MetricsMapper.toState(currentMetrics))
            }
        }
    }

    val shouldShowCalendarView = remember { mutableStateOf(showCalendarView) }
    val selectedDay = remember { mutableStateOf(LocalDate.now()) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Kiwi_AnchoredDraggable(
            initialState = initialState,
            anchors = anchors,
            onStateChange = { targetState ->
                if (targetState != DashboardModalState.EXPANDED) {
                    shouldShowCalendarView.value = false
                    selectedDay.value = LocalDate.now()
                }
            },
            modifier = Modifier
                .testTag(DashboardModalTestTags.DRAGGABLE_NODE)
        ) { modalState, requestStateChange ->
            when (modalState) {
                DashboardModalState.EXPANDED ->
                    ExpandedContent(
                        viewModel = viewModel,
                        state = metricsState,
                        selectedDay = selectedDay,
                        shouldShowCalendarView = shouldShowCalendarView
                    )

                DashboardModalState.COLLAPSED ->
                    CollapsedContent(
                        state = metricsState,
                        isHidden = false,
                        onCalendarViewClicked = {
                            shouldShowCalendarView.value = true
                            requestStateChange(DashboardModalState.EXPANDED)
                        }
                    )

                DashboardModalState.HIDDEN ->
                    CollapsedContent(
                        state = metricsState,
                        isHidden = true,
                        onCalendarViewClicked = {}
                    )
            }
        }
    }
}


@Composable
private fun CollapsedContent(
    state: MetricsState?,
    isHidden: Boolean,
    onCalendarViewClicked: () -> Unit
) {
    state?.let { currentState ->
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(getResponsiveRelativeSize(20.dp)))
                .background(MaterialTheme.colorScheme.background)
                .padding(getResponsiveRelativeSize(Spacing.medium))
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag(CommonTestTags.DASHBOARD_MODAL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()

            if (!isHidden) {
                CollapsedSummaryCard(
                    currentState,
                    onCalendarViewClicked
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpandedContent(
    viewModel: IMetricsViewModel,
    state: MetricsState?,
    selectedDay: MutableState<LocalDate>,
    shouldShowCalendarView: MutableState<Boolean>
) {
    state?.let {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(getResponsiveRelativeSize(20.dp)))
                .background(MaterialTheme.colorScheme.background)
                .padding(getResponsiveRelativeSize(Spacing.medium))
                .fillMaxWidth()
                .wrapContentHeight()
                .testTag(CommonTestTags.DASHBOARD_MODAL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()

            if (shouldShowCalendarView.value) {
                CalendarView(
                    viewModel = viewModel,
                    shouldShowCalendarView = shouldShowCalendarView,
                    selectedDay = selectedDay
                )
            } else {
                if (shouldShowCalendarView.value) {
                    CalendarView(
                        viewModel = viewModel,
                        shouldShowCalendarView = shouldShowCalendarView,
                        selectedDay = selectedDay
                    )
                } else {
                    WeekView(
                        viewModel = viewModel,
                        selectedDay = selectedDay
                    ) {
                        shouldShowCalendarView.value = true
                    }
                }

                if (!shouldShowCalendarView.value) {
                    ExpandedProgressBox(it)
                }
            }
        }
    }
}


@Composable
private fun Header() {

    Kiwi_HorizontalLine(
        getResponsiveRelativeSize(40.dp),
        getResponsiveRelativeSize(2.dp),
        MaterialTheme.colorScheme.secondary,
    )
    Kiwi_Spacer()

    Kiwi_H3(
        Kiwi_TextArguments(
            "Daily Progress",
            TextAlign.Center,
            MaterialTheme.colorScheme.secondary
        )
    )
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
private fun WeekView(
    viewModel: IMetricsViewModel,
    selectedDay: MutableState<LocalDate>,
    onCalendarViewClicked: () -> Unit
) {
    val currentDayOfWeek = selectedDay.value.dayOfWeek.value % 7
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayOfWeek) }
    val coroutineScope = rememberCoroutineScope()
    val startOfWeek = selectedDay.value.minusDays(currentDayOfWeek.toLong())

    CurrentDayIndicator()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = getResponsiveRelativeSize(Spacing.medium))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(getResponsiveRelativeSize(Spacing.medium))
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(getResponsiveRelativeSize(Spacing.xSmall))
            ) {
                (0..6).forEach { index ->
                    val day = startOfWeek.plusDays(index.toLong())
                    val dayNumber = day.dayOfMonth
                    val isSelected = selectedDayIndex.intValue == index

                    Box(modifier = Modifier.weight(1f)) {
                        ExpandedDayIndicator(
                            dayName = dayNumber.toString(),
                            isSelected = isSelected,
                            onClicked = {
                                selectedDayIndex.intValue = index
                                selectedDay.value = startOfWeek.plusDays(index.toLong())

                                coroutineScope.launch {
                                    viewModel.loadMetrics(
                                        day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    )
                                }
                            },
                            isInFuture = day.isAfter(LocalDate.now()),
                            testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + index
                        )
                    }
                }
            }

            ShowCalendarViewButton(onCalendarViewClicked)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarView(
    viewModel: IMetricsViewModel,
    modifier: Modifier = Modifier,
    totalHeight: Dp = getResponsiveRelativeSize(300.dp),
    selectedDay: MutableState<LocalDate>,
    shouldShowCalendarView: MutableState<Boolean>
) {
    var currentYearMonth by rememberSaveable(stateSaver = Saver(
        save = { it.toString() },
        restore = { YearMonth.parse(it) }
    )) { mutableStateOf(YearMonth.from(selectedDay.value)) }

    var transitionDirection by remember { mutableStateOf(0) } // -1 = previous, 1 = next
    var totalDragOffsetX by remember { mutableStateOf(0f) }

    val gestureModifier = Modifier.pointerInput(currentYearMonth) {
        detectDragGestures(
            onDragEnd = {
                val dragThreshold = 100f
                when {
                    totalDragOffsetX > dragThreshold -> {
                        transitionDirection = -1
                        currentYearMonth = currentYearMonth.minusMonths(1)
                    }
                    totalDragOffsetX < -dragThreshold -> {
                        transitionDirection = 1
                        currentYearMonth = currentYearMonth.plusMonths(1)
                    }
                }
                totalDragOffsetX = 0f
            },
            onDrag = { change, dragAmount ->
                change.consume()
                totalDragOffsetX += dragAmount.x
            }
        )
    }

    val selectedDay = rememberSaveable { mutableStateOf(LocalDate.now()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .height(getResponsiveRelativeSize(totalHeight))
            .then(gestureModifier)
            .testTag(DashboardModalTestTags.CALENDAR_VIEW)
    ) {
        Kiwi_P2(
            Kiwi_TextArguments(
                currentYearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy")),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .testTag(DashboardModalTestTags.SELECTED_MONTH_TEXT)
            )
        )
        Kiwi_Spacer()

        AnimatedContent(
            targetState = currentYearMonth,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { fullWidth -> fullWidth * transitionDirection }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { fullWidth -> -fullWidth * transitionDirection }
                )
            },
            label = "CalendarMonthTransition"
        ) { displayedMonth ->
            val startOfMonth = displayedMonth.atDay(1)
            val endOfMonth = displayedMonth.atEndOfMonth()
            val startDayOfWeek = startOfMonth.dayOfWeek.value % 7
            val totalDays = startDayOfWeek + endOfMonth.dayOfMonth
            val totalWeeks = ceil(totalDays / 7f).toInt()

            Column {
                (0 until totalWeeks).forEach { weekIndex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(getResponsiveRelativeSize(4.dp))
                    ) {
                        (0..6).forEach { dayOfWeek ->
                            val dayIndex = weekIndex * 7 + dayOfWeek
                            val dayOffset = dayIndex - startDayOfWeek
                            val dayDate = startOfMonth.plusDays(dayOffset.toLong())

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayDate in startOfMonth..endOfMonth) {
                                    val isSelected = selectedDay.value == dayDate

                                    ExpandedDayIndicator(
                                        dayName = dayDate.dayOfMonth.toString(),
                                        isSelected = isSelected,
                                        onClicked = {
                                            if (selectedDay.value == dayDate) {
                                                shouldShowCalendarView.value = false
                                            }

                                            selectedDay.value = dayDate
                                            coroutineScope.launch {
                                                viewModel.loadMetrics(
                                                    dayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                )
                                            }
                                        },
                                        isInFuture = dayDate.isAfter(LocalDate.now()),
                                        testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + dayDate.dayOfMonth
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

@Composable
private fun ExpandedDayIndicator(
    dayName: String,
    isSelected: Boolean,
    onClicked: () -> Unit,
    isInFuture: Boolean,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(getResponsiveRelativeSize(12.dp)))
            .border(
                width = if (isSelected) getResponsiveRelativeSize(2.dp) else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else Color.Transparent,
                shape = RoundedCornerShape(getResponsiveRelativeSize(12.dp))
            )
            .padding(vertical = getResponsiveRelativeSize(Spacing.xSmall))
            .clickable { onClicked() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val contentAlpha = if (isInFuture) 0.4f else 1f
            Kiwi_P2(
                Kiwi_TextArguments(
                    dayName,
                    color = MaterialTheme.colorScheme.inversePrimary,
                    modifier = Modifier
                        .alpha(contentAlpha)
                )
            )

            Kiwi_Image(
                R.drawable.ph_dashboard_day_empty,
                "Dashboard day indicator",
                modifier = Modifier
                    .size(getResponsiveRelativeSize(50.dp))
                    .alpha(contentAlpha)
            )

        }
    }
}

@Composable
private fun ExpandedProgressBox(state: MetricsState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(getResponsiveRelativeSize(40.dp)))
            .background(MaterialTheme.colorScheme.surface)
            .padding(getResponsiveRelativeSize(Spacing.medium))
    ) {
        Column {
            ExpandedMetricsProgress(state)

            Kiwi_Spacer()

            ExpandedSummaryCard()
        }
    }
}

@Composable
private fun ExpandedMetricsProgress(state: MetricsState) {
    val maxSteps = 100000
    val currentSteps =
        if (state.steps < maxSteps)
            state.steps.toString()
        else "+99,999"

    val maxScreenTimeSeconds = 10 * 60 * 60
    val currentScreenTimeSeconds =
        if (state.screenTimeSeconds < maxScreenTimeSeconds)
            MetricsUtils.parseScreenTimeSeconds(state.screenTimeSeconds)
        else "+10 hours\n(are you serious?)"

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        MetricProgress(
            "Steps",
            currentSteps,
            "8,000",
            Modifier.weight(1f),
            DashboardModalTestTags.STEPS
        )
        MetricProgress(
            "Screen Time",
            currentScreenTimeSeconds,
            "3 hours",
            Modifier.weight(1f),
            DashboardModalTestTags.SCREEN_TIME
        )
    }
}

@Composable
private fun MetricProgress(
    title: String,
    value: String,
    target: String,
    modifier: Modifier,
    testTag: String
) {
    Box(modifier) {
        Column {
            Kiwi_H3(Kiwi_TextArguments(
                title,
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth())
            )
            Kiwi_P2(Kiwi_TextArguments(
                value,
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag))
            )
            Kiwi_P3(Kiwi_TextArguments(
                "/$target",
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth())
            )
        }
    }
}

@Composable
private fun CollapsedSummaryCard(
    state: MetricsState,
    onCalendarViewClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = getResponsiveRelativeSize(Spacing.xLarge))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(getResponsiveRelativeSize(40.dp)))
            .padding(getResponsiveRelativeSize(Spacing.medium))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Box(
                Modifier
                    .weight(0.2F)
            ) {
                CurrentDayIndicator()
            }
            Box(
                Modifier
                    .weight(0.6F)
                    .padding(horizontal = getResponsiveRelativeSize(Spacing.small))
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    val maxSteps = 100000
                    val currentSteps =
                        if (state.steps < maxSteps)
                            state.steps.toString()
                        else "+99,999"

                    val maxScreenTimeSeconds = 60
                    val currentScreenTimeSeconds =
                        if (state.screenTimeSeconds < maxScreenTimeSeconds)
                            (state.screenTimeSeconds / 60).toString()
                        else "+60"

                    val stepsText = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                            append(currentSteps)
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))) {
                            append("/8,000 steps")
                        }
                    }
                    Kiwi_AnnotatedString_P2(Kiwi_AnnotatedStringArguments(
                        stepsText,
                        TextAlign.Left,
                        Modifier.testTag(DashboardModalTestTags.STEPS)
                    ))

                    Kiwi_Spacer(Spacing.xSmall)

                    val screenTimeText = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                            append(currentScreenTimeSeconds)
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))) {
                            append("/60 screen mins")
                        }
                    }
                    Kiwi_AnnotatedString_P2(Kiwi_AnnotatedStringArguments(
                        screenTimeText,
                        TextAlign.Left,
                        Modifier.testTag(DashboardModalTestTags.SCREEN_TIME)
                    ))
                }
            }
            Box(
                Modifier
                    .weight(0.1F)
            ) {
                ShowCalendarViewButton(onCalendarViewClicked)
            }
        }
    }
}

@Composable
private fun ShowCalendarViewButton(
    onCalendarViewClicked: () -> Unit
) {
    Kiwi_Image(
        R.drawable.calendar,
        "Show Calendar View Button",
        Modifier
            .size(getResponsiveRelativeSize(30.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onCalendarViewClicked()
            }
            .testTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON)
    )
}

@Composable
private fun ExpandedSummaryCard() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Kiwi_H3(Kiwi_TextArguments(
            "Challenges",
            TextAlign.Center,
            MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight())
        )

        Kiwi_Spacer(Spacing.small)

        ExpandedQuestProgress(
            "Use Duolingo For 20 Minutes",
            R.drawable.ph_quest_01,
            0.5f
        )

        Kiwi_Spacer()

        ExpandedQuestProgress(
            "Do 3 Sets Of 10 Push-Ups",
            R.drawable.ph_quest_02,
            0.8f
        )
    }
}

@Composable
private fun ExpandedQuestProgress(title: String, imageRes: Int, progress: Float) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(getResponsiveRelativeSize(20.dp)))
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.inversePrimary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = getResponsiveRelativeSize(4.dp),
                color = MaterialTheme.colorScheme.tertiary
            )

            Kiwi_Image(
                imageRes,
                "Quest Indicator For: $title",
                modifier = Modifier
                    .size(getResponsiveRelativeSize(20.dp))
            )
        }
        Box (
            modifier = Modifier
                .weight(0.8F),
            contentAlignment = Alignment.Center
        ) {
            Kiwi_P2(Kiwi_TextArguments(
                title,
                TextAlign.Center,
                MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .padding(getResponsiveRelativeSize(Spacing.small))
            ))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalCalendarPreview() {
    DashboardModalPreview(
        DashboardModalState.EXPANDED,
        true
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalExpandedPreview() {
    DashboardModalPreview(
        DashboardModalState.EXPANDED,
        false
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalCollapsedPreview() {
    DashboardModalPreview(
        DashboardModalState.COLLAPSED,
        false
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModalHiddenPreview() {
    DashboardModalPreview(
        DashboardModalState.HIDDEN,
        false
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DashboardModalPreview(
    dashboardModalState: DashboardModalState,
    showCalendarView: Boolean
) {
    KiwiTheme {
        Scaffold(
            bottomBar = {
                AppBarModal(navController = rememberNavController())
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    MapScreen()
                    DashboardModal(
                        MetricsFakeViewModel(
                            MetricsState(
                                "2025-06-12",
                                1173,
                                9900
                            )
                        ),
                        dashboardModalState,
                        showCalendarView
                    )
                }
            }
        )
    }
}
