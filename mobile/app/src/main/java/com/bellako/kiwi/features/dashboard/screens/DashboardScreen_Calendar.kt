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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Display1
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P1
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
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun CurrentDayIndicator(
    size: Dp,
    challengesProgress: Float,
    dailyStatsProgress: Float,
) {
    Box(
        modifier = Modifier.size(size),
    ) {
        Kiwi_Image(
            if (challengesProgress == 1f && dailyStatsProgress == 1f) {
                R.drawable.heart_bg_completed
            } else {
                R.drawable.heart_bg_empty
            },
            "Day indicator bg",
            modifier = Modifier.matchParentSize(),
        )

        Kiwi_Image(
            R.drawable.heart_exterior_empty,
            "Day indicator challenges bg",
            modifier = Modifier.matchParentSize(),
        )

        Kiwi_Image(
            R.drawable.heart_exterior_completed,
            "Day indicator challenges fill",
            modifier =
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        clip = true
                        shape =
                            object : Shape {
                                @Suppress("MagicNumber")
                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density,
                                ): Outline {
                                    val progress = challengesProgress.coerceIn(0f, 1f)
                                    val sweep = 180f * progress

                                    val radius = min(size.width, size.height) / 2f
                                    val center = Offset(size.width / 2f, size.height / 2f)

                                    val rect =
                                        Rect(
                                            center.x - radius,
                                            center.y - radius,
                                            center.x + radius,
                                            center.y + radius,
                                        )

                                    val startAngle = 90f + sweep

                                    val path =
                                        Path().apply {
                                            moveTo(center.x, center.y)
                                            arcTo(
                                                rect = rect,
                                                startAngleDegrees = startAngle,
                                                sweepAngleDegrees = -2 * sweep,
                                                forceMoveTo = false,
                                            )
                                            close()
                                        }

                                    return Outline.Generic(path)
                                }
                            }
                    },
        )

        Kiwi_Image(
            R.drawable.heart_interior_empty,
            "Day indicator stepscreen bg",
            modifier = Modifier.matchParentSize(),
        )

        Kiwi_Image(
            R.drawable.heart_interior_completed,
            "Day indicator challenges fill",
            modifier =
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        clip = true
                        shape =
                            object : Shape {
                                @Suppress("MagicNumber")
                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density,
                                ): Outline {
                                    val progress = dailyStatsProgress.coerceIn(0f, 1f)
                                    val topPadding = size.height * 0.28f
                                    val bottomPadding = size.height * 0.28f
                                    val usableHeight = size.height - topPadding - bottomPadding
                                    val h = usableHeight * progress
                                    return Outline.Rectangle(
                                        Rect(0f, size.height - bottomPadding - h, size.width, size.height - bottomPadding),
                                    )
                                }
                            }
                    },
        )
    }
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
    var selectedDayIndex = currentDayOfWeek
    val startOfWeek = date.minusDays(currentDayOfWeek.toLong())

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    top = 0.dp,
                    start = getResponsiveSizeHeight(Spacing.large),
                    end = getResponsiveSizeHeight(Spacing.large),
                    bottom = getResponsiveSizeHeight(Spacing.medium),
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(getResponsiveSizeHeight(22.dp)))
                    .background(color = LocalKiwiColors.current.color2B)
                    .padding(getResponsiveSizeHeight(Spacing.small), getResponsiveSizeHeight(Spacing.small)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            for (index in 0 until DAYS_IN_WEEK) {
                val day = startOfWeek.plusDays(index.toLong())
                val isSelected = selectedDayIndex == index

                Box(modifier = Modifier.weight(1f)) {
                    CalendarDayView(
                        usersViewModel = usersViewModel,
                        isLoading = isLoading,
                        day = day,
                        isSelected = isSelected,
                        onClicked = {
                            AudioManager.playSFX(context, R.raw.snd_ui_tap)
                            selectedDayIndex = index
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
            ShowCalendarButton(
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

    Kiwi_Spacer(Spacing.medium)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .wrapContentHeight()
                .padding(horizontal = getResponsiveSizeHeight(Spacing.large))
                .background(kiwiColors.color2B, shape = RoundedCornerShape(getResponsiveSizeHeight(22.dp)))
                .then(gestureModifier)
                .testTag(DashboardModalTestTags.CALENDAR_VIEW),
    ) {
        Box {
            Kiwi_Display1(
                KiwiTextArguments(
                    String.format(Locale.ROOT, "%02d", selectedMonth.value.monthValue),
                    color = kiwiColors.color4B,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(start = getResponsiveSizeHeight(Spacing.medium))
                            .testTag(DashboardModalTestTags.SELECTED_MONTH_TEXT),
                ),
            )
            Kiwi_P1(
                KiwiTextArguments(
                    selectedMonth.value.month.toString(),
                    color = kiwiColors.color4B,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                ),
            )
        }

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

    Column(
        verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(8.dp)),
        modifier =
            Modifier.padding(
                start = getResponsiveSizeHeight(Spacing.large),
                end = getResponsiveSizeHeight(Spacing.large),
                bottom = getResponsiveSizeHeight(Spacing.medium),
            ),
    ) {
        for (weekIndex in 0 until totalWeeks) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                                    AudioManager.playSFX(context, R.raw.snd_ui_tap)
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
    isCompletedChallenges: Boolean = false,
    isCompletedStepsScreen: Boolean = false,
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
                .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp)))
                .background(color = if (isSelected) kiwiColors.color9A else kiwiColors.color3A)
                .border(
                    width = if (isSelected) getResponsiveSizeHeight(2.dp) else 0.dp,
                    color = if (isSelected) kiwiColors.color9 else Color.Transparent,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(20.dp)),
                ).padding(getResponsiveSizeHeight(6.dp))
                .clickable(
                    enabled = !isLoading && isDayEnabled,
                    onClick = onClicked,
                ).testTag(testTag),
        contentAlignment = Alignment.Center,
    ) {
        val contentAlpha = if (isDayEnabled) 1f else KIWI_DISABLED_ALPHA

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(contentAlpha),
        ) {
            Kiwi_P2(
                KiwiTextArguments(
                    day.dayOfMonth.toString(),
                    color = if (isSelected) kiwiColors.color9 else kiwiColors.color4B,
                ),
            )
            Box(
                modifier =
                    Modifier
                        .padding(
                            top = getResponsiveSizeHeight(4.dp),
                            start = getResponsiveSizeHeight(1.dp),
                            end = getResponsiveSizeHeight(1.dp),
                        ).size(getResponsiveSizeHeight(16.dp)),
            ) {
                Kiwi_Image(
                    if (isCompletedChallenges) {
                        R.drawable.tiny_heart_exterior_filled
                    } else {
                        if (isSelected) {
                            R.drawable.tiny_heart_exterior_current_empty
                        } else {
                            R.drawable.tiny_heart_exterior_empty
                        }
                    },
                    "Dashboard day indicator",
                    modifier =
                        Modifier
                            .matchParentSize(),
                )

                Kiwi_Image(
                    if (isCompletedStepsScreen) {
                        R.drawable.tiny_heart_filled
                    } else {
                        if (isSelected) {
                            R.drawable.tiny_heart_current_empty
                        } else {
                            R.drawable.tiny_heart_empty
                        }
                    },
                    "Dashboard day indicator",
                    modifier =
                        Modifier
                            .matchParentSize(),
                )
            }
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
fun ShowCalendarButton(
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier =
            Modifier
                .size(getResponsiveSizeHeight(34.dp))
                .clip(RoundedCornerShape(getResponsiveSizeHeight(10.dp)))
                .background(color = LocalKiwiColors.current.color4)
                .clickable(
                    enabled = !isLoading,
                ) {
                    AudioManager.playSFX(context, R.raw.snd_ui_button)
                    onCalendarViewClicked()
                }.testTag(DashboardModalTestTags.CALENDAR_VIEW_BUTTON),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Image(
            R.drawable.ic_calendar,
            "Show Calendar View Button",
            Modifier
                .size(getResponsiveSizeHeight(20.dp)),
        )
    }
}

@Composable
fun SelectedDayText(metricsState: MetricsState) {
    val kiwiColors = LocalKiwiColors.current
    val selectedDay = metricsState.date

    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(getResponsiveSizeHeight(10.dp)))
                .background(color = LocalKiwiColors.current.color9A)
                .padding(horizontal = getResponsiveSizeHeight(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_P2(
            KiwiTextArguments(
                text = selectedDay,
                textAlign = TextAlign.Center,
                color = kiwiColors.color6,
                modifier =
                    Modifier
                        .testTag(DashboardModalTestTags.SELECTED_DAY_TEXT),
            ),
        )
    }
}
