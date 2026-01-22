package com.bellako.kiwi.features.skills.screen

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label1
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_P3
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.skills.data.CooldownType
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
fun Skill(
    skill: SkillDomain,
    isDisabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current
    val isHolding = false

    Box(
        modifier =
            modifier
                .alpha(if (isDisabled) KIWI_DISABLED_ALPHA else 1.0f)
                .clickable(enabled = !isDisabled) {
                    onClick()
                },
    ) {
        // Background image
        Kiwi_Image(
            if (skill.isCooldown) {
                R.drawable.skill_cooldown_bg
            } else {
                R.drawable.skill_bg
            },
            "Skill background",
        )

        if (skill.isCooldown) {
            var cooldownPercentage: Float = 0.0f

            if (skill.cooldownType == CooldownType.TIME) {
                cooldownPercentage = rememberTimeCooldownPercentage(skill)
            } else if (skill.cooldownType == CooldownType.GOAL) {
                // todo
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
                                skill.cooldownType,
                            ),
                        text =
                            skillStatusText(
                                skill.isCooldown,
                                skill.cooldownType,
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
}

private const val SKILL_MODAL_ASPECT_RATIO = 5f / 4f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillDetailOverlay(
    skill: SkillDomain,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(SKILL_MODAL_ASPECT_RATIO),
    ) {
        Kiwi_Image(
            R.drawable.combat_modal,
            "Skill info background",
            modifier = Modifier.fillMaxSize(),
        )

        // HEADER
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = getResponsiveSizeHeight(Spacing.xLarge)),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Kiwi_Image(
                    skillIcon(skill.icon),
                    "Skill Icon",
                    modifier =
                        Modifier
                            .size(getResponsiveSizeHeight(48.dp)),
                )

                Column(
                    modifier =
                        Modifier
                            .padding(start = getResponsiveSizeHeight(Spacing.medium)),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            text = skill.name,
                        ),
                    )

                    Kiwi_Label2(
                        KiwiTextArguments(
                            color =
                                skillStatusColor(
                                    kiwiColors,
                                    skill.isCooldown,
                                    skill.cooldownType,
                                ),
                            text =
                                skillStatusText(
                                    skill.isCooldown,
                                    skill.cooldownType,
                                ),
                            italic = true,
                        ),
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = getResponsiveSizeHeight(38.dp))
                        .padding(top = getResponsiveSizeHeight(Spacing.large)),
                verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(0.20f)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    skill.quote?.let {
                        Kiwi_Label3(
                            KiwiTextArguments(
                                color = kiwiColors.color7A,
                                text = "\"" + it + "\"",
                                fontWeight = FontWeight.Light,
                                italic = true,
                                textAlign = TextAlign.Center,
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
                            ),
                        )
                    }
                }

                Box(
                    modifier =
                        Modifier
                            .weight(0.3f)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Kiwi_Label2(
                        KiwiTextArguments(
                            text = skill.description,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        ),
                    )
                }

                // Cooldown progress
                Box(
                    modifier =
                        Modifier
                            .weight(0.5f)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (skill.isCooldown) {
                        when (skill.cooldownType) {
                            CooldownType.TIME -> SkillCooldownTime(skill)
                            CooldownType.OTHER -> SkillCooldownOther(skill)
                            CooldownType.GOAL -> {
                                // TODO
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillCooldownTime(skill: SkillDomain) {
    val kiwiColors = LocalKiwiColors.current

    val percentage = rememberTimeCooldownPercentage(skill)
    val totalMinutes = skill.cooldownTimeMinutes
    val currentMinutes = skill.cooldownTimeMinutes?.times(percentage)

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_Label3(
            KiwiTextArguments(
                "Cooldown time",
                TextAlign.Center,
                color = kiwiColors.color6,
            ),
        )

        Kiwi_Slider(
            value = percentage,
            onValueChange = { },
            steps = 100,
            testTag = "",
            valueRange = 0f..1f,
            enabled = false,
        )

        Kiwi_P3(
            KiwiTextArguments(
                "$currentMinutes/$totalMinutes",
                color = kiwiColors.color7A,
            ),
        )
    }
}

@Composable
private fun SkillCooldownOther(skill: SkillDomain) {
    skill.cooldownOtherDescription?.let {
        Kiwi_Label3(
            KiwiTextArguments(
                text = it,
                italic = true,
                color = LocalKiwiColors.current.color8,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ),
        )
    }
}

// HELPERS

const val ONEMINUTE_SECONDS = 60f
const val ONESEC_MILLISECONDS = 1_000L

@RequiresApi(Build.VERSION_CODES.O)
fun timeCooldownPercentage(
    cooldownUntil: Instant,
    cooldownTimeMinutes: Int,
    now: Instant,
): Float {
    val totalSeconds = cooldownTimeMinutes * ONEMINUTE_SECONDS
    val remainingSeconds =
        (cooldownUntil.epochSecond - now.epochSecond).coerceAtLeast(0)

    return remainingSeconds / totalSeconds
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberTimeCooldownPercentage(skill: SkillDomain): Float {
    return produceState(
        initialValue = 1f,
        key1 = skill.id,
        key2 = skill.cooldownUntil,
    ) {
        if (
            !skill.isCooldown ||
            skill.cooldownUntil == null ||
            skill.cooldownTimeMinutes == null
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

            if (percentage <= 0f) break

            delay(ONESEC_MILLISECONDS)
        }
    }.value
}

@DrawableRes
private fun skillDecoration(
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
private fun skillIcon(skillIcon: Int): Int =
    when (skillIcon) {
        1 -> R.drawable.ic_skill_1
        2 -> R.drawable.ic_skill_2
        else -> R.drawable.ic_skill_3
    }

private fun skillStatusText(
    isCooldown: Boolean,
    cooldownType: CooldownType,
): String =
    if (isCooldown) {
        when (cooldownType) {
            CooldownType.TIME -> "Cooldown"
            CooldownType.GOAL -> "Cooldown"
            CooldownType.OTHER -> "Blocked"
        }
    } else {
        "Ready"
    }

private fun skillStatusColor(
    currentColors: KiwiColorsData,
    isCooldown: Boolean,
    cooldownType: CooldownType,
): Color =
    if (isCooldown) {
        when (cooldownType) {
            CooldownType.TIME -> currentColors.color8A
            CooldownType.GOAL -> currentColors.color8A
            CooldownType.OTHER -> currentColors.colorF1
        }
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
                    SkillDetailOverlay(SkillsTestFactory.skill1())
                    Kiwi_Spacer(Spacing.medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small))) {
                        Skill(SkillsTestFactory.timeCooldownSkillEquipped(), true, onClick = {}, Modifier.weight(0.5f))
                        Skill(SkillsTestFactory.skill2(), false, onClick = {}, Modifier.weight(0.5f))
                    }
                }
            }
        }
    }
}
