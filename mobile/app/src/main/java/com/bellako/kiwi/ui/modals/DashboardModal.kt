package com.bellako.kiwi.ui.modals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.abs

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
    initialState: DashboardModalState = DashboardModalState.COLLAPSED
) {
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }

    val expandedHeight = screenHeightPx
    val collapsedHeight = with(density) { 300.dp.toPx() }
    val hiddenHeight = with(density) { 150.dp.toPx() }

    val anchors = listOf(
        DashboardModalState.EXPANDED to expandedHeight,
        DashboardModalState.COLLAPSED to collapsedHeight,
        DashboardModalState.HIDDEN to hiddenHeight
    )

    val offsetY = remember { Animatable(anchors.first { it.first == initialState }.second) }
    var modalState by remember { mutableStateOf(initialState) }

    val metricsState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMetrics(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        metricsState?.let {
            if (it.isDefault()) viewModel.createMetrics(it)
            else viewModel.updateMetrics(MetricsMapper.toState(MetricsReader.getCurrentMetrics()))
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset {
                    IntOffset(x = 0, y = (screenHeightPx - offsetY.value).toInt())
                }
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            offsetY.snapTo(
                                (offsetY.value - delta).coerceIn(hiddenHeight, expandedHeight)
                            )
                        }
                    },
                    onDragStopped = {
                        coroutineScope.launch {
                            val (nearestState, nearestOffset) = anchors.minByOrNull { abs(it.second - offsetY.value) }!!
                            modalState = nearestState
                            offsetY.animateTo(
                                targetValue = nearestOffset,
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { offsetY.value.toDp() })
            ) {
                when (modalState) {
                    DashboardModalState.EXPANDED -> ExpandedContent(viewModel, metricsState)
                    DashboardModalState.COLLAPSED -> CollapsedContent(metricsState, isHidden = false)
                    DashboardModalState.HIDDEN -> CollapsedContent(metricsState, isHidden = true)
                }
            }
        }
    }
}

@Composable
private fun CollapsedContent(state: MetricsState?, isHidden: Boolean) {
    state?.let {
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
                CollapsedSummaryCard(it)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpandedContent(viewModel: IMetricsViewModel, state: MetricsState?) {
    state?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 128.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
                .testTag(CommonTestTags.DASHBOARD_MODAL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()
            ExpandedDaysIndicators(viewModel)
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
private fun ExpandedDaysIndicators(viewModel: IMetricsViewModel) {
    val currentDate = LocalDate.now()
    val currentDayIndex = MetricsUtils.getDayOfWeekNumber(currentDate)
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayIndex) }
    val coroutineScope = rememberCoroutineScope()

    CurrentDayIndicator(240.dp)

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        listOf("S", "M", "T", "W", "T", "F", "S").forEachIndexed { index, day ->
            ExpandedDayIndicator(
                dayName = day,
                isSelected = selectedDayIndex.intValue == index,
                onClicked = {
                    selectedDayIndex.intValue = index

                    val selectedDate = currentDate.plusDays((index - currentDayIndex).toLong())

                    coroutineScope.launch {
                        viewModel.loadMetrics(selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    }
                },
                testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + index
            )
        }
    }
}


@Composable
private fun ExpandedDayIndicator(dayName: String, isSelected: Boolean, onClicked: () -> Unit, testTag: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Kiwi_P1(Kiwi_TextArguments(
            dayName,
            color = MaterialTheme.colorScheme.inversePrimary)
        )

        Kiwi_Image(
            if (isSelected) R.drawable.ph_dashboard_day_filled else R.drawable.ph_dashboard_day_empty,
            "Dashboard day indicator",
            Modifier.size(40.dp).clickable { onClicked() }.testTag(testTag)
        )
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
private fun CollapsedSummaryCard(state: MetricsState) {
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
                    .background(MaterialTheme.colorScheme.background))
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