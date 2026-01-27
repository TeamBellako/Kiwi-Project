package com.bellako.kiwi.features.skills.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Label2
import com.bellako.kiwi.common.screens.components.Kiwi_Label3
import com.bellako.kiwi.common.screens.components.Kiwi_P3
import com.bellako.kiwi.common.screens.components.Kiwi_Slider
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer_Horizontal
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

private const val SKILL_MODAL_ASPECT_RATIO = 0.96f

private const val SKILL_MODAL_BODY_BIG_SPACE = 0.32f
private const val SKILL_MODAL_BODY_SMALL_SPACE = 0.18f
private const val SKILL_MODAL_BUTTON_SPACE = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillDetailsModal(
    skill: SkillDomain,
    modifier: Modifier = Modifier,
    onApplyGoalProgress: (skillId: Long, newProgress: Int) -> Unit = { _, _ -> },
) {
    val kiwiColors = LocalKiwiColors.current

    // Only cooldown by goal
    var goalProgress by remember {
        mutableFloatStateOf(
            if (skill is SkillDomain.Goal) {
                skill.goalProgress.toFloat() / skill.goalTarget.toFloat()
            } else {
                0f
            },
        )
    }
    val progressChanged =
        skill is SkillDomain.Goal &&
            (goalProgress * skill.goalTarget).toInt() != skill.goalProgress

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(SKILL_MODAL_ASPECT_RATIO),
    ) {
        // BG
        Kiwi_Image(
            R.drawable.combat_modal,
            "Skill info background",
            modifier = Modifier.fillMaxSize(),
        )

        Column {
            // HEADER
            SkillDetailsHeader(skill, kiwiColors)

            // BODY
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = getResponsiveSizeHeight(38.dp))
                        .padding(top = getResponsiveSizeHeight(Spacing.large)),
                verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.small)),
            ) {
                // QUOTE
                Box(
                    modifier =
                        Modifier
                            .weight(SKILL_MODAL_BODY_SMALL_SPACE)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    skill.quote?.let {
                        Kiwi_Label2(
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

                // DESCRIPTION
                Box(
                    modifier =
                        Modifier
                            .weight(SKILL_MODAL_BODY_BIG_SPACE)
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

                // SPACER
                Box(
                    modifier =
                        Modifier
                            .weight(SKILL_MODAL_BODY_SMALL_SPACE),
                ) {}

                // COOLDOWN SECTION
                Box(
                    modifier =
                        Modifier
                            .alpha(if (skill.isCooldown) 1.0f else KIWI_DISABLED_ALPHA)
                            .weight(SKILL_MODAL_BODY_BIG_SPACE)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    if (skill.isCooldown) {
                        when (skill) {
                            is SkillDomain.Other -> {
                                SkillCooldownOther(skill, kiwiColors)
                            }
                            is SkillDomain.Time -> {
                                SkillCooldownTime(skill, kiwiColors)
                            }
                            is SkillDomain.Goal -> {
                                SkillCooldownGoal(
                                    skill.goalAction,
                                    skill.goalProgress,
                                    skill.goalTarget,
                                    onProgressChange = { goalProgress = it },
                                    kiwiColors,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // GOAL MODIFICATION BUTTONS
    if (skill.isCooldown && skill is SkillDomain.Goal) {
        Kiwi_Spacer(Spacing.medium)
        Row {
            ApplyProgressButton(
                progressChanged,
                kiwiColors,
                Modifier.weight(SKILL_MODAL_BUTTON_SPACE),
            ) {
                val newValue =
                    (goalProgress * skill.goalTarget).toInt()

                onApplyGoalProgress(skill.id, newValue)
            }
            Kiwi_Spacer_Horizontal(Spacing.small)
            CancelButton(progressChanged, kiwiColors, Modifier.weight(SKILL_MODAL_BUTTON_SPACE))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillDetailsHeader(
    skill: SkillDomain,
    currentColors: KiwiColorsData,
) {
    Column(
        modifier =
            Modifier
                .padding(top = getResponsiveSizeHeight(26.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            // ICON
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
                // TITLE
                Kiwi_H2(
                    KiwiTextArguments(
                        text = skill.name,
                    ),
                )

                // COOLDOWN TEXT
                Kiwi_Label2(
                    KiwiTextArguments(
                        color =
                            skillStatusColor(
                                currentColors,
                                skill.isCooldown,
                            ),
                        text =
                            skillStatusText(
                                skill.isCooldown,
                            ),
                        italic = true,
                    ),
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillCooldownTime(
    skill: SkillDomain.Time,
    currentColors: KiwiColorsData,
) {
    val percentage = rememberTimeCooldownPercentage(skill)
    val totalMinutes = skill.cooldownTimeMinutes
    val currentMinutes = skill.cooldownTimeMinutes.times(percentage)

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
                color = currentColors.color6,
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
                color = currentColors.color7A,
            ),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillCooldownGoal(
    action: String,
    current: Int,
    target: Int,
    onProgressChange: (Float) -> Unit,
    currentColors: KiwiColorsData,
) {
    var sliderProgress: Float by
        remember {
            mutableFloatStateOf(
                (current.toFloat() / target.toFloat()).coerceIn(0f, 1f),
            )
        }

    val current = (sliderProgress * target).toInt()

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_Label3(
            KiwiTextArguments(
                action,
                TextAlign.Center,
                color = currentColors.color6,
            ),
        )

        Kiwi_Slider(
            value = sliderProgress,
            onValueChange = { onProgressChange(it.coerceIn(0f, 1f)) },
            steps = 100,
            testTag = "",
            valueRange = 0f..1f,
        )

        Kiwi_P3(
            KiwiTextArguments(
                "$current/$target",
                color = currentColors.color7A,
            ),
        )
    }
}

@Composable
private fun SkillCooldownOther(
    skill: SkillDomain.Other,
    currentColors: KiwiColorsData,
) {
    Kiwi_Label3(
        KiwiTextArguments(
            text = skill.cooldownOtherDescription,
            italic = true,
            color = currentColors.color8,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        ),
    )
}

@Composable
private fun ApplyProgressButton(
    enabled: Boolean,
    currentColors: KiwiColorsData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Kiwi_FixedSizeButton(
        textArguments =
            KiwiTextArguments(
                "Apply",
                color = currentColors.colorF,
            ),
        enabled = enabled,
        modifier = modifier,
        color = currentColors.color7C,
        onClick = onClick,
    )
}

@Composable
private fun CancelButton(
    enabled: Boolean,
    currentColors: KiwiColorsData,
    modifier: Modifier = Modifier,
) {
    Kiwi_FixedSizeButton(
        textArguments =
            KiwiTextArguments(
                "Cancel",
                color = currentColors.colorF,
            ),
        enabled = enabled,
        modifier = modifier,
        color = currentColors.color8,
        onClick = {
        },
    )
}

@Suppress("MagicNumber")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun SkillModal_Preview() {
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
                    SkillDetailsModal(SkillsTestFactory.timeCooldownSkillEquipped())
                }
            }
        }
    }
}
