package com.bellako.kiwi.features.skills.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.bellako.kiwi.common.utils.DateUtils
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.KiwiColors
import com.bellako.kiwi.ui.KiwiColorsData
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private const val SKILL_MODAL_ASPECT_RATIO = 0.96f
private const val SKILL_MODAL_BUTTON_SPACE = 0.5f

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberInComposition")
@Composable
fun SkillDetailsModal(
    skill: SkillDomain,
    onDismiss: () -> Unit = {},
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
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
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        onClick = onDismiss,
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                    ),
        ) {
            Box(
                modifier =
                    Modifier.clickable(
                        onClick = {},
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                    ),
            ) {
                SkillDetails(skill, onApplyGoalProgress, onDismiss)
            }
        }
    }
}

const val SKILL_QUOTE_WEIGHT = 0.16f
const val SKILL_DESCRIPTION_WEIGHT = 0.32f
const val SKILL_SPACER_WEIGHT = 0.10f
const val SKILL_COOLDOWN_WEIGHT = 0.36f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkillDetails(
    skill: SkillDomain,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    // Only cooldown by goal
    var goalProgress by remember {
        mutableFloatStateOf(
            if (skill is SkillDomain.Goal) {
                val progress = skill.goalData.progress
                val target = skill.goalData.target
                progress.toFloat() / target.toFloat()
            } else {
                0f
            },
        )
    }

    val progressChanged =
        skill is SkillDomain.Goal &&
            ((goalProgress * (skill.goalData.target)).toInt() != (skill.goalData.progress))

    Column(
        modifier =
            Modifier
                .padding(horizontal = getResponsiveSizeWidth(Spacing.large)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(SKILL_MODAL_ASPECT_RATIO),
        ) {
            // BG
            Kiwi_Image(
                R.drawable.combat_modal,
                "Skill info background",
                modifier = Modifier.fillMaxWidth(),
            )

            Column {
                // HEADER
                SkillDetailsHeader(skill, kiwiColors)

                // BODY
                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = getResponsiveSizeWidth(38.dp))
                            .padding(top = getResponsiveSizeWidth(Spacing.large)),
                    verticalArrangement = Arrangement.spacedBy(getResponsiveSizeWidth(Spacing.small)),
                ) {
                    // QUOTE
                    Box(
                        modifier =
                            Modifier
                                .weight(SKILL_QUOTE_WEIGHT)
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

                    // DESCRIPTION
                    Box(
                        modifier =
                            Modifier
                                .weight(SKILL_DESCRIPTION_WEIGHT)
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
                                .weight(SKILL_SPACER_WEIGHT),
                    ) {}

                    // COOLDOWN SECTION
                    Box(
                        modifier =
                            Modifier
                                .alpha(if (skill.isCooldown) 1.0f else KIWI_DISABLED_ALPHA)
                                .weight(SKILL_COOLDOWN_WEIGHT)
                                .fillMaxWidth(),
                    ) {
                        when (skill) {
                            is SkillDomain.Other -> {
                                SkillCooldownOther(skill, kiwiColors)
                            }

                            is SkillDomain.Time -> {
                                SkillCooldownTime(skill, kiwiColors)
                            }

                            is SkillDomain.Goal -> {
                                SkillCooldownGoal(
                                    skill.goalData.action,
                                    goalProgress,
                                    skill.goalData.target,
                                    onProgressChange = { goalProgress = it },
                                    enabled = skill.isCooldown,
                                    kiwiColors,
                                    skill.isCooldown
                                )
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
                CancelButton(
                    progressChanged,
                    kiwiColors,
                    Modifier.weight(SKILL_MODAL_BUTTON_SPACE),
                    onDismiss,
                )
                Kiwi_Spacer_Horizontal(Spacing.small)
                ApplyProgressButton(
                    progressChanged,
                    kiwiColors,
                    Modifier.weight(SKILL_MODAL_BUTTON_SPACE),
                ) {
                    val target = skill.goalData.target
                    val newValue =
                        (goalProgress * target).toInt()

                    onApplyGoalProgress(skill.id, skill.goalData.id, newValue)
                    if (newValue >= target) {
                        onDismiss()
                    }
                }
            }
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
                .padding(top = getResponsiveSizeWidth(26.dp)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            // ICON
            Kiwi_Image(
                skill.icon,
                "Skill Icon",
                modifier =
                    Modifier
                        .size(getResponsiveSizeWidth(48.dp)),
            )

            Column(
                modifier =
                    Modifier
                        .padding(start = getResponsiveSizeWidth(Spacing.medium)),
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

const val ONE_MINUTE_SECONDS = 60f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillCooldownTime(
    skill: SkillDomain.Time,
    currentColors: KiwiColorsData,
) {
    val percentage = skill.cooldownProgress
    val totalTime =
        DateUtils.parseTimeSeconds(
            (skill.cooldownTimeMinutes * ONE_MINUTE_SECONDS).toInt(),
        )
    val currentTime =
        DateUtils.parseTimeSeconds(
            (skill.cooldownTimeMinutes.times(percentage) * ONE_MINUTE_SECONDS).toInt(),
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
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
                    "$currentTime/$totalTime",
                    color = currentColors.color7A,
                ),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SkillCooldownGoal(
    action: String,
    goalProgress: Float,
    goalTarget: Int,
    onProgressChange: (Float) -> Unit,
    enabled: Boolean,
    currentColors: KiwiColorsData,
    isCooldown: Boolean,
) {
    val currentValue = (goalProgress * goalTarget).toInt()

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Kiwi_Label3(
                KiwiTextArguments(
                    text = action,
                    textAlign = TextAlign.Center,
                    color = currentColors.color6,
                ),
            )

            Kiwi_Slider(
                value = goalProgress,
                onValueChange = { newProgress ->
                    onProgressChange(newProgress.coerceIn(0f, 1f))
                },
                steps = 100,
                valueRange = 0f..1f,
                testTag = "",
                enabled = isCooldown,
            )

            Kiwi_P3(
                KiwiTextArguments(
                    text = "$currentValue/$goalTarget",
                    color = currentColors.color7A,
                ),
            )
        }
    }
}

@Composable
private fun SkillCooldownOther(
    skill: SkillDomain.Other,
    currentColors: KiwiColorsData,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_Label3(
            KiwiTextArguments(
                text = skill.cooldownOtherDescription,
                italic = true,
                color = currentColors.color8,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(), // ancho completo
            ),
        )
    }
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
    onClick: () -> Unit,
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
        onClick = onClick,
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
                SkillDetailsModal(SkillsTestFactory.skill1(), {}, { _, _, _ -> })
            }
        }
    }
}
