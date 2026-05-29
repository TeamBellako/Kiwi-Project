package com.bellako.kiwi.features.skills.screen

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.audio.AudioManager
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.KiwiColors
import com.bellako.kiwi.ui.KiwiColorsData
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlinx.coroutines.launch

private const val HOLD_DURATION_MS = 600
private const val HOLD_RESET_DURATION_MS = 150
private const val TAP_THRESHOLD_MS = 250L
private const val HOLD_FILL_ALPHA = 0.35f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillComponent(
    skill: SkillDomain,
    isDisabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current
    val context = LocalContext.current
    var showModal by remember { mutableStateOf(false) }
    val holdProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier =
            modifier
                .testTag("skill-${skill.id}")
                .alpha(if (isDisabled) KIWI_DISABLED_ALPHA else 1.0f)
                .pointerInput(isDisabled) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)

                        if (isDisabled) {
                            val release = waitForUpOrCancellation()
                            if (release != null) {
                                val pressDurationMs = release.uptimeMillis - down.uptimeMillis
                                if (pressDurationMs < TAP_THRESHOLD_MS) {
                                    showModal = true
                                }
                            }
                            return@awaitEachGesture
                        }

                        AudioManager.playSFX(context, R.raw.snd_fx_skill_hold, looping = true)

                        var actionTriggered = false
                        val animationJob =
                            coroutineScope.launch {
                                holdProgress.animateTo(1f, tween(HOLD_DURATION_MS))
                                actionTriggered = true
                                AudioManager.stopSFX(R.raw.snd_fx_skill_hold)
                                onClick()
                                holdProgress.animateTo(0f, tween(HOLD_RESET_DURATION_MS))
                            }

                        val release = waitForUpOrCancellation()
                        if (!actionTriggered) {
                            animationJob.cancel()
                            AudioManager.stopSFX(R.raw.snd_fx_skill_hold)
                            coroutineScope.launch {
                                holdProgress.animateTo(0f, tween(HOLD_RESET_DURATION_MS))
                            }
                        }

                        if (!actionTriggered && release != null) {
                            val pressDurationMs = release.uptimeMillis - down.uptimeMillis
                            if (pressDurationMs < TAP_THRESHOLD_MS) {
                                showModal = true
                            }
                        }
                    }
                },
    ) {
        SkillBackground(skill, holdProgress.value)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ICON
            Column(
                modifier =
                    Modifier
                        .padding(start = getResponsiveSizeHeight(20.dp)),
            ) {
                Kiwi_Image(
                    skill.icon,
                    "Skill Icon",
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(40.dp))
                            .alpha(if (isDisabled) 0.7f else 1.0f),
                )
            }

            // TEXT
            Column(
                modifier =
                    Modifier
                        .padding(end = getResponsiveSizeHeight(15.dp)),
            ) {
                Kiwi_Label2(
                    KiwiTextArguments(
                        text = skill.name,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ),
                )

                Kiwi_Label3(
                    KiwiTextArguments(
                        color =
                            skillStatusColor(
                                kiwiColors,
                                skill.isCooldown,
                            ),
                        text =
                            skillStatusText(
                                skill.isCooldown,
                            ),
                        italic = true,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ),
                )
            }
        }
    }

    if (showModal) {
        SkillDetailsModal(
            skill = skill,
            onDismiss = { showModal = false },
            onApplyGoalProgress,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillBackground(
    skill: SkillDomain,
    holdProgress: Float,
) {
    Box {
        Kiwi_Image(
            if (skill.isCooldown) {
                R.drawable.skill_cooldown_bg
            } else {
                R.drawable.skill_bg
            },
            "Skill background",
        )

        if (skill.isCooldown) {
            var cooldownPercentage = 0.0f

            when (skill) {
                is SkillDomain.Time -> {
                    cooldownPercentage = skill.cooldownProgress
                }
                is SkillDomain.Goal -> {
                    val progress = skill.goalData.progress
                    val target = skill.goalData.target
                    cooldownPercentage = progress.toFloat() / target.toFloat()
                }

                is SkillDomain.Other -> {}
            }

            Kiwi_Image(
                R.drawable.skill_cooldown_fill,
                "Skill cooldown percentage",
                modifier =
                    Modifier
                        .graphicsLayer {
                            clip = true
                            shape =
                                object : Shape {
                                    override fun createOutline(
                                        size: Size,
                                        layoutDirection: LayoutDirection,
                                        density: Density,
                                    ): Outline {
                                        val w = size.width * cooldownPercentage
                                        return Outline.Rectangle(Rect(0f, 0f, w, size.height))
                                    }
                                }
                        },
            )
        }

        if (holdProgress > 0f) {
            HoldProgressFill(holdProgress)
        }

        Kiwi_Image(
            skillDecoration(holdProgress > 0f, skill.isCooldown),
            "Skill background decoration",
        )
    }
}

@Composable
private fun HoldProgressFill(holdProgress: Float) {
    Kiwi_Image(
        R.drawable.skill_cooldown_fill,
        "Skill hold progress",
        colorFilter = ColorFilter.tint(Color.White),
        modifier =
            Modifier
                .alpha(HOLD_FILL_ALPHA)
                .graphicsLayer {
                    clip = true
                    shape =
                        object : Shape {
                            override fun createOutline(
                                size: Size,
                                layoutDirection: LayoutDirection,
                                density: Density,
                            ): Outline {
                                val w = size.width * holdProgress
                                return Outline.Rectangle(Rect(0f, 0f, w, size.height))
                            }
                        }
                },
    )
}

// HELPERS

@DrawableRes
fun skillDecoration(
    isHolding: Boolean,
    isCooldown: Boolean,
): Int =
    if (isHolding) {
        if (isCooldown) {
            R.drawable.skill_deco_cooldown_selected
        } else {
            R.drawable.skill_deco_selected
        }
    } else {
        R.drawable.skill_deco
    }

fun skillStatusText(isCooldown: Boolean): String =
    if (isCooldown) {
        "Cooldown"
    } else {
        "Ready"
    }

fun skillStatusColor(
    currentColors: KiwiColorsData,
    isCooldown: Boolean,
): Color =
    if (isCooldown) {
        currentColors.color8A
    } else {
        currentColors.color7A
    }

@Suppress("MagicNumber")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Skills_Preview() {
    val nav = rememberNavController()
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = nav)
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .background(KiwiColors.color2)
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .padding(
                                vertical = getResponsiveSizeHeight(Spacing.large),
                                horizontal = getResponsiveSizeHeight(Spacing.large),
                            ),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small))) {
                        SkillComponent(
                            SkillsTestFactory.timeCooldownSkillEquipped(),
                            true,
                            onClick = {},
                            Modifier.weight(0.5f),
                            { _, _, _ -> },
                        )
                        SkillComponent(
                            SkillsTestFactory.skill2(),
                            false,
                            onClick = {},
                            Modifier.weight(0.5f),
                            { _, _, _ -> },
                        )
                    }
                }
            }
        }
    }
}
