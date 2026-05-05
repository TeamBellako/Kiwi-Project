package com.bellako.kiwi.features.combat.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.utils.AssetResolver
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_H3
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_P2
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.combat.components.CombatLog
import com.bellako.kiwi.features.combat.components.buildCombatLogEntries
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.KiwiColors
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth

private const val DEFEAT_BACKGROUND_SATURATION = 0.05f
private const val DEFEAT_BACKGROUND_DIM_ALPHA = 0.7f
private const val EDGE_FADE_TOP_ALPHA = 0.85f
private const val EDGE_FADE_BOTTOM_ALPHA = 0.95f
private const val EDGE_FADE_TOP_END = 0.18f
private const val EDGE_FADE_BOTTOM_START = 0.55f
private const val LOG_HEIGHT_FRACTION = 0.7f
private val CONTINUE_BUTTON_HORIZONTAL_MARGIN = 56.dp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CombatDefeatScreen(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    onContinue: () -> Unit,
) {
    val colors = LocalKiwiColors.current
    val skillsByName = remember(deckSkills) { deckSkills.associateBy { it.name } }
    val skillsUsed =
        remember(combat.log, deckSkills) {
            buildSkillUsedSummary(combat, skillsByName)
        }
    var isLogOpen by rememberSaveable(combat.id) { mutableStateOf(false) }

    val logEntries =
        remember(combat.log, combat.combatStatus, combat.enemyName, colors) {
            buildCombatLogEntries(
                actions = combat.log,
                enemyName = combat.enemyName.ifBlank { "Enemy" },
                combatStatus = combat.combatStatus,
                colors = colors,
            )
        }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.color2),
    ) {
        DefeatBackground(combat.background)

        Column(modifier = Modifier.fillMaxSize()) {
            DefeatHeader()

            Kiwi_Spacer(Spacing.large)

            DefeatBanner()

            Kiwi_Spacer(Spacing.large)

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeWidth(Spacing.medium)),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SkillsUsedHeader(
                        isLogOpen = isLogOpen,
                        onToggleLog = { isLogOpen = !isLogOpen },
                    )

                    Kiwi_Spacer(Spacing.small)

                    SkillsUsedList(skillsUsed = skillsUsed)
                }

                if (isLogOpen) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = { isLogOpen = false },
                                ),
                    )
                    CombatLog(
                        entries = logEntries,
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .fillMaxHeight(LOG_HEIGHT_FRACTION)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {},
                                ),
                    )
                }
            }

            Kiwi_Spacer(Spacing.medium)

            Kiwi_FixedSizeButton(
                textArguments =
                    KiwiTextArguments(
                        text = "Continue",
                        color = colors.colorF,
                        fontWeight = FontWeight.Bold,
                    ),
                onClick = onContinue,
                color = colors.color5,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = getResponsiveSizeWidth(CONTINUE_BUTTON_HORIZONTAL_MARGIN)),
            )

            Kiwi_Spacer(Spacing.large)
        }
    }
}

@Composable
private fun DefeatHeader() {
    val colors = LocalKiwiColors.current
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                    vertical = getResponsiveSizeHeight(Spacing.medium),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Kiwi_H3(
            KiwiTextArguments(
                text = "Results",
                color = colors.colorF,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun DefeatBanner() {
    val colors = LocalKiwiColors.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Kiwi_H2(
            KiwiTextArguments(
                text = "You Have Been Defeated",
                color = colors.colorF,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Kiwi_Spacer(Spacing.small)
        Kiwi_P2(
            KiwiTextArguments(
                text = "Your Will Is Not Strong Enough Yet",
                color = colors.color7A,
                italic = true,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
    }
}

@Composable
private fun DefeatBackground(background: String?) {
    val colors = LocalKiwiColors.current
    val resId = AssetResolver.drawable(LocalContext.current, background) ?: return
    Kiwi_Image(
        painterResourceId = resId,
        alt = "Combat background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        colorFilter =
            ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(DEFEAT_BACKGROUND_SATURATION) },
            ),
    )
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.color2.copy(alpha = DEFEAT_BACKGROUND_DIM_ALPHA)),
    )
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to colors.color2.copy(alpha = EDGE_FADE_TOP_ALPHA),
                        EDGE_FADE_TOP_END to Color.Transparent,
                        EDGE_FADE_BOTTOM_START to Color.Transparent,
                        1f to colors.color2.copy(alpha = EDGE_FADE_BOTTOM_ALPHA),
                    ),
                ),
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun CombatDefeatScreen_Preview() {
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = rememberNavController())
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .background(KiwiColors.color2)
                        .padding(paddingValues)
                        .fillMaxSize(),
            ) {
                CombatDefeatScreen(
                    combat = previewDefeatedCombat(),
                    deckSkills =
                        listOf(
                            SkillsTestFactory.timeCooldownSkillEquipped(),
                            SkillsTestFactory.goalCooldownSkillEquipped(),
                        ),
                    onContinue = {},
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
private fun previewDefeatedCombat(): CombatDomain {
    val base =
        CombatTestFactory.validCombatDomain(
            enemyName = "Procrastinogre",
            enemySprite = "liria_neutral",
            background = "background_mindveil",
            user =
                CombatTestFactory.validCombatActorDomain(
                    stats =
                        CombatTestFactory.validCombatStatsDomain(
                            currentHp = 0,
                            maxHp = 18000,
                        ),
                ),
        )
    val log =
        listOf(
            CombatTestFactory.skillUsedAction(skillName = "Strategic Advantage"),
            CombatTestFactory.skillUsedAction(actor = CombatActor.ENEMY, skillName = "Insomnia"),
            CombatTestFactory.skillUsedAction(skillName = "Goal Skill"),
            CombatTestFactory.skillUsedAction(skillName = "Strategic Advantage"),
            CombatTestFactory.skillUsedAction(skillName = "Goal Skill"),
            CombatTestFactory.skillUsedAction(skillName = "Goal Skill"),
        )
    return base.copy(log = log, combatStatus = CombatGeneralStatus.USER_LOST)
}
