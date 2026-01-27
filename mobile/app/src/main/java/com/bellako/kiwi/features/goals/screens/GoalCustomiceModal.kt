package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_HorizontalLine
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
@Suppress("LongMethod")
fun GoalCustomice(
    goal: GoalDomain,
    goalsViewModel: IGoalsViewModel,
    onDismiss: () -> Unit = {},
    onGoalUpdated: (GoalDomain) -> Unit = {},
) {
    val buttonsWidth = getResponsiveSizeWidth(150.dp)
    val coroutineScope = rememberCoroutineScope()
    val kiwiColor = LocalKiwiColors.current

    val initialProgress = goal.value.toFloat() / goal.target.toFloat()

    // Estado local para el progreso del slider (inicializado con el valor del goal)
    var sliderProgress: Float by remember { mutableStateOf(initialProgress.coerceIn(0f, 1f)) }

    // Si el goal cambia (p. ej. se abre otro goal), sincronizamos el slider
    LaunchedEffect(goal.id) {
        sliderProgress = (goal.value.toFloat() / goal.target.toFloat()).coerceIn(0f, 1f)
    }

    val current = (sliderProgress * goal.target).roundToInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box {
            Kiwi_Image(
                painter = painterResource(id = R.drawable.goal_customice_modal),
                alt = "Goal customize modal",
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier.fillMaxWidth().padding(
                        horizontal =
                            getResponsiveSizeWidth(Spacing.medium),
                    ),
            ) {
                Kiwi_Image(
                    painter = painterResource(id = getIcon(goal.type)),
                    alt = "Goal icon",
                    modifier =
                        Modifier
                            .padding(vertical = getResponsiveSizeHeight(20.dp))
                            .size(getResponsiveSizeHeight(40.dp)),
                )
                Kiwi_H2(
                    KiwiTextArguments(
                        goal.action,
                        TextAlign.Center,
                        modifier =
                            Modifier.padding(
                                top = getResponsiveSizeHeight(Spacing.medium),
                                bottom = getResponsiveSizeHeight(Spacing.small),
                            ),
                        color = kiwiColor.color6,
                    ),
                )
                // Usar el estado local del slider y actualizarlo en onValueChange
                Kiwi_Slider(
                    value = sliderProgress,
                    onValueChange = { newValue -> sliderProgress = newValue.coerceIn(0f, 1f) },
                    steps = 100,
                    testTag = "",
                    valueRange = 0f..1f,
                )
                Kiwi_Spacer(Spacing.small)

                Kiwi_P2(
                    KiwiTextArguments(
                        "${current}/${goal.target}",
                        color = kiwiColor.color7A,
                    ),
                )
                Kiwi_Spacer(Spacing.medium)
                Kiwi_HorizontalLine(color = kiwiColor.color2)
                Kiwi_Spacer(Spacing.small)
                Kiwi_H2(KiwiTextArguments("Dificulty"))
                Kiwi_Spacer(Spacing.small)
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                            .background(
                                color = kiwiColor.color2,
                                shape = RoundedCornerShape(getResponsiveSizeHeight(25.dp)),
                            ).padding(
                                horizontal = getResponsiveSizeWidth(12.dp),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Kiwi_Label1(KiwiTextArguments("Easy", modifier = Modifier.padding(vertical = Spacing.small)))
                    }
                }
                Kiwi_Spacer(Spacing.medium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                )
                {
                    Kiwi_FixedSizeButton(
                        textArguments =
                            KiwiTextArguments(
                                "Swap",
                                color = kiwiColor.colorF,
                            ),
                        color = kiwiColor.color8,
                        modifier = Modifier.width(buttonsWidth),
                        onClick = {},
                        iconRes = R.drawable.ic_swap,
                        iconSize = 15.dp,
                    )
                }
            }
        }
        Kiwi_Spacer(Spacing.medium)
        Kiwi_FixedSizeButton(
            textArguments =
                KiwiTextArguments(
                    "Apply",
                    color = kiwiColor.colorF,
                ),
            modifier = Modifier.width(buttonsWidth),
            color = kiwiColor.color7C,
            onClick = {
                // Construir el goal actualizado con el nuevo valor y estado
                val updatedGoal =
                    goal.copy(
                        value = current,
                        status = if (current >= goal.target) GoalStatus.COMPLETED else goal.status,
                    )

                coroutineScope.launch {
                    val result = goalsViewModel.updateGoal(updatedGoal)
                    result.onSuccess { updated ->
                        onGoalUpdated(updated)
                        onDismiss()
                    }.onFailure {
                        // En preview/fake no mostramos UI de error; en la app real podríamos mostrar un toast/modal
                    }
                }
            },
        )
    }
}

@Composable
fun GoalCustomiceModal(
    goal: GoalDomain,
    goalsViewModel: IGoalsViewModel,
    onDismiss: () -> Unit = {},
    onGoalUpdated: (GoalDomain) -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier.clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                    ),
            ) {
                GoalCustomice(goal, goalsViewModel, onDismiss, onGoalUpdated)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalCustomiceModal_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color6),
        ) {
            GoalCustomiceModal(
                goal =
                    GoalDomain(
                        1,
                        9000,
                        "Walk 9000 Steps",
                        GoalType.EXERCISE,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.IN_PROGRESS,
                        100,
                        value = 2000,
                    ),
                goalsViewModel = GoalsFakeViewModel(),
            )
        }
    }
}
