package com.bellako.kiwi.features.dashboard.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.common.utils.DAYS_IN_WEEK
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.common.utils.DateUtils.stringToDate
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

@Composable
fun CurrentDayIndicator(size: Dp) {
    Kiwi_Image(
        R.drawable.ph_dashboard_heart,
        "Current day indicator",
        Modifier.size(size)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarWeekView(
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
                .padding(getResponsiveSizeHeight(Spacing.medium)),
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
                        .clip(RoundedCornerShape(getResponsiveSizeHeight(22.dp)))
                        .background(color = LocalKiwiColors.current.color2B)
                        .padding(getResponsiveSizeHeight(Spacing.xSmall))
                        .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
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
fun CalendarMonthView(
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
    val kiwiColors = LocalKiwiColors.current
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
                .height(getResponsiveSizeHeight(340.dp))
                .padding( horizontal = getResponsiveSizeHeight(Spacing.medium))
                .then(gestureModifier)
                .testTag(DashboardModalTestTags.CALENDAR_VIEW),
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                text = dateToString(selectedMonth.value),
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
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
        ) { month ->
            CalendarMonth(
                context = context,
                isLoading = isLoading,
                coroutineScope = coroutineScope,
                usersViewModel = usersViewModel,
                metricsViewModel = metricsViewModel,
                metricsState = metricsState,
                shouldShowCalendarView = shouldShowCalendarView,
                month = month,
                personalityViewModel = personalityViewModel,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarMonth(
    context: Context,
    isLoading: Boolean,
    coroutineScope: CoroutineScope,
    usersViewModel: IUsersViewModel,
    metricsViewModel: IMetricsViewModel,
    metricsState: MetricsState,
    personalityViewModel: IPersonalityViewModel,
    month: YearMonth,
    shouldShowCalendarView: MutableState<Boolean>,
) {
    val startOfMonth = month.atDay(1)
    val endOfMonth = month.atEndOfMonth()
    val startDayOfWeek = startOfMonth.dayOfWeek.value % DAYS_IN_WEEK
    val totalDays = startDayOfWeek + endOfMonth.dayOfMonth
    val totalWeeks = ceil(totalDays / DAYS_IN_WEEK.toFloat()).toInt()

    Column (
        verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(4.dp))
    ){
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDayView(
    usersViewModel: IUsersViewModel,
    isLoading: Boolean,
    canSelectBeforeRegisterDate: Boolean = true,
    day: LocalDate,
    isSelected: Boolean,
    onClicked: () -> Unit,
    testTag: String,
) {
    val kiwiColors = LocalKiwiColors.current
    val isDayEnabled =
        !day.isAfter(LocalDate.now()) &&
            (canSelectBeforeRegisterDate || !day.isBefore(usersViewModel.getRegisterDate()))

    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(getResponsiveSizeHeight(22.dp)))
                .background(color = kiwiColors.color3)
                .border(
                    width = if (isSelected) getResponsiveSizeHeight(2.dp) else 0.dp,
                    color = if (isSelected) kiwiColors.color9 else Color.Transparent,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(22.dp)),
                ).padding(getResponsiveSizeHeight(Spacing.xSmall))
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
                    color = kiwiColors.color9,
                    modifier = Modifier.alpha(contentAlpha),
                ),
            )

            Kiwi_Image(
                R.drawable.ph_dashboard_day_empty,
                "Dashboard day indicator",
                modifier =
                    Modifier
                        .alpha(contentAlpha),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun selectDay(
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
fun selectYearMonth(
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

@Composable
fun ShowCalendarViewButton(
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(height = getResponsiveSizeHeight(60.dp), width = getResponsiveSizeHeight(35.dp))
                .clip(RoundedCornerShape(getResponsiveSizeHeight(14.dp)))
                .background(color = LocalKiwiColors.current.color3)
                .clickable(
                    enabled = !isLoading,
                ) {
                    onCalendarViewClicked()
                }.testTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON),
        contentAlignment = Alignment.Center
    ) {
        Kiwi_Image(
            R.drawable.calendar,
            "Show Calendar View Button",
            Modifier
                .size(getResponsiveSizeHeight(24.dp))

        )
    }
}
