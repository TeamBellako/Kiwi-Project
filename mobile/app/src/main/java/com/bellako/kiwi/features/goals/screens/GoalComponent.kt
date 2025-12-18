package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
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
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight

@Suppress("MagicNumber")
@Composable
fun GoalComponent(goal: GoalDomain) {
    val kiwiColors = LocalKiwiColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Min).padding(
                horizontal =
                    getResponsiveSizeHeight(Spacing.medium),
            ),
    ) {
        Kiwi_Image(
            R.drawable.daily_challenges_bg,
            "Bar bg",
            modifier =
                Modifier.fillMaxSize(),
        )

        Kiwi_Image(
            if (goal.progress == 1f) R.drawable.daily_challenges_completed else R.drawable.daily_challenges_fill,
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
                                    val w = size.width * goal.progress.coerceIn(0f, 1f)
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
                    getIcon(goal.type),
                    "Quest Indicator For: ${goal.objective}",
                    colorFilter =
                        ColorFilter.tint(if (goal.status == GoalStatus.NOT_COMPLETED) kiwiColors.colorF1 else kiwiColors.color8C),
                    contentScale = ContentScale.FillWidth,
                )
            }

            Box(
                modifier = Modifier.weight(0.70f),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_Label1(
                    KiwiTextArguments(
                        goal.objective,
                        TextAlign.Center,
                        kiwiColors.color6,
                    ),
                )
            }

            Box(
                modifier = Modifier.weight(0.10f).padding(getResponsiveSizeHeight(Spacing.small)),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_Image(
                    if (goal.status ==
                        GoalStatus.NOT_COMPLETED
                    ) {
                        R.drawable.ic_daily_challenges_plus
                    } else {
                        R.drawable.ic_daily_challenges_tick
                    },
                    "Quest Indicator For: ${goal.objective}",
//                    modifier = Modifier.height(getResponsiveSizeHeight(Spacing.large)),
                )
            }
        }
    }
}

@Composable
private fun ExpandedGoalComponent(goal: GoalDomain) {
    val kiwiColors = LocalKiwiColors.current
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(getResponsiveSizeHeight(230.dp), getResponsiveSizeHeight(46.dp)),
    ) {
        Kiwi_Image(
            R.drawable.daily_challenges_bg,
            "Bar bg",
            modifier =
                Modifier
                    .fillMaxSize(),
        )

        Kiwi_Image(
            R.drawable.daily_challenges_fill,
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
                                    val w = size.width * goal.progress.coerceIn(0f, 1f)
                                    return Outline.Rectangle(Rect(0f, 0f, w, size.height))
                                }
                            }
                    },
        )

        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = getResponsiveSizeHeight(10.dp)),
        ) {
            Kiwi_Image(
                getIcon(goal.type),
                "Quest Indicator For: ${goal.objective}",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(22.dp)),
            )
        }
        Box(
            modifier = Modifier.align(Alignment.Center),
        ) {
            Kiwi_Label3(
                KiwiTextArguments(
                    goal.objective,
                    TextAlign.Center,
                    kiwiColors.color6,
                ),
            )
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = getResponsiveSizeHeight(16.dp)),
        ) {
            Kiwi_Image(
                R.drawable.ic_daily_challenges_plus,
                "Quest Indicator For: ${goal.objective}",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(14.dp)),
            )
        }
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
                    "Goal completado",
                    "Programa el modal lo mejor que sepas",
                    GoalType.PRODUCTIVITY,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.COMPLETED,
                    1000,
                    progress = 1f,
                ),
            )
            GoalComponent(
                GoalDomain(
                    "1",
                    "Goal en progreso",
                    "Programa el modal lo mejor que sepas",
                    GoalType.EXERCISE,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.NOT_COMPLETED,
                    1000,
                    progress = 0.6f,
                ),
            )
            GoalComponent(
                GoalDomain(
                    "1",
                    "Goal sin empezar",
                    "Programa el modal lo mejor que sepas",
                    GoalType.MEDITATION,
                    GoalCategory.DAILY_CHALLENGES,
                    GoalStatus.NOT_COMPLETED,
                    1000,
                    progress = 0.0f,
                ),
            )
        }
    }
}
