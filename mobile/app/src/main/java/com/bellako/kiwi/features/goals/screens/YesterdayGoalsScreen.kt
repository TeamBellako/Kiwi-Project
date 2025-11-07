package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.LoadingModal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.features.goals.tests.GoalsTestFactory.validYesterdayGoalsState
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongMethod")
fun YesterdayGoalsModal(viewModel: IGoalsViewModel) {
    val yesterdayGoalsState by viewModel.yesterdayGoalsState.collectAsState()
    val kiwiColors = LocalKiwiColors.current

    LaunchedEffect(Unit) {
        viewModel.loadYesterdayDailyChallenges()
    }

    if (yesterdayGoalsState.isVisible) {
        Dialog(onDismissRequest = { viewModel.closeYesterdayGoalsModal() }) {
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(getResponsiveSizeHeight(16.dp)),
                shape = RoundedCornerShape(getResponsiveSizeHeight(16.dp)),
                colors =
                    CardDefaults.cardColors(
                        containerColor = kiwiColors.color1,
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = getResponsiveSizeHeight(8.dp)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(getResponsiveSizeHeight(24.dp)),
                ) {
                    if (yesterdayGoalsState.isLoading) {
                        LoadingModal()
                    } else {
                        val currentGoal = yesterdayGoalsState.currentGoal
                        if (currentGoal != null) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(16.dp)),
                            ) {
                                Kiwi_Label1(
                                    KiwiTextArguments(
                                        text = "Yesterday's Goals",
                                        color = kiwiColors.colorF,
                                        bold = true,
                                    ),
                                )

                                Kiwi_Spacer()
                                Kiwi_Label2(
                                    KiwiTextArguments(
                                        text =
                                            "Goal ${yesterdayGoalsState.currentGoalIndex + 1} of ${yesterdayGoalsState.goals.size}",
                                        color = kiwiColors.colorF,
                                    ),
                                )

                                Kiwi_Spacer()
                                Kiwi_Label1(
                                    KiwiTextArguments(
                                        text = currentGoal.description,
                                        color = kiwiColors.colorF,
                                    ),
                                )
                                Kiwi_Spacer()
                                Kiwi_Label2(
                                    KiwiTextArguments(
                                        text = "Did you complete this goal?",
                                        color = kiwiColors.color8,
                                    ),
                                )

                                Kiwi_Spacer()
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(12.dp)),
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        Kiwi_Button(
                                            textArguments =
                                                KiwiTextArguments(
                                                    text = "No",
                                                    color = kiwiColors.colorF,
                                                    bold = true,
                                                ),
                                            color = kiwiColors.color3,
                                            onClick = {
                                                viewModel.markYesterdayGoalAsCompleted(currentGoal.id, false)
                                            },
                                        )
                                    }

                                    Box(modifier = Modifier.weight(1f)) {
                                        Kiwi_Button(
                                            textArguments =
                                                KiwiTextArguments(
                                                    text = "Yes",
                                                    color = kiwiColors.colorF,
                                                    bold = true,
                                                ),
                                            color = kiwiColors.color5,
                                            onClick = {
                                                viewModel.markYesterdayGoalAsCompleted(currentGoal.id, true)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// -------------------------------------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun YesterdayGoalsState_Preview() {
    val fakeViewModel =
        GoalsFakeViewModel(
            initialYesterdayState =
                validYesterdayGoalsState(
                    isVisible = true,
                    isLoading = false,
                ),
        )

    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            YesterdayGoalsModal(viewModel = fakeViewModel)
        }
    }
}
