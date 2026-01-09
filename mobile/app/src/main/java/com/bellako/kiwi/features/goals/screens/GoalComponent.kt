package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.launch

@Suppress("MagicNumber")
@Composable
fun GoalComponent(
    goal: IGoal,
    goalsViewModel: IGoalsViewModel,
) {
    var currentGoal by remember { mutableStateOf(goal) }
    var showModal by remember { mutableStateOf(false) }
    val goalDomain = currentGoal as? GoalDomain
    val progress = goalDomain?.progress ?: 0f
    val status = goalDomain?.status ?: GoalStatus.IN_PROGRESS
    val kiwiColors = LocalKiwiColors.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .padding(
                    horizontal =
                        getResponsiveSizeHeight(Spacing.medium),
                ).clickable { showModal = true },
    ) {
        Kiwi_Image(
            R.drawable.daily_challenges_bg,
            "Bar bg",
            modifier =
                Modifier.fillMaxSize(),
        )

        Kiwi_Image(
            if (progress == 1f) R.drawable.daily_challenges_completed else R.drawable.daily_challenges_fill,
            "Bar fill",
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        clip = true
                        shape =
                            object : Shape {
                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density,
                                ): Outline {
                                    val w = size.width * progress.coerceIn(0f, 1f)
                                    return Outline.Rectangle(Rect(0f, 0f, w, size.height))
                                }
                            }
                    },
            contentScale = ContentScale.FillWidth,
        )

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(getResponsiveSizeHeight(-3.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(0.20f).padding(getResponsiveSizeHeight(Spacing.medium)),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_Image(
                    getIcon(currentGoal.type),
                    "Quest Indicator For: $currentGoal.objective",
                    colorFilter =
                        ColorFilter.tint(if (status != GoalStatus.COMPLETED) kiwiColors.colorF1 else kiwiColors.color8C),
                    contentScale = ContentScale.FillWidth,
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(0.70f),
            ) {
                Kiwi_Label1(
                    KiwiTextArguments(
                        currentGoal.description,
                        TextAlign.Center,
                        kiwiColors.color6,
                    ),
                )
            }

            Box(
                modifier =
                    Modifier.weight(0.10f).padding(getResponsiveSizeHeight(Spacing.small)).clickable {
                        if (status == GoalStatus.COMPLETED) {
                            return@clickable
                        } else {
                            coroutineScope.launch {
                                val result = goalsViewModel.updateGoalProgress(currentGoal.id)
                                result.onSuccess { update -> currentGoal = update }
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_Image(
                    if (status !=
                        GoalStatus.COMPLETED
                    ) {
                        R.drawable.ic_daily_challenges_plus
                    } else {
                        R.drawable.ic_daily_challenges_tick
                    },
                    "Quest Indicator For: $currentGoal.objective",
//                    modifier = Modifier.height(getResponsiveSizeHeight(Spacing.large)),
                )
            }
        }
    }
    if (showModal) {
        GoalCustomiceModal(
            goal = currentGoal as GoalDomain,
            goalsViewModel = goalsViewModel,
            onDismiss = { showModal = false },
            onGoalUpdated = { updatedGoal ->
                currentGoal = updatedGoal
            },
        )
    }
}

fun getIcon(goalType: GoalType): Int =
    when (goalType) {
        GoalType.EXERCISE -> R.drawable.ic_daily_challenge_physical
        GoalType.PRODUCTIVITY -> R.drawable.ic_daily_challenge_mental
        GoalType.SLEEP -> R.drawable.ic_daily_challenge_mental
        GoalType.MEDITATION -> R.drawable.ic_daily_challenge_mental
        GoalType.NUTRITION -> R.drawable.ic_daily_challenge_mental
    }

// =================================================================================================
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalComponent_Preview() {
    Kiwi_Theme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            GoalComponent(
                GoalDomain(
                    "1",
                    1,
                    "Programa el modal lo mejor que sepas",
                    GoalType.PRODUCTIVITY,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.COMPLETED,
                    1000,
                    progress = 1f,
                ),
                GoalsFakeViewModel(),
            )
            GoalComponent(
                GoalDomain(
                    "1",
                    10,
                    "Programa el modal lo mejor que sepas",
                    GoalType.EXERCISE,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.NOT_COMPLETED,
                    1000,
                    progress = 0.6f,
                ),
                GoalsFakeViewModel(),
            )
            GoalComponent(
                GoalDomain(
                    "1",
                    20,
                    "Programa el modal lo mejor que sepas",
                    GoalType.MEDITATION,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.IN_PROGRESS,
                    1000,
                    progress = 0.0f,
                ),
                GoalsFakeViewModel(),
            )
        }
    }
}
