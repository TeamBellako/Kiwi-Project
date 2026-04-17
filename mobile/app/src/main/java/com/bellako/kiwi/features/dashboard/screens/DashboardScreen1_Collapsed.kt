package com.bellako.kiwi.features.dashboard.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen1_Collapsed(
    goalsViewModel: IGoalsViewModel,
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
            goalsViewModel = goalsViewModel,
            metricsState = metricsState,
            isLoading = isLoading,
            onCalendarViewClicked = onCalendarViewClicked,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CollapsedSummaryCard(
    goalsViewModel: IGoalsViewModel,
    metricsState: MetricsState,
    isLoading: Boolean,
    onCalendarViewClicked: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    var dailyGoalProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(metricsState) {
        dailyGoalProgress = goalsViewModel.getDailyGoalsProgress(metricsState.date)
    }

    Box(
        modifier =
            Modifier
                .padding(getResponsiveSizeHeight(Spacing.medium))
                .background(kiwiColors.color3, shape = RoundedCornerShape(40.dp)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .padding(start = getResponsiveSizeHeight(Spacing.medium)),
            ) {
                @Suppress("MagicNumber")
                CurrentDayIndicator(
                    getResponsiveSizeHeight(100.dp),
                    dailyGoalProgress,
                    metricsState.getAppUsageProgress(),
                )
            }
            Box(
                Modifier
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
                .align(alignment = Alignment.CenterEnd)
                .padding(horizontal = getResponsiveSizeHeight(Spacing.large)),
        ) {
            ShowCalendarButton(
                isLoading,
                onCalendarViewClicked,
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
fun DashboardModal1_Collapsed_Preview() {
    DashboardModal_Preview(false, DashboardLayout.COLLAPSED)
}
