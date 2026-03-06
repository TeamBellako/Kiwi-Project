package com.bellako.kiwi.features.goals.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.modals.WIPPopUpScreen
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
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
fun GoalCustomize(
    goal: UserGoalStatusDomain,
    goalsViewModel: IGoalsViewModel,
    onDismiss: () -> Unit = {},
    onGoalUpdated: (UserGoalStatusDomain) -> Unit = {},
) {
    val buttonsWidth = getResponsiveSizeWidth(150.dp)
    val coroutineScope = rememberCoroutineScope()
    val kiwiColor = LocalKiwiColors.current

    val initialProgress = goal.value.toFloat() / goal.target.toFloat()

    // Estado local para el progreso del slider (inicializado con el valor del goal)
    var sliderProgress: Float by remember { mutableFloatStateOf(initialProgress.coerceIn(0f, 1f)) }

    var showWorkInProgressPopup by remember { mutableStateOf(false) }

    // Si el goal cambia (p. ej. se abre otro goal), sincronizamos el slider
    LaunchedEffect(goal.id) {
        sliderProgress = (goal.value.toFloat() / goal.target.toFloat()).coerceIn(0f, 1f)
    }

    val current = (sliderProgress * goal.target).roundToInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = getResponsiveSizeWidth(Spacing.large)),
    ) {
        Box {
            Kiwi_Image(
                painter = painterResource(id = R.drawable.goal_customize_modal),
                alt = "Goal customize modal",
                modifier = Modifier.fillMaxWidth(),
            )
            Kiwi_Image(
                painter = painterResource(id = goalIcon(goal.type)),
                alt = "Goal icon",
                modifier =
                    Modifier
                        .padding(top = getResponsiveSizeWidth(18.dp))
                        .size(getResponsiveSizeWidth(38.dp))
                        .align(Alignment.TopCenter),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier =
                    Modifier
                        .matchParentSize()
                        .padding(top = getResponsiveSizeWidth(60.dp))
                        .padding(
                            horizontal =
                                getResponsiveSizeWidth(Spacing.medium),
                            vertical =
                                getResponsiveSizeWidth(Spacing.medium),
                        ),
            ) {
                Kiwi_H2(
                    KiwiTextArguments(
                        goal.action,
                        TextAlign.Center,
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

                Kiwi_P2(
                    KiwiTextArguments(
                        "$current/${goal.target}",
                        color = kiwiColor.color7A,
                    ),
                )

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(getResponsiveSizeWidth(4.dp))
                            .padding(horizontal = getResponsiveSizeWidth(Spacing.small))
                            .background(kiwiColor.color2),
                )

                Kiwi_Label1(KiwiTextArguments("Difficulty"))

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.medium)
                            .background(
                                color = kiwiColor.color2,
                                shape = RoundedCornerShape(getResponsiveSizeWidth(25.dp)),
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

                Kiwi_FixedSizeButton(
                    textArguments =
                        KiwiTextArguments(
                            "Swap",
                            color = kiwiColor.colorF,
                        ),
                    color = kiwiColor.color8,
                    modifier = Modifier.width(buttonsWidth).padding(top = getResponsiveSizeHeight(5.dp)),
                    onClick = { showWorkInProgressPopup = true },
                    iconRes = R.drawable.ic_swap,
                    iconSize = 15.dp,
                )
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
                    result
                        .onSuccess { updated ->
                            onGoalUpdated(updated)
                            onDismiss()
                        }.onFailure {
                            // En preview/fake no mostramos UI de error; en la app real podríamos mostrar un toast/modal
                        }
                }
            },
        )
    }

    // Popup de "Work in progress"
    if (showWorkInProgressPopup) {
        WIPPopUpScreen(onDismiss = { showWorkInProgressPopup = false })
    }
}

@SuppressLint("RememberInComposition")
@Composable
fun GoalCustomizeModal(
    goal: UserGoalStatusDomain,
    goalsViewModel: IGoalsViewModel,
    onDismiss: () -> Unit = {},
    onGoalUpdated: (UserGoalStatusDomain) -> Unit = {},
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
                GoalCustomize(goal, goalsViewModel, onDismiss, onGoalUpdated)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
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
            GoalCustomizeModal(
                goal =
                    UserGoalStatusDomain(
                        1,
                        1,
                        "Walk 9000 Steps",
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
