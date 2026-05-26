package com.bellako.kiwi.features.combat.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
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
import com.bellako.kiwi.features.combat.components.CombatBarkBubble
import com.bellako.kiwi.features.combat.components.CombatBattleArea
import com.bellako.kiwi.features.combat.components.CombatEnemySprite
import com.bellako.kiwi.features.combat.components.CombatHeader
import com.bellako.kiwi.features.combat.components.CombatTurnIndicator
import com.bellako.kiwi.features.combat.components.DeathSequenceOverlay
import com.bellako.kiwi.features.combat.components.CombatLogOverlay
import com.bellako.kiwi.features.combat.components.CombatIntroController
import com.bellako.kiwi.features.combat.components.FocusTarget
import com.bellako.kiwi.features.combat.components.PlayerControls
import com.bellako.kiwi.features.combat.components.PlayerDamageOverlays
import com.bellako.kiwi.features.combat.components.buildCombatLogEntries
import com.bellako.kiwi.features.combat.components.combatTurnGlowColor
import com.bellako.kiwi.features.combat.components.rememberCombatIntroController
import com.bellako.kiwi.features.combat.components.rememberDeathSequenceVfx
import com.bellako.kiwi.features.combat.components.rememberFocusBlurVfx
import com.bellako.kiwi.features.combat.components.rememberPlayerDamageVfx
import com.bellako.kiwi.features.combat.components.userTurnMessage
import com.bellako.kiwi.features.combat.data.ActiveBarkDomain
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.data.CombatGeneralStatus
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import com.bellako.kiwi.features.nodes.screens.LocalNodeEntryTransition
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.model.MAX_DECK_SLOTS
import kotlinx.coroutines.launch
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
private const val BARK_FADE_MS = 250
private const val BARK_VERTICAL_BIAS = 0.25f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongParameterList")
fun CombatScreen(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    isTurnPlaying: Boolean = false,
    activeBark: ActiveBarkDomain? = null,
    onBarkDismiss: () -> Unit = {},
    onConfirmAbandon: () -> Unit = {},
    onSkillClick: (skillId: Long, skillName: String) -> Unit = { _, _ -> },
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit = { _, _, _ -> },
) {
    val colors = LocalKiwiColors.current
    val context = LocalContext.current
    var isLogOpen by rememberSaveable(combat.id) { mutableStateOf(false) }
    var selectedStatus by remember(combat.id) { mutableStateOf<CombatActiveStatusDomain?>(null) }
    var showAbandonConfirm by rememberSaveable(combat.id) { mutableStateOf(false) }

    val intro = rememberCombatIntroController()
    // Withhold any bark until the intro is fully over — barks pulling focus
    // while the HUD is still introing makes the sequence read as cluttered.
    val displayedBark = if (intro.isCompleted) activeBark else null
    val isOverlayOpen =
        isLogOpen || selectedStatus != null || isTurnPlaying || displayedBark != null

    val enemyName = combat.enemyName.ifBlank { DEFAULT_ENEMY_NAME }
    val turnMessage = currentTurnMessage(combat, enemyName)
    // The player can act when combat is live and no turn animation is mid-flight.
    val isUserTurn = combat.combatStatus == CombatGeneralStatus.ONGOING && !isTurnPlaying
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
    val deathCloseProgress = rememberDeathSequenceVfx(combat.user.stats.currentHp == 0, combat.id)
    // Ambient depth-of-field pulses, plus a cinematic focus-on-enemy pull when
    // a bark is on screen. Paused during intro and after either side hits 0 HP.
    val isAlive = combat.user.stats.currentHp > 0 && combat.enemy.stats.currentHp > 0
    val focusBlur = rememberFocusBlurVfx(
        key = combat.id,
        enabled = intro.isCompleted && isAlive,
    )
    LaunchedEffect(displayedBark?.triggerId) {
        focusBlur.overrideTarget.value =
            if (displayedBark != null) FocusTarget.ENEMY else null
    }
    val nodeEntry = LocalNodeEntryTransition.current
    val nodeEntryScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        intro.play(
            skillSlotCount = MAX_DECK_SLOTS,
            onBackgroundShown = {
                // Background is now opaque, so the node-entry veil can lift
                // without the player glimpsing the map underneath.
                nodeEntry?.let { controller ->
                    nodeEntryScope.launch { controller.fadeOut() }
                }
            },
        )
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(colors.color2),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Only the background shakes on player damage — the enemy sprite and
            // the HUD stay put so it doesn't read as a full-camera shake.
            CombatBackground(
                background = combat.background,
                alpha = intro.backgroundAlpha,
                shakeOffsetX = { playerVfx.shakeOffsetX.value },
                blurRadiusDp = {
                    (focusBlur.backgroundBlurRadius.value +
                        focusBlur.backgroundOscillation.value)
                        .coerceAtLeast(0f)
                },
            )

            CombatEnemySprite(
                enemySprite = combat.enemySprite,
                currentHp = combat.enemy.stats.currentHp,
                isEnemyDefeated = combat.combatStatus == CombatGeneralStatus.USER_WON,
                context = context,
                introAlpha = intro.enemyAlpha,
                blurRadiusDp = { focusBlur.enemyBlurRadius.value },
            )

            Column(modifier = Modifier.fillMaxSize().padding(top = Spacing.large)) {
                CombatHeader(
                    title = "Ongoing Combat",
                    onClose = { showAbandonConfirm = true },
                )

                CombatBattleArea(
                    combat = combat,
                    enemyBarRevealProgress = intro.enemyHealthMaskProgress,
                    enemyBarNumbersAlpha = intro.enemyHealthNumbersAlpha,
                    timerIntroProgress = intro.timerSlideProgress,
                )

                CombatBottomPanel(
                    combat = combat,
                    deckSkills = deckSkills,
                    turnMessage = turnMessage,
                    isUserTurn = isUserTurn,
                    isLogOpen = isLogOpen,
                    isOverlayOpen = isOverlayOpen,
                    selectedStatus = selectedStatus,
                    intro = intro,
                    onToggleLog = { isLogOpen = !isLogOpen },
                    onSkillClick = onSkillClick,
                    onApplyGoalProgress = onApplyGoalProgress,
                    onStatusClick = { status ->
                        selectedStatus = if (selectedStatus?.stateId == status.stateId) null else status
                    },
                    onDismissPopup = { selectedStatus = null },
                )
            }
        }

        Crossfade(
            targetState = displayedBark,
            animationSpec = tween(BARK_FADE_MS),
            label = "combat_bark",
        ) { bark ->
            if (bark != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = BiasAlignment(0f, BARK_VERTICAL_BIAS),
                ) {
                    CombatBarkBubble(
                        bark = bark,
                        onDismiss = onBarkDismiss,
                    )
                }
            }
        }

        PlayerDamageOverlays(playerVfx)
        DeathSequenceOverlay(deathCloseProgress.value)

        CombatLogOverlay(
            isOpen = isLogOpen,
            entries = logEntries,
            onDismiss = { isLogOpen = false },
        )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Suppress("LongParameterList")
private fun CombatBottomPanel(
    combat: CombatDomain,
    deckSkills: List<SkillDomain>,
    turnMessage: AnnotatedString,
    isUserTurn: Boolean,
    isLogOpen: Boolean,
    isOverlayOpen: Boolean,
    selectedStatus: CombatActiveStatusDomain?,
    intro: CombatIntroController,
    onToggleLog: () -> Unit,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onStatusClick: (CombatActiveStatusDomain) -> Unit,
    onDismissPopup: () -> Unit,
) {
    val colors = LocalKiwiColors.current
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
            onClick = onToggleLog,
            glowColor = combatTurnGlowColor(combat.log.lastOrNull()),
            introAlpha = intro.turnIndicatorAlpha,
            dimmed = isOverlayOpen,
            isUserTurn = isUserTurn,
        )

        Kiwi_Spacer(Spacing.medium)

        PlayerControls(
            deckSkills = deckSkills,
            userActor = combat.user,
            isOverlayOpen = isOverlayOpen,
            selectedStatus = selectedStatus,
            onSkillClick = onSkillClick,
            onApplyGoalProgress = onApplyGoalProgress,
            onStatusClick = onStatusClick,
            onDismissPopup = onDismissPopup,
            skillSlotIntroScale = { slotIndex -> intro.skillSlotScale(slotIndex) },
            skillSlotIntroAlpha = { slotIndex -> intro.skillSlotAlpha(slotIndex) },
            playerBarRevealProgress = intro.playerHealthMaskProgress,
            playerBarNumbersAlpha = intro.playerHealthNumbersAlpha,
        )

        Kiwi_Spacer(Spacing.large)
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
        withStyle(SpanStyle(color = colors.color6, fontStyle = FontStyle.Italic)) { append(" used ") }
        withStyle(SpanStyle(color = colors.color8A, fontStyle = FontStyle.Italic)) {
            append(skillName)
        }
        withStyle(SpanStyle(color = colors.color6, fontStyle = FontStyle.Italic)) { append("!") }
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
        enemyName = "Flicker",
        enemySprite = "enemy_flicker_base",
        background = "background_mindveil",
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
