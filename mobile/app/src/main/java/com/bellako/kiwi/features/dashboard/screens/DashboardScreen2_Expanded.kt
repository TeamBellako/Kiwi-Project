package com.bellako.kiwi.features.dashboard.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.features.metrics.model.IMetricsViewModel
import com.bellako.kiwi.features.personality.model.IPersonalityViewModel
import com.bellako.kiwi.features.users.model.IUsersViewModel
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.CoroutineScope

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

        @Suppress("MagicNumber")
        ExpandedQuestProgress(
            "Use Duolingo For 20 Minutes",
            R.drawable.ph_quest_01,
            0.5f,
        )

        Kiwi_Spacer()

        @Suppress("MagicNumber")
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
fun DashboardModal2_Expanded_Preview() {
    DashboardModal_Preview(false, 2)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModal2_Expanded_Calendar_Preview() {
    DashboardModal_Preview(true, 2)
}
