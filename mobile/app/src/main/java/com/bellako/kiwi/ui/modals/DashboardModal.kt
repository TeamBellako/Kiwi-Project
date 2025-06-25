package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.features.metrics.IMetricsViewModel
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsMapper
import com.bellako.kiwi.features.metrics.MetricsReader
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.features.metrics.MetricsUtils
import com.bellako.kiwi.services.common.CommonTestTags
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

@Composable
fun DashboardModal(
    viewModel: IMetricsViewModel
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMetrics(
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        if (state?.isDefault() == true) {
            viewModel.createMetrics(state!!)
        } else {
            val updatedMetrics = MetricsReader.getCurrentMetrics()
            viewModel.updateMetrics(MetricsMapper.toState(updatedMetrics))
        }
    }

    state?.let { currentState ->
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
            Kiwi_HorizontalLine(
                40.dp,
                2.dp,
                Color.LightGray,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            Kiwi_Spacer()

            Kiwi_H2(Kiwi_TextArguments(
                "Daily Progress",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary
            ))

            DaysIndicators(viewModel)
            ProgressBox(currentState)
        }
    }
}

@Composable
private fun DaysIndicators(
    viewModel: IMetricsViewModel
) {
    val currentDate = LocalDate.now()
    val currentDayIndex = MetricsUtils.getDayOfWeekNumber(currentDate)
    val selectedDayIndex = rememberSaveable { mutableIntStateOf(currentDayIndex) }

    val coroutineScope = rememberCoroutineScope()

    Kiwi_Image(
        R.drawable.ph_dashboard_heart,
        "Current day indicator",
        Modifier.size(240.dp)
    )

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    ) {
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        days.forEachIndexed { index, day ->
            DayIndicator(
                day,
                selectedDayIndex.intValue == index,
                {
                    selectedDayIndex.intValue = index

                    val daysBetween = selectedDayIndex.intValue - currentDayIndex
                    val selectedDate = currentDate.plusDays(daysBetween.toLong())

                    coroutineScope.launch {
                        viewModel.loadMetrics(
                            selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                    }
                },
                DashboardModalTestTags.DAY_INDICATOR_PREFIX + index
            )
        }
    }
}

@Composable
private fun DayIndicator(
    dayName: String,
    isSelected: Boolean,
    onClicked: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_P1(Kiwi_TextArguments(
            dayName,
            color = MaterialTheme.colorScheme.inversePrimary
        ))

        val imageResource = if (isSelected) {
            R.drawable.ph_dashboard_day_filled
        } else {
            R.drawable.ph_dashboard_day_empty
        }
        Kiwi_Image(
            imageResource,
            "Dashboard day indicator",
            Modifier
                .size(40.dp)
                .clickable { onClicked() }
                .testTag(testTag)
        )
    }
}

@Composable
private fun ProgressBox(
    currentState: MetricsState,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp)
    ) {
        Column {
            MetricsProgress(currentState)
            QuestsProgress()
        }
    }
}

@Composable
private fun MetricsProgress(
    currentState: MetricsState
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        MetricProgress(
            "Steps",
            currentState.steps.toString(),
            "8,000",
            Modifier.weight(1.0F),
            DashboardModalTestTags.STEPS
        )
        MetricProgress(
            "Screen Time",
            MetricsUtils.parseScreenTimeSeconds(currentState.screenTimeSeconds),
            "3 hours",
            Modifier.weight(1.0F),
            DashboardModalTestTags.SCREEN_TIME
        )
    }
}



@Composable
private fun MetricProgress(
    metricName: String,
    currentValue: String,
    targetValue: String,
    rowModifier: Modifier = Modifier,
    testTag: String,
) {
    Box(rowModifier) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ){
            Kiwi_H3(Kiwi_TextArguments(
                metricName,
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth()
            ))
            Kiwi_P1(Kiwi_TextArguments(
                currentValue,
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            ))
            Kiwi_P2(Kiwi_TextArguments(
                "/$targetValue",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3F),
                modifier = Modifier.fillMaxWidth()
            ))
        }
    }

}

@Composable
private fun QuestsProgress() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H3(Kiwi_TextArguments(
            "Challenges",
            TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.fillMaxWidth()
        ))

        QuestProgress(
            "Use Duolingo For 20 Minutes",
            R.drawable.ph_quest_01,
            0.5F
        )
        Kiwi_Spacer()
        QuestProgress(
            "Do 3 Sets Of 10 Push-Ups",
            R.drawable.ph_quest_02,
            0.8F
        )
    }
}

@Composable
private fun QuestProgress(
    questTitle: String,
    questImageResourceId: Int,
    currentProgress: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = {currentProgress},
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .align(Alignment.Center),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.inversePrimary,
            )
            Kiwi_Image(
                questImageResourceId,
                "Quest Indicator For: $questTitle",
                Modifier.size(20.dp)
            )
        }
        Kiwi_P1(Kiwi_TextArguments(
            questTitle,
            TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.padding(8.dp)
        ))
    }
}

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