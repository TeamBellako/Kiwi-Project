package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P3
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.utils.DateUtils.dateToString
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalDomain
import com.bellako.kiwi.features.goals.data.GoalModalType
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import java.time.LocalDate

@Composable
@Suppress("MagicNumber")
fun GoalsNotificationCard(
    type: GoalModalType,
    goals: List<IGoal>,
    onClick: () -> Unit = {},
) {
    val header =
        if (type == GoalModalType.NEW) {
            "New Daily Goals!"
        } else {
            "Yesterday's Challenge"
        }
    val body =
        if (type == GoalModalType.NEW) {
            "What can you accomplish today?"
        } else {
            "Let's check what you accomplished"
        }

    val kiwiColor = LocalKiwiColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = getResponsiveSizeHeight(Spacing.large),
                    ).padding(horizontal = getResponsiveSizeHeight(Spacing.large))
                    .clickable { onClick() }
                    .zIndex(1f),
        ) {
            Image(
                painter = painterResource(id = R.drawable.goals_notification),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.medium))
                        .padding(vertical = getResponsiveSizeHeight(Spacing.large)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier.weight(0.25f).padding(
//                            top = getResponsiveSizeHeight(Spacing.xSmall),
                            end = getResponsiveSizeHeight(Spacing.medium),
//                            bottom = getResponsiveSizeHeight(Spacing.xSmall),
                        ),
                ) {
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[0].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(30.dp))
                                .offset(x = getResponsiveSizeHeight((-4).dp)),
                        alignment = Alignment.Center,
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.small))
                    Kiwi_Image(
                        painter = painterResource(id = getIcon(goals[1].type)),
                        alt = "Goal icon",
                        colorFilter = ColorFilter.tint(kiwiColor.colorF),
                        modifier =
                            Modifier
                                .size(getResponsiveSizeHeight(30.dp))
                                .offset(x = getResponsiveSizeHeight(14.dp)),
                        alignment = Alignment.Center,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .weight(0.75f)
                            .padding(start = getResponsiveSizeHeight(Spacing.medium)),
                ) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            header,
                            color = kiwiColor.colorF,
                        ),
                    )
                    Kiwi_Spacer(getResponsiveSizeHeight(Spacing.xSmall))
                    Kiwi_P3(
                        KiwiTextArguments(
                            body,
                            color = kiwiColor.color6,
                        ),
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongMethod", "CyclomaticComplexMethod", "MagicNumber")
fun GoalsNotificationsOverlay(
    goalsViewModel: IGoalsViewModel,
    modifier: Modifier = Modifier,
) {
    // Estados reactivos para controlar las notificaciones
    var yesterdayGoals by remember { mutableStateOf<List<GoalDomain>?>(null) }
    var todayGoals by remember { mutableStateOf<List<IGoal>?>(null) }
    var showYesterdayNotification by remember { mutableStateOf(false) }
    var showTodayNotification by remember { mutableStateOf(false) }

    // Estados para controlar la visibilidad de los modales
    var showYesterdayModal by remember { mutableStateOf(false) }
    var showTodayModal by remember { mutableStateOf(false) }

    // Cargar los datos de goals una sola vez al iniciar
    @Suppress("NewApi")
    LaunchedEffect(Unit) {
        val today = dateToString(LocalDate.now())
        val inProgressResult = goalsViewModel.getGoalsInProgress()
        val todayResult = goalsViewModel.getGoalsByDate(today)

        if (inProgressResult.isSuccess) {
            val goals = inProgressResult.getOrNull() ?: emptyList()
            if (goals.isNotEmpty()) {
                yesterdayGoals = goals
                showYesterdayNotification = true
            }
        }
        if (todayResult.isSuccess) {
            val goals = todayResult.getOrNull() ?: emptyList()
            if (goals.isNotEmpty()) {
                todayGoals = goals
            } else {
                val suggestedResult = goalsViewModel.getSuggestedGoals()
                if (suggestedResult.isSuccess) {
                    val newGoals = suggestedResult.getOrNull() ?: emptyList()
                    if (newGoals.isNotEmpty()) {
                        todayGoals = newGoals
                        if (!showYesterdayNotification) {
                            showTodayNotification = true
                        }
                    }
                }
            }
        } else {
            // No hay goals en progreso, buscar sugerencias para hoy
            val suggestedResult = goalsViewModel.getSuggestedGoals()
            if (suggestedResult.isSuccess) {
                val newGoals = suggestedResult.getOrNull() ?: emptyList()
                if (newGoals.isNotEmpty()) {
                    todayGoals = newGoals
                    showTodayNotification = true
                }
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(getResponsiveSizeHeight(Spacing.large)),
        ) {
            AnimatedVisibility(
                visible = showYesterdayNotification && yesterdayGoals != null,
                enter =
                    slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
            ) {
                yesterdayGoals?.let { goals ->
                    GoalsNotificationCard(
                        type = GoalModalType.YESTERDAY,
                        goals = goals,
                        onClick = {
                            // Cerrar la notificación y abrir el modal
                            showYesterdayNotification = false
                            showYesterdayModal = true
                        },
                    )
                }
            }

            AnimatedVisibility(
                visible = showTodayNotification && todayGoals != null,
                enter =
                    slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300),
                    ),
            ) {
                todayGoals?.let { goals ->
                    GoalsNotificationCard(
                        type = GoalModalType.NEW,
                        goals = goals,
                        onClick = {
                            // Cerrar la notificación y abrir el modal
                            showTodayNotification = false
                            showTodayModal = true
                        },
                    )
                }
            }
        }
    }

    // Mostrar modales cuando corresponda
    if (showYesterdayModal && yesterdayGoals != null) {
        GoalsModal(
            goalModalType = GoalModalType.YESTERDAY,
            goals = yesterdayGoals!!,
            goalsViewModel = goalsViewModel,
            onDismiss = {
                showYesterdayModal = false
                // Mostrar notificación de hoy después de cerrar el modal de ayer
                if (todayGoals != null) {
                    showTodayNotification = true
                }
            },
        )
    }

    if (showTodayModal && todayGoals != null) {
        GoalsModal(
            goalModalType = GoalModalType.NEW,
            goals = todayGoals!!,
            goalsViewModel = goalsViewModel,
            onDismiss = {
                showTodayModal = false
            },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalsNotification_Card_Preview() {
    Kiwi_Theme {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            GoalsNotificationCard(
                type = GoalModalType.NEW,
                goals =
                    listOf(
                        GoalDomain(
                            "1",
                            "Hacer el modal",
                            "Programa el modal lo mejor que sepas",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.COMPLETED,
                            1000,
                            progress = 1f,
                        ),
                        GoalDomain(
                            "2",
                            "Haz que sea bonito",
                            "Esto está fuera de tu alcance",
                            GoalType.PRODUCTIVITY,
                            GoalCategory.DAILY_CHALLENGES,
                            GoalStatus.NOT_COMPLETED,
                            1000,
                            progress = 0.5f,
                        ),
                    ),
            )
        }
    }
}
