package com.bellako.kiwi.features.dashboard.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.tests.DashboardModalTestTags
import com.bellako.kiwi.features.metrics.data.MetricsState
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Composable
fun DashboardScreen1_Collapsed(
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

// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun DashboardModal1_Collapsed_Preview() {
    DashboardModal_Preview(false, 1)
}
