package com.bellako.kiwi.ui.modals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.bellako.kiwi.R
import com.bellako.kiwi.features.metrics.IMetricsViewModel
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsReader
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.features.metrics.MetricsUtils
import com.bellako.kiwi.services.common.CommonTestTags
import com.bellako.kiwi.ui.components.Kiwi_AnchoredDraggable
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedString
import com.bellako.kiwi.ui.components.Kiwi_AnnotatedStringArguments
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_H3
import com.bellako.kiwi.ui.components.Kiwi_HorizontalLine
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_P2
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.tags.DashboardModalTestTags
import com.bellako.kiwi.ui.theme.KiwiTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

enum class DashboardModalState {
    EXPANDED,
    COLLAPSED,
    HIDDEN
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun DashboardModalPreview() {
    KiwiTheme {
        DashboardModal(
            MetricsFakeViewModel(
                MetricsState(
                    "2025-06-12",
                    1173,
                    9900
                )
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalWearMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardModal(
    viewModel: IMetricsViewModel,
    initialState: DashboardModalState = DashboardModalState.EXPANDED
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val appBarOffsetPx = with(density) { 120.dp.toPx() }

    val expandedHeight = screenHeightPx
    val collapsedHeight = with(density) { 300.dp.toPx() + appBarOffsetPx }
    val hiddenHeight = with(density) { 120.dp.toPx() + appBarOffsetPx }

    val anchors = listOf(
        DashboardModalState.EXPANDED to expandedHeight,
        DashboardModalState.COLLAPSED to collapsedHeight,
        DashboardModalState.HIDDEN to hiddenHeight
    )

    val metricsState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMetrics(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        metricsState?.let {
            if (it.isDefault()) viewModel.createMetrics(it)
            else viewModel.updateMetrics(MetricsMapper.toState(MetricsReader.getCurrentMetrics()))
        }
    }

    val shouldShowCalendarView = remember { mutableStateOf(false) }

    Kiwi_AnchoredDraggable(
        initialState = initialState,
        anchors = anchors,
        onStateChange = { targetState ->
            if (targetState != DashboardModalState.EXPANDED) {
                shouldShowCalendarView.value = false
            }
        },
        modifier = Modifier.testTag(DashboardModalTestTags.DRAGGABLE_NODE)
    ) { modalState, requestStateChange ->
        when (modalState) {
            DashboardModalState.EXPANDED -> ExpandedContent(
                viewModel,
                metricsState,
                shouldShowCalendarView
            )

            DashboardModalState.COLLAPSED -> CollapsedContent(
                metricsState,
                false,
            ) {
                shouldShowCalendarView.value = true
                requestStateChange(DashboardModalState.EXPANDED)
            }

            DashboardModalState.HIDDEN -> CollapsedContent(
                 metricsState,
                true
            ) {}
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
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 16.dp),
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
    shouldShowCalendarView: MutableState<Boolean>
) {
    state?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 86.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
                .testTag(CommonTestTags.DASHBOARD_MODAL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()

            if (shouldShowCalendarView.value) {
                CalendarView(viewModel)
            } else {
                WeekView(viewModel) {
                    shouldShowCalendarView.value = true
                }
            }

            ExpandedProgressBox(it)
        }
    }
}

@Composable
private fun Header() {
    Kiwi_HorizontalLine(
        40.dp,
        2.dp,
        Color.LightGray,
        Modifier.padding(top = 8.dp)
    )

    Kiwi_Spacer()

    Kiwi_H2(Kiwi_TextArguments(
        "Daily Progress",
        TextAlign.Center,
        MaterialTheme.colorScheme.inversePrimary)
    )
}

@Composable
private fun CurrentDayIndicator(size: Dp) {
    Kiwi_Image(
        R.drawable.ph_dashboard_heart,
        "Current day indicator",
        Modifier.size(size)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekView(
    viewModel: IMetricsViewModel,
    onCalendarViewClicked: () -> Unit
) {
    val currentDate = LocalDate.now()
    val currentDayOfWeek = currentDate.dayOfWeek.value % 7
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayOfWeek) }
    val coroutineScope = rememberCoroutineScope()
    val startOfWeek = currentDate.minusDays(currentDayOfWeek.toLong())

    CurrentDayIndicator(240.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                coroutineScope.launch {
                                    viewModel.loadMetrics(
                                        day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    )
                                }
                            },
                            testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + index
                        )
                    }
                }
            }

            Kiwi_Image(
                R.drawable.calendar,
                "Calendar View Button",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onCalendarViewClicked()
                    }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    viewModel: IMetricsViewModel,
    modifier: Modifier = Modifier,
    totalHeight: Dp = 300.dp
) {
    val today = LocalDate.now()
    val selectedDay = rememberSaveable { mutableStateOf(today) }
    val coroutineScope = rememberCoroutineScope()

    val startOfMonth = today.withDayOfMonth(1)
    val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

    val startDayOfWeek = startOfMonth.dayOfWeek.value % 7 // 0 = Sunday
    val totalDays = startDayOfWeek + endOfMonth.dayOfMonth
    val totalWeeks = ceil(totalDays / 7f).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        (0 until totalWeeks).forEach { weekIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                (0..6).forEach { dayOfWeek ->
                    val dayIndex = weekIndex * 7 + dayOfWeek
                    val dayOffset = dayIndex - startDayOfWeek
                    val dayDate = startOfMonth.plusDays(dayOffset.toLong())

                    Box(
                        modifier = Modifier
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            dayDate in startOfMonth..endOfMonth -> {
                                val isSelected = selectedDay.value == dayDate

                                ExpandedDayIndicator(
                                    dayName = dayDate.dayOfMonth.toString(),
                                    isSelected = isSelected,
                                    onClicked = {
                                        selectedDay.value = dayDate
                                        coroutineScope.launch {
                                            viewModel.loadMetrics(
                                                dayDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            )
                                        }
                                    },
                                    testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + dayDate.dayOfMonth
                                )
                            }

                            else -> {
                                Kiwi_Spacer()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedDayIndicator(
    dayName: String,
    isSelected: Boolean,
    onClicked: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f) // square cells help balance icon + text
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClicked() }
            .testTag(testTag)
            .padding(4.dp), // internal padding to prevent clipping
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Kiwi_P1(
                Kiwi_TextArguments(
                    dayName,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            )

            Kiwi_Image(
                R.drawable.ph_dashboard_day_empty,
                "Dashboard day indicator",
                modifier = Modifier
                    .size(32.dp) // reduced to fit smaller cells
            )
        }
    }
}



@Composable
private fun ExpandedProgressBox(state: MetricsState) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp)
    ) {
        Column {
            ExpandedMetricsProgress(state)
            ExpandedSummaryCard()
        }
    }
}

@Composable
private fun ExpandedMetricsProgress(state: MetricsState) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        MetricProgress(
            "Steps",
            state.steps.toString(),
            "8,000",
            Modifier.weight(1f),
            DashboardModalTestTags.STEPS
        )
        MetricProgress(
            "Screen Time",
            MetricsUtils.parseScreenTimeSeconds(state.screenTimeSeconds),
            "3 hours",
            Modifier.weight(1f),
            DashboardModalTestTags.SCREEN_TIME
        )
    }
}

@Composable
private fun MetricProgress(title: String, value: String, target: String, modifier: Modifier, testTag: String) {
    Box(modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Kiwi_H3(Kiwi_TextArguments(
                title,
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth())
            )
            Kiwi_P1(Kiwi_TextArguments(
                value,
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth().testTag(testTag))
            )
            Kiwi_P2(Kiwi_TextArguments(
                "/$target",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth())
            )
        }
    }
}

@Composable
private fun CollapsedSummaryCard(
    state: MetricsState,
    onCalendarViewClicked: () -> Unit?
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .wrapContentSize()
        ) {
            CurrentDayIndicator(120.dp)

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentSize()
            ) {
                val stepsText = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.inversePrimary)) {
                        append(state.steps.toString())
                    }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f))) {
                        append("/8,000 steps")
                    }
                }
                Kiwi_AnnotatedString(Kiwi_AnnotatedStringArguments(
                    stepsText,
                    TextAlign.Left,
                    Modifier
                        .testTag(DashboardModalTestTags.STEPS)))

                val screenTimeText = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.inversePrimary)) {
                        append((state.screenTimeSeconds / 60).toString())
                    }
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f))) {
                        append("/60 screen mins")
                    }
                }
                Kiwi_AnnotatedString(Kiwi_AnnotatedStringArguments(
                    screenTimeText,
                    TextAlign.Left,
                    Modifier
                        .testTag(DashboardModalTestTags.SCREEN_TIME))
                )
            }

            Kiwi_Image(
                R.drawable.calendar,
                "Calendar View Button",
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .size(30.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                        onCalendarViewClicked()
                    }
            )
        }
    }
}

@Composable
private fun ExpandedSummaryCard() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Kiwi_H3(Kiwi_TextArguments(
            "Challenges",
            TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.fillMaxWidth())
        )

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
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.inversePrimary
            )

            Kiwi_Image(
                imageRes,
                "Quest Indicator For: $title",
                Modifier.size(20.dp)
            )
        }
        Kiwi_P1(Kiwi_TextArguments(
            title, TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.padding(8.dp))
        )
    }
}