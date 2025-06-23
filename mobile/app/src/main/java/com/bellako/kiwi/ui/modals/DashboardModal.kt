package com.bellako.kiwi.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.features.metrics.IMetricsViewModel
import com.bellako.kiwi.features.metrics.MetricsFakeViewModel
import com.bellako.kiwi.features.metrics.MetricsState
import com.bellako.kiwi.ui.components.Kiwi_H2
import com.bellako.kiwi.ui.components.Kiwi_Image
import com.bellako.kiwi.ui.components.Kiwi_Spacer
import com.bellako.kiwi.ui.components.Kiwi_TextArguments
import com.bellako.kiwi.ui.theme.KiwiTheme
import com.bellako.kiwi.R
import com.bellako.kiwi.ui.components.Kiwi_H3
import com.bellako.kiwi.ui.components.Kiwi_P1
import com.bellako.kiwi.ui.components.Kiwi_P2

@Composable
fun DashboardModal(
    viewModel: IMetricsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Kiwi_H2(Kiwi_TextArguments(
            "Daily Progress",
            TextAlign.Center,
            MaterialTheme.colorScheme.inversePrimary
        ))

        DaysRow()
        Kiwi_Spacer()
        ProgressBox()
    }
}

@Composable
private fun DaysRow() {
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
    ) {
        val days = listOf("S", "M", "T", "W", "T", "F", "S")
        days.forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Kiwi_P1(Kiwi_TextArguments(
                    day,
                    color = MaterialTheme.colorScheme.inversePrimary
                ))

                Kiwi_Spacer(0.2F)

                Kiwi_Image(
                    R.drawable.ph_dashboard_day_filled,
                    "Dashboard day indicator"
                )
            }
        }
    }
}

@Composable
private fun ProgressBox() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp)
    ) {
        Column {
            MetricsProgress()
            Kiwi_Spacer()
            QuestsProgress()
        }
    }
}

@Composable
private fun MetricsProgress() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ){
            Kiwi_H3(Kiwi_TextArguments(
                "Steps",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth()
            ))
            Kiwi_P1(Kiwi_TextArguments(
                "1,173",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth()
            ))
            Kiwi_P2(Kiwi_TextArguments(
                "/8,000",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3F),
                modifier = Modifier.fillMaxWidth()
            ))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ){
            Kiwi_H3(Kiwi_TextArguments(
                "Screen Time",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth()
            ))
            Kiwi_P1(Kiwi_TextArguments(
                "2h 45min",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.fillMaxWidth()
            ))
            Kiwi_P2(Kiwi_TextArguments(
                "/3 hours",
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
                    .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 0.5F,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.inversePrimary,
                )
                Kiwi_Image(
                    R.drawable.ph_quest_01,
                    "Quest Indicator 1",
                    Modifier.size(20.dp)
                )
            }
            Kiwi_P1(Kiwi_TextArguments(
                "Use Duolingo For 20 Minutes",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.padding(8.dp)
            ))
        }
        Kiwi_Spacer()
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
                    .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 0.8F,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.inversePrimary,
                )
                Kiwi_Image(
                    R.drawable.ph_quest_02,
                    "Quest Indicator 1",
                    Modifier.size(20.dp)
                )
            }
            Kiwi_P1(Kiwi_TextArguments(
                "Do 3 Sets Of 10 Push-Ups",
                TextAlign.Center,
                MaterialTheme.colorScheme.inversePrimary,
                modifier = Modifier.padding(8.dp)
            ))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardModalPreview() {
    KiwiTheme {
        DashboardModal(
            MetricsFakeViewModel(
                MetricsState(
                    "finn@thehuman.com",
                    "2025-06-12",
                    1173,
                    9900
                )
            )
        )
    }
}