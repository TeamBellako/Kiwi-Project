package com.bellako.kiwi.features.combat.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.combat.components.CombatAbandonConfirmModal
import com.bellako.kiwi.features.combat.components.CombatBackground
import com.bellako.kiwi.features.combat.components.CombatBattleArea
import com.bellako.kiwi.features.combat.components.CombatHeader
import com.bellako.kiwi.features.combat.components.CombatTurnIndicator
import com.bellako.kiwi.features.combat.components.DeathSequenceOverlay
import com.bellako.kiwi.features.combat.components.LogDimOverlay
import com.bellako.kiwi.features.combat.components.PlayerControls
import com.bellako.kiwi.features.combat.components.PlayerDamageOverlays
import com.bellako.kiwi.features.combat.components.buildCombatLogEntries
import com.bellako.kiwi.features.combat.components.rememberDeathSequenceVfx
import com.bellako.kiwi.features.combat.components.rememberPlayerDamageVfx
import com.bellako.kiwi.features.combat.components.userTurnMessage
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
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

private const val DEFAULT_ENEMY_NAME = "Enemy"
private const val BOTTOM_PANEL_GRADIENT_START = -0.2f
private const val BOTTOM_PANEL_GRADIENT_MID = 0.5f
private const val BOTTOM_PANEL_GRADIENT_END = 1f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CombatScreen(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    isTurnPlaying: Boolean = false,
    onConfirmAbandon: () -> Unit = {},
    onSkillClick: (skillId: Long, skillName: String) -> Unit = { _, _ -> },
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit = { _, _, _ -> },
) {
    val colors = LocalKiwiColors.current
    val context = LocalContext.current
    var isLogOpen by rememberSaveable(combat.id) { mutableStateOf(false) }
    var selectedStatus by remember(combat.id) { mutableStateOf<CombatActiveStatusDomain?>(null) }
    var showAbandonConfirm by rememberSaveable(combat.id) { mutableStateOf(false) }
    val isOverlayOpen = isLogOpen || selectedStatus != null || isTurnPlaying

    val enemyName = combat.enemyName.ifBlank { DEFAULT_ENEMY_NAME }
    val turnMessage = currentTurnMessage(combat, enemyName)
    val logEntries =
        remember(combat.log, combat.combatStatus, enemyName, colors) {
            buildCombatLogEntries(
                actions = combat.log,
                enemyName = enemyName,
                combatStatus = combat.combatStatus,
                colors = colors,
            )
        }

    val playerVfx = rememberPlayerDamageVfx(combat.user.stats.currentHp, combat.id)
    val deathVignetteAlpha = rememberDeathSequenceVfx(combat.user.stats.currentHp == 0, combat.id)

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.color2),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationX = playerVfx.shakeOffsetX.value },
        ) {
            CombatBackground(combat.backgroundId)

            Column(modifier = Modifier.fillMaxSize()) {
                Box {
                    CombatHeader(
                        title = "Ongoing Combat",
                        onClose = { showAbandonConfirm = true },
                    )
                    if (isLogOpen) {
                        LogDimOverlay(
                            modifier = Modifier.matchParentSize(),
                            onDismiss = { isLogOpen = false },
                        )
                    }
                }

                CombatBattleArea(
                    combat = combat,
                    isLogOpen = isLogOpen,
                    onDismissLog = { isLogOpen = false },
                    logEntries = logEntries,
                    context = context,
                    isEnemyDefeated = combat.combatStatus == CombatGeneralStatus.USER_WON,
                )

                Box {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        BOTTOM_PANEL_GRADIENT_START to Color.Transparent,
                                        BOTTOM_PANEL_GRADIENT_MID to colors.color2,
                                        BOTTOM_PANEL_GRADIENT_END to colors.color2,
                                    ),
                                ).padding(
                                    horizontal = getResponsiveSizeWidth(Spacing.medium),
                                    vertical = getResponsiveSizeHeight(Spacing.small),
                                ),
                    ) {
                        CombatTurnIndicator(
                            message = turnMessage,
                            isLogOpen = isLogOpen,
                            onClick = { isLogOpen = !isLogOpen },
                        )

                        Kiwi_Spacer(Spacing.medium)

                        PlayerControls(
                            deckSkills = deckSkills,
                            userActor = combat.user,
                            isOverlayOpen = isOverlayOpen,
                            selectedStatus = selectedStatus,
                            onSkillClick = onSkillClick,
                            onApplyGoalProgress = onApplyGoalProgress,
                            onStatusClick = { status ->
                                selectedStatus = if (selectedStatus?.stateId == status.stateId) null else status
                            },
                            onDismissPopup = { selectedStatus = null },
                        )

                        Kiwi_Spacer(Spacing.large)
                    }

                    if (isLogOpen) {
                        LogDimOverlay(
                            modifier = Modifier.matchParentSize(),
                            onDismiss = { isLogOpen = false },
                        )
                    }
                }
            }
        }

        PlayerDamageOverlays(playerVfx)
        DeathSequenceOverlay(deathVignetteAlpha.value)
    }

    if (showAbandonConfirm) {
        CombatAbandonConfirmModal(
            onConfirm = {
                showAbandonConfirm = false
                onConfirmAbandon()
            },
            onCancel = { showAbandonConfirm = false },
        )
    }
}

@Composable
private fun currentTurnMessage(
    combat: CombatDomain,
    enemyName: String,
): AnnotatedString {
    val colors = LocalKiwiColors.current
    val lastSkill =
        combat.log.lastOrNull { it.actionType == CombatActionType.SKILL_USED }
    val skillName = lastSkill?.skillName
    if (lastSkill == null || skillName == null) return userTurnMessage()

    val actorName = if (lastSkill.actor == CombatActor.USER) "You" else enemyName
    return buildAnnotatedString {
        withStyle(SpanStyle(color = colors.color7A, fontStyle = FontStyle.Italic)) {
            append(actorName)
        }
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(" used ") }
        withStyle(SpanStyle(color = colors.color8A, fontStyle = FontStyle.Italic)) {
            append(skillName)
        }
        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("!") }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun CombatScreen_Preview() {
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
                CombatScreen(
                    combat = previewCombat(),
                    deckSkills =
                        listOf(
                            SkillsTestFactory.timeCooldownSkillEquipped(),
                            SkillsTestFactory.goalCooldownSkillEquipped(),
                        ),
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("MagicNumber")
@Preview(name = "Log open", widthDp = 392, heightDp = 800)
@Composable
fun CombatScreen_LogOpen_Preview() {
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
                val combat =
                    previewCombat().copy(
                        log =
                            listOf(
                                CombatTestFactory.skillUsedAction(skillName = "Smite"),
                                CombatActionDomain(
                                    actor = CombatActor.ENEMY,
                                    actionType = CombatActionType.ACTOR_DAMAGED_BY_STATE,
                                    stateName = "venom",
                                ),
                                CombatTestFactory.skillUsedAction(skillName = "Wind"),
                                CombatTestFactory.skillUsedAction(
                                    actor = CombatActor.ENEMY,
                                    skillName = "Insomnia",
                                ),
                            ),
                    )
                CombatScreen(
                    combat = combat,
                    deckSkills =
                        listOf(
                            SkillsTestFactory.timeCooldownSkillEquipped(),
                            SkillsTestFactory.goalCooldownSkillEquipped(),
                        ),
                )
            }
        }
    }
}

@Suppress("MagicNumber")
private fun previewCombat(): CombatDomain =
    CombatTestFactory.validCombatDomain(
        enemyName = "Procrastinogre",
        enemySprite = "liria_neutral",
        backgroundId = 1L,
        endsAt = System.currentTimeMillis() + 7L * 3600L * 1000L,
        enemy =
            CombatTestFactory.validCombatActorDomain(
                stats =
                    CombatTestFactory.validCombatStatsDomain(
                        currentHp = 18000,
                        maxHp = 18000,
                    ),
            ),
        user =
            CombatTestFactory
                .validCombatActorDomain(
                    stats =
                        CombatTestFactory.validCombatStatsDomain(
                            currentHp = 18000,
                            maxHp = 18000,
                        ),
                ).copy(
                    activeStatus =
                        listOf(
                            CombatActiveStatusDomain(
                                stateId = 1L,
                                name = "Poisoned",
                                description = "Receives Damage After Each Turn",
                                remainingTurns = 1,
                            ),
                            CombatActiveStatusDomain(
                                stateId = 2L,
                                name = "Burning",
                                description = "Burning damage on each turn",
                                remainingTurns = 3,
                            ),
                            CombatActiveStatusDomain(
                                stateId = 3L,
                                name = "Frozen",
                                description = "Cannot act this turn",
                                remainingTurns = 1,
                            ),
                        ),
                ),
    )
