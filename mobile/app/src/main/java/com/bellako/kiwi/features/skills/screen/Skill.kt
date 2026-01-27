package com.bellako.kiwi.features.skills.screen

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
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
import kotlinx.coroutines.delay
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillComponent(
    skill: SkillDomain,
    isDisabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onApplyGoalProgress: (skillId: Long, newProgress: Int) -> Unit = { _, _ -> },
) {
    val kiwiColors = LocalKiwiColors.current
    var showModal by remember { mutableStateOf(false) }

    Box(
        modifier =
            modifier
                .alpha(if (isDisabled) KIWI_DISABLED_ALPHA else 1.0f)
                .pointerInput(isDisabled) {
                    detectTapGestures(
                        onTap = {
                            if (!isDisabled) {
                                onClick()
                            }
                        },
                        onLongPress = {
                            showModal = true
                        },
                    )
                },
    ) {
        SkillBackground(skill, showModal)

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
                    skillIcon(skill.icon),
                    "Skill Icon",
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(40.dp)),
                )
            }

            // TEXT
            Column(
                modifier =
                    Modifier
                        .padding(end = getResponsiveSizeHeight(15.dp)),
            ) {
                Kiwi_Label1(
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
    isHolding: Boolean,
) {
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
                cooldownPercentage = rememberTimeCooldownPercentage(skill)
            }
            is SkillDomain.Goal -> {
                cooldownPercentage = skill.goalProgress.toFloat() / skill.goalTarget.toFloat()
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

    Kiwi_Image(
        skillDecoration(isHolding, skill.isCooldown),
        "Skill background decoration",
    )
}

// HELPERS

const val ONE_MINUTE_SECONDS = 60f
const val ONE_SEC_MILLISECONDS = 1_000L

@RequiresApi(Build.VERSION_CODES.O)
fun timeCooldownPercentage(
    cooldownUntil: Instant,
    cooldownTimeMinutes: Int,
    now: Instant,
): Float {
    val totalSeconds = cooldownTimeMinutes * ONE_MINUTE_SECONDS
    val remainingSeconds =
        (cooldownUntil.epochSecond - now.epochSecond).coerceAtLeast(0)

    val remainingRatio = remainingSeconds.toFloat() / totalSeconds

    return (1f - remainingRatio).coerceIn(0f, 1f)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberTimeCooldownPercentage(skill: SkillDomain.Time): Float {
    return produceState(
        initialValue = 0f,
        key1 = skill.id,
        key2 = skill.cooldownUntil,
    ) {
        if (
            !skill.isCooldown ||
            skill.cooldownUntil == null
        ) {
            value = 0f
            return@produceState
        }

        while (true) {
            val now = Instant.now()

            val percentage =
                timeCooldownPercentage(
                    cooldownUntil = skill.cooldownUntil,
                    cooldownTimeMinutes = skill.cooldownTimeMinutes,
                    now = now,
                )

            value = percentage

            if (percentage <= 1f) break

            delay(ONE_SEC_MILLISECONDS)
        }
    }.value
}

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

@DrawableRes
fun skillIcon(skillIcon: Int): Int =
    when (skillIcon) {
        1 -> R.drawable.ic_skill_1
        2 -> R.drawable.ic_skill_2
        else -> R.drawable.ic_skill_3
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
                        SkillComponent(SkillsTestFactory.timeCooldownSkillEquipped(), true, onClick = {}, Modifier.weight(0.5f))
                        SkillComponent(SkillsTestFactory.skill2(), false, onClick = {}, Modifier.weight(0.5f))
                    }
                }
            }
        }
    }
}
