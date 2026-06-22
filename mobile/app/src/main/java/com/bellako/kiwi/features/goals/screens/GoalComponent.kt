// Kotlin
package com.bellako.kiwi.features.goals.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.services.eventbus.EventBus
import com.bellako.kiwi.common.services.eventbus.EventPayload
import com.bellako.kiwi.common.services.eventbus.EventType
import com.bellako.kiwi.features.goals.data.GoalCategory
import com.bellako.kiwi.features.goals.data.GoalStatus
import com.bellako.kiwi.features.goals.data.GoalType
import com.bellako.kiwi.features.goals.data.IGoal
import com.bellako.kiwi.features.goals.data.UserGoalStatusDomain
import com.bellako.kiwi.features.goals.model.IGoalsViewModel
import com.bellako.kiwi.features.goals.tests.GoalsFakeViewModel
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.launch

@Suppress("MagicNumber", "LongMethod", "CyclomaticComplexMethod")
@Composable
fun GoalComponent(
    goal: IGoal,
    goalsViewModel: IGoalsViewModel,
    modifier: Modifier = Modifier,
    plus: Boolean = true,
) {
    var currentGoal by remember(goal.id) { mutableStateOf<IGoal>(goal) }
    var showModal by remember { mutableStateOf(false) }
    var showConfirmCompletion by remember { mutableStateOf(false) }

    val goalDomain = currentGoal as? UserGoalStatusDomain
    val status = goalDomain?.status ?: GoalStatus.IN_PROGRESS
    val kiwiColors = LocalKiwiColors.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val targetProgress: Float =
        if (goalDomain == null) {
            0f
        } else {
            goalDomain.value.toFloat() / goalDomain.target.toFloat()
        }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec =
            tween(
                durationMillis = 800,
                easing = EaseInOut,
            ),
        label = "progressAnimation",
    )

    // Color flips to the "completed" palette only after the user has
    // confirmed the goal — hitting the target does not turn the bar/icon
    // gold by itself. Confirmed goals stay gold even if they're later
    // edited further.
    val isConfirmedComplete = status == GoalStatus.COMPLETED

    // One-shot press animation on the plus icon. The Animatables are owned
    // by the parent so the increment click handler can drive them.
    val pressScale = remember(goal.id) { Animatable(1f) }
    val pressRotation = remember(goal.id) { Animatable(0f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .height(IntrinsicSize.Min)
                .clickable { showModal = true },
    ) {
        Kiwi_Image(
            R.drawable.daily_challenges_bg,
            "Bar bg",
            modifier = Modifier.fillMaxWidth(),
        )

        Kiwi_Image(
            if (isConfirmedComplete) {
                R.drawable.daily_challenges_completed
            } else {
                R.drawable.daily_challenges_fill
            },
            "Bar fill",
            modifier =
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        clip = true
                        shape =
                            object : Shape {
                                override fun createOutline(
                                    size: Size,
                                    layoutDirection: LayoutDirection,
                                    density: Density,
                                ): Outline {
                                    val w = size.width * animatedProgress
                                    return Outline.Rectangle(Rect(0f, 0f, w, size.height))
                                }
                            }
                    },
            contentScale = ContentScale.FillWidth,
        )

        Row(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(end = getResponsiveSizeHeight(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                Kiwi_Image(
                    goalIcon(currentGoal.type),
                    "Quest Indicator For: ${currentGoal.target}",
                    Modifier.padding(vertical = getResponsiveSizeWidth(13.dp)),
                    colorFilter =
                        ColorFilter.tint(
                            if (isConfirmedComplete) {
                                kiwiColors.color8C
                            } else {
                                kiwiColors.colorF1
                            },
                        ),
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(0.6f),
            ) {
                Kiwi_Label3(
                    KiwiTextArguments(
                        currentGoal.resolveAction(),
                        TextAlign.Center,
                        kiwiColors.color6,
                    ),
                )
            }

            Box(
                modifier =
                    Modifier
                        .weight(0.10f)
                        .fillMaxHeight()
                        // No ripple — the plus has its own squish + wiggle press
                        // feedback, so the default dark overlay is redundant.
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            // IN_PROGRESS goals confirm at target; COMPLETED goals
                            // are post-confirmation edits — bump value but never
                            // re-trigger backend completion.
                            if (status != GoalStatus.IN_PROGRESS && status != GoalStatus.COMPLETED) {
                                return@clickable
                            }

                            AudioManager.playSFX(context, R.raw.snd_ui_check)

                            // Press feedback: a quick squish + wiggle so the tap
                            // reads even when the value doesn't visibly change
                            // (e.g. already past target on a COMPLETED goal).
                            coroutineScope.launch {
                                pressScale.snapTo(1f)
                                pressScale.animateTo(0.82f, tween(70, easing = EaseInOut))
                                pressScale.animateTo(1f, tween(130, easing = EaseInOut))
                            }
                            coroutineScope.launch {
                                pressRotation.snapTo(0f)
                                pressRotation.animateTo(-12f, tween(60, easing = EaseInOut))
                                pressRotation.animateTo(12f, tween(80, easing = EaseInOut))
                                pressRotation.animateTo(0f, tween(60, easing = EaseInOut))
                            }

                            val atTarget =
                                goalDomain != null && goalDomain.value >= goalDomain.target
                            if (status == GoalStatus.IN_PROGRESS && atTarget) {
                                showConfirmCompletion = true
                            } else {
                                coroutineScope.launch {
                                    val result = goalsViewModel.updateGoalProgress(currentGoal.id)
                                    result.onSuccess { updatedGoal ->
                                        // Preserve the confirmed status locally so
                                        // the bar/icon stay gold and we don't
                                        // accidentally route back through the
                                        // confirm-completion flow.
                                        currentGoal =
                                            if (status == GoalStatus.COMPLETED &&
                                                updatedGoal is UserGoalStatusDomain
                                            ) {
                                                updatedGoal.copy(status = GoalStatus.COMPLETED)
                                            } else {
                                                updatedGoal
                                            }
                                    }
                                }
                            }
                        },
                contentAlignment = Alignment.Center,
            ) {
                if (plus && (status == GoalStatus.IN_PROGRESS || status == GoalStatus.COMPLETED)) {
                    // A confirmed (completed) goal shows a static tick. An
                    // unconfirmed goal that's just hit its target shows the same
                    // tick but pulsing, as a "ready to confirm" cue.
                    val isReadyToConfirm =
                        status == GoalStatus.IN_PROGRESS && goalDomain?.value == goalDomain?.target
                    val showTick = isReadyToConfirm || isConfirmedComplete
                    val iconModifier =
                        if (isReadyToConfirm) {
                            val transition = rememberInfiniteTransition(label = "tickPulse")
                            val scale by transition.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.2f,
                                animationSpec =
                                    infiniteRepeatable(
                                        animation = tween(650, easing = EaseInOut),
                                        repeatMode = RepeatMode.Reverse,
                                    ),
                                label = "tickScale",
                            )
                            val glowAlpha by transition.animateFloat(
                                initialValue = 0.55f,
                                targetValue = 1f,
                                animationSpec =
                                    infiniteRepeatable(
                                        animation = tween(650, easing = EaseInOut),
                                        repeatMode = RepeatMode.Reverse,
                                    ),
                                label = "tickAlpha",
                            )
                            Modifier
                                .padding(getResponsiveSizeHeight(8.dp))
                                .graphicsLayer {
                                    scaleX = scale * pressScale.value
                                    scaleY = scale * pressScale.value
                                    rotationZ = pressRotation.value
                                    alpha = glowAlpha
                                }
                        } else {
                            Modifier
                                .padding(getResponsiveSizeHeight(8.dp))
                                .graphicsLayer {
                                    scaleX = pressScale.value
                                    scaleY = pressScale.value
                                    rotationZ = pressRotation.value
                                }
                        }
                    Kiwi_Image(
                        if (showTick) {
                            R.drawable.ic_daily_challenges_tick
                        } else {
                            R.drawable.ic_daily_challenges_plus
                        },
                        "Quest Indicator For: ${currentGoal.target}",
                        iconModifier,
                    )
                }
            }
        }
    }

    if (showModal && currentGoal is UserGoalStatusDomain) {
        GoalCustomizeModal(
            goal = currentGoal as UserGoalStatusDomain,
            goalsViewModel = goalsViewModel,
            onDismiss = { showModal = false },
            onGoalUpdated = { updatedGoal ->
                currentGoal = updatedGoal
            },
        )
    }

    if (showConfirmCompletion && goalDomain != null) {
        GoalConfirmCompletionModal(
            rewardPoints = goalDomain.reward,
            onDismiss = { showConfirmCompletion = false },
            onConfirm = {
                showConfirmCompletion = false
                val goalToComplete = goalDomain
                coroutineScope.launch {
                    val result = goalsViewModel.completeGoal(goalToComplete.id)
                    result.onSuccess {
                        currentGoal = goalToComplete.copy(status = GoalStatus.COMPLETED)
                        EventBus.emitEvent(
                            EventType.MAP_CONTENT_AVAILABLE,
                            EventPayload.EmptyPayload(),
                        )
                    }
                }
            },
        )
    }
}

fun goalIcon(goalType: GoalType): Int =
    when (goalType) {
        GoalType.EXERCISE -> R.drawable.ic_daily_challenge_physical
        GoalType.PRODUCTIVITY -> R.drawable.ic_daily_challenge_mental
        GoalType.SLEEP -> R.drawable.ic_daily_challenge_mental
        GoalType.MEDITATION -> R.drawable.ic_daily_challenge_mental
        GoalType.NUTRITION -> R.drawable.ic_daily_challenge_mental
    }

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Suppress("MagicNumber")
@Composable
fun GoalComponent_Preview() {
    // Evitar construir el ViewModel directamente en la composición del preview
    val goalFakeViewModel = remember { GoalsFakeViewModel() }
    Kiwi_Theme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(LocalKiwiColors.current.color0),
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeHeight(Spacing.xLarge)),
            ) {
                GoalComponent(
                    UserGoalStatusDomain(
                        1,
                        1,
                        "Programa el modal lo mejor que sepas",
                        1000,
                        "Programa el modal lo mejor que sepas",
                        GoalType.PRODUCTIVITY,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.COMPLETED,
                        1000,
                        value = 1,
                        onCompletedEvent = "_",
                        onCompletedEntityId = 0,
                    ),
                    goalFakeViewModel,
                )
                GoalComponent(
                    UserGoalStatusDomain(
                        2,
                        10,
                        "Programa el modal lo mejor que sepas",
                        1000,
                        "Programa el modal lo mejor que sepas",
                        GoalType.EXERCISE,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.NOT_COMPLETED,
                        1000,
                        value = 2,
                        onCompletedEvent = "_",
                        onCompletedEntityId = 0,
                    ),
                    goalFakeViewModel,
                )
                GoalComponent(
                    UserGoalStatusDomain(
                        3,
                        20,
                        "Programa el modal lo mejor que sepas",
                        1000,
                        "Programa el modal lo mejor que sepas",
                        GoalType.MEDITATION,
                        GoalCategory.DAILY_CHALLENGES,
                        GoalStatus.IN_PROGRESS,
                        1000,
                        value = 0,
                        onCompletedEvent = "_",
                        onCompletedEntityId = 0,
                    ),
                    goalFakeViewModel,
                )
            }
        }
    }
}
