package com.bellako.kiwi.features.dashboard.screens

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.bellako.kiwi.common.tests.CommonTestTags
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.common.utils.DAYS_IN_WEEK
import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.common.utils.DateUtils.formatDate
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

const val MONTH_SLIDE_ANIM_DURATION = 300
const val DAY_DISABLED_ALPHA = 0.3f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    personalityViewModel: IPersonalityViewModel,
    showCalendarView: Boolean = false,
    initialStateIndex: Int = 0,
) {
    val metricsState by metricsViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val dateNow = formatDate(LocalDate.now())
        val loadResult = metricsViewModel.loadMetrics(dateNow)
        metricsState?.let { state ->
            if (loadResult.isFailure) {
                metricsViewModel.createMetrics(state.copy(date = dateNow))
            }
            metricsViewModel.updateMetrics(
                MetricsProvider.getCurrentMetrics(
                    context,
                    LocalDate.now(),
                    metricsViewModel.state.value!!,
                    personalityViewModel.state.value!!,
                ),
            )
        }
    }

    val shouldShowCalendarView = remember { mutableStateOf(showCalendarView) }
    val selectedDay = remember { mutableStateOf(LocalDate.now()) }

    Kiwi_DraggableBar(
        modifier = Modifier.testTag(DashboardModalTestTags.DRAGGABLE_NODE),
        states = listOf(150, 260, 650),
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
                    CollapsedContent(
                        state = metricsState,
                        isHidden = true,
                        onCalendarViewClicked = {},
                    )
                } else if (currentStateIndex <= 1) {
                    CollapsedContent(
                        state = metricsState,
                        isHidden = false,
                        onCalendarViewClicked = {
                            shouldShowCalendarView.value = true
                        },
                    )
                } else if (currentStateIndex <= 2) {
                    ExpandedContent(
                        usersViewModel = usersViewModel,
                        metricsViewModel = metricsViewModel,
                        state = metricsState,
                        selectedDay = selectedDay,
                        shouldShowCalendarView = shouldShowCalendarView,
                    )
                }
            }
        },
        initialStateIndex = initialStateIndex,
    )
}

@Composable
private fun CollapsedContent(
    state: MetricsState?,
    isHidden: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    state?.let { currentState ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isHidden) {
                CollapsedSummaryCard(
                    currentState,
                    onCalendarViewClicked,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ExpandedContent(
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    state: MetricsState?,
    selectedDay: MutableState<LocalDate>,
    shouldShowCalendarView: MutableState<Boolean>,
) {
    state?.let {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (shouldShowCalendarView.value) {
                CalendarMonthView(
                    usersViewModel = usersViewModel,
                    metricsViewModel = metricsViewModel,
                    shouldShowCalendarView = shouldShowCalendarView,
                    selectedDay = selectedDay,
                )
            } else {
                CurrentDayIndicator()
                CalendarWeekView(
                    usersViewModel = usersViewModel,
                    metricsViewModel = metricsViewModel,
                    selectedDay = selectedDay,
                ) {
                    shouldShowCalendarView.value = true
                }
                ExpandedProgressBox(it)
            }
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
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    selectedDay: MutableState<LocalDate>,
    onCalendarViewClicked: () -> Unit,
) {
    val currentDayOfWeek = selectedDay.value.dayOfWeek.value % DAYS_IN_WEEK
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayOfWeek) }
    val coroutineScope = rememberCoroutineScope()
    val startOfWeek = selectedDay.value.minusDays(currentDayOfWeek.toLong())

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
                            day = day,
                            isSelected = isSelected,
                            onClicked = {
                                selectedDayIndex.intValue = index
                                selectDay(coroutineScope, metricsViewModel, selectedDay, startOfWeek.plusDays(index.toLong()))
                            },
                            testTag = DashboardModalTestTags.DAY_INDICATOR_PREFIX + index,
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
private fun CalendarMonthView(
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    modifier: Modifier = Modifier,
    selectedDay: MutableState<LocalDate>,
    shouldShowCalendarView: MutableState<Boolean>,
) {
    var currentYearMonth by rememberSaveable(
        stateSaver =
            Saver(
                save = { it.toString() },
                restore = { YearMonth.parse(it) },
            ),
    ) { mutableStateOf(YearMonth.from(selectedDay.value)) }

    var transitionDirection by remember { mutableIntStateOf(0) } // -1 = previous, 1 = next
    var totalDragOffsetX by remember { mutableFloatStateOf(0f) }

    val gestureModifier =
        Modifier.pointerInput(currentYearMonth) {
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
                },
            )
        }

    val coroutineScope = rememberCoroutineScope()

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
                currentYearMonth.format(DateTimeFormatter.ofPattern("MM-yyyy")),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier =
                    Modifier
                        .testTag(DashboardModalTestTags.SELECTED_MONTH_TEXT),
            ),
        )
        Kiwi_Spacer(Spacing.large)

        AnimatedContent(
            targetState = currentYearMonth,
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
                                        day = dayDate,
                                        isSelected = selectedDay.value == dayDate,
                                        onClicked = {
                                            selectDay(coroutineScope, metricsViewModel, selectedDay, dayDate)
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
    day: LocalDate,
    isSelected: Boolean,
    onClicked: () -> Unit,
    testTag: String,
) {
    val isDayEnabled = !day.isBefore(usersViewModel.getRegisterDate()) && !day.isAfter(LocalDate.now())

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
                    enabled = isDayEnabled,
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
    selectedDay: MutableState<LocalDate>,
    day: LocalDate,
) {
    selectedDay.value = day

    coroutineScope.launch {
        metricsViewModel.loadMetrics(formatDate(day))
    }

    firebaseLogEvent(
        FirebaseEventNames.DASHBOARD_SEE_DAY,
        mapOf(
            "day" to formatDate(day),
        ),
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
private fun TimeExpanded(
    maxSeconds: Int,
    currentSeconds: Int,
    tag: String,
) {
    val text =
        buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                append(DateUtils.parseTimeSeconds(currentSeconds))
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))) {
                append(" / " + DateUtils.parseTimeSeconds(maxSeconds))
            }
        }
    Kiwi_AnnotatedString_P1(
        KiwiAnnotatedStringArguments(
            text,
            TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(tag),
        ),
    )
}

@Composable
private fun GoodTimeExpanded(state: MetricsState) {
    TimeExpanded(state.maxGoodTimeSeconds, state.currentGoodTimeSeconds, DashboardModalTestTags.GOOD_TIME)
}

@Composable
private fun BadTimeExpanded(state: MetricsState) {
    TimeExpanded(state.maxBadTimeSeconds, state.currentBadTimeSeconds, DashboardModalTestTags.BAD_TIME)
}

@Composable
private fun TimeCollapsed(
    maxSeconds: Int,
    currentSeconds: Int,
    tag: String,
) {
    val text =
        buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline)) {
                append(DateUtils.parseTimeSeconds(currentSeconds))
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))) {
                append(" / " + DateUtils.parseTimeSeconds(maxSeconds))
            }
        }
    Kiwi_AnnotatedString_P2(
        KiwiAnnotatedStringArguments(
            text,
            TextAlign.Left,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag(tag),
        ),
    )
}

@Composable
private fun GoodTimeCollapsed(state: MetricsState) {
    TimeCollapsed(state.maxGoodTimeSeconds, state.currentGoodTimeSeconds, DashboardModalTestTags.GOOD_TIME)
}

@Composable
private fun BadTimeCollapsed(state: MetricsState) {
    TimeCollapsed(state.maxBadTimeSeconds, state.currentBadTimeSeconds, DashboardModalTestTags.BAD_TIME)
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
                GoodTimeExpanded(state)
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column {
                ExpandedMetricProgressTitle("Evil Apps Time")
                BadTimeExpanded(state)
            }
        }
    }
}

@Composable
private fun CollapsedSummaryCard(
    state: MetricsState,
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
                    GoodTimeCollapsed(state)

                    Kiwi_Spacer(Spacing.xSmall)

                    BadTimeCollapsed(state)
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(getResponsiveSizeHeight(52.dp)),
            contentAlignment = Alignment.CenterEnd,
        ) {
            ShowCalendarViewButton(onCalendarViewClicked)
        }
    }
}

@Composable
private fun ShowCalendarViewButton(onCalendarViewClicked: () -> Unit) {
    Kiwi_Image(
        R.drawable.calendar,
        "Show Calendar View Button",
        Modifier
            .size(getResponsiveSizeHeight(30.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable {
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
