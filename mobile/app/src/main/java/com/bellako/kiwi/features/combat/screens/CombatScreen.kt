package com.bellako.kiwi.features.combat.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.common.screens.components.Kiwi_Spacer
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.combat.components.CombatAbandonConfirmModal
import com.bellako.kiwi.features.combat.components.CombatDeck
import com.bellako.kiwi.features.combat.components.CombatHeader
import com.bellako.kiwi.features.combat.components.CombatHealthBar
import com.bellako.kiwi.features.combat.components.CombatLog
import com.bellako.kiwi.features.combat.components.CombatLogEntry
import com.bellako.kiwi.features.combat.components.CombatStatusPopup
import com.bellako.kiwi.features.combat.components.CombatStatusRow
import com.bellako.kiwi.features.combat.components.CombatTimer
import com.bellako.kiwi.features.combat.components.CombatTurnIndicator
import com.bellako.kiwi.features.combat.components.buildCombatLogEntries
import com.bellako.kiwi.features.combat.components.userTurnMessage
import com.bellako.kiwi.features.combat.data.CombatActionDomain
import com.bellako.kiwi.features.combat.data.CombatActionType
import com.bellako.kiwi.features.combat.data.CombatActiveStatusDomain
import com.bellako.kiwi.features.combat.data.CombatActor
import com.bellako.kiwi.features.combat.data.CombatActorDomain
import com.bellako.kiwi.features.combat.data.CombatDomain
import com.bellako.kiwi.features.combat.tests.CombatTestFactory
import com.bellako.kiwi.features.skills.data.SkillDomain
import com.bellako.kiwi.features.skills.tests.SkillsTestFactory
import com.bellako.kiwi.ui.KIWI_DISABLED_ALPHA
import com.bellako.kiwi.ui.KiwiColors
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val HEALTH_BAR_WIDTH_FRACTION = 0.6f
private const val PLAYER_HEALTH_BAR_WIDTH_FRACTION = 0.5f
private const val SPRITE_HEIGHT_FRACTION = 0.7f
private const val LOG_HEIGHT_FRACTION = 0.85f
private const val DAMAGE_WIGGLE_CYCLES = 4
private const val DAMAGE_WIGGLE_AMPLITUDE_PX = 22f
private const val DAMAGE_WIGGLE_STEP_MS = 50
private const val DAMAGE_FLASH_CYCLES = 3
private const val DAMAGE_FLASH_STEP_MS = 90L
private const val DAMAGE_FLASH_RED_ALPHA = 0.65f
private const val DAMAGE_FLASH_DIM_ALPHA = 0.4f
private const val PLAYER_SHAKE_CYCLES = 4
private const val PLAYER_SHAKE_AMPLITUDE_PX = 28f
private const val PLAYER_SHAKE_STEP_MS = 50
private const val PLAYER_FLASH_CYCLES = 2
private const val PLAYER_FLASH_STEP_MS = 90L
private const val PLAYER_FLASH_ALPHA = 0.15f
private const val PLAYER_VIGNETTE_PEAK_ALPHA = 0.3f
private const val PLAYER_VIGNETTE_RISE_MS = 120
private const val PLAYER_VIGNETTE_FALL_MS = 500
private const val LOG_DIM_ALPHA = 0.55f
private const val DEFAULT_ENEMY_NAME = "Enemy"
private const val BOTTOM_PANEL_GRADIENT_START = -0.2f
private const val BOTTOM_PANEL_GRADIENT_MID = 0.5f
private const val BOTTOM_PANEL_GRADIENT_END = 1f
private const val BACKGROUND_SATURATION = 0.45f
private const val EDGE_FADE_TOP_ALPHA = 0.75f
private const val EDGE_FADE_BOTTOM_ALPHA = 0.95f
private const val EDGE_FADE_TOP_END = 0.18f
private const val EDGE_FADE_BOTTOM_START = 0.55f
private val STATUS_POPUP_BOTTOM_OFFSET = 44.dp

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
private fun PlayerControls(
    deckSkills: List<SkillDomain>,
    userActor: CombatActorDomain,
    isOverlayOpen: Boolean,
    selectedStatus: CombatActiveStatusDomain?,
    onSkillClick: (skillId: Long, skillName: String) -> Unit,
    onApplyGoalProgress: (skillId: Long, goalId: Long, newProgress: Int) -> Unit,
    onStatusClick: (CombatActiveStatusDomain) -> Unit,
    onDismissPopup: () -> Unit,
) {
    val colors = LocalKiwiColors.current
    val dimAlpha = if (isOverlayOpen) KIWI_DISABLED_ALPHA else 1f

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CombatDeck(
                deckSkills = deckSkills,
                isDisabled = isOverlayOpen,
                onSkillClick = onSkillClick,
                onApplyGoalProgress = onApplyGoalProgress,
                modifier = Modifier.alpha(dimAlpha),
            )

            Kiwi_Spacer(Spacing.small)

            CombatHealthBar(
                currentHp = userActor.stats.currentHp,
                maxHp = userActor.stats.maxHp,
                fillTint = colors.color7C,
                label = "Player HP",
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(PLAYER_HEALTH_BAR_WIDTH_FRACTION)
                        .alpha(dimAlpha),
            )

            Kiwi_Spacer(Spacing.small)

            CombatStatusRow(
                statuses = userActor.activeStatus,
                selectedStatusId = selectedStatus?.stateId,
                onStatusClick = onStatusClick,
            )
        }

        if (selectedStatus != null) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onDismissPopup,
                        ),
            )

            CombatStatusPopup(
                status = selectedStatus,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = getResponsiveSizeHeight(STATUS_POPUP_BOTTOM_OFFSET)),
            )
        }
    }
}

@Composable
private fun EnemyArena(
    enemySprite: String,
    currentHp: Int,
    maxHp: Int,
    endsAt: Long?,
    context: Context,
) {
    var previousHp by remember { mutableIntStateOf(currentHp) }
    var damageTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentHp) {
        if (currentHp < previousHp) damageTrigger++
        previousHp = currentHp
    }

    val offsetX = remember { Animatable(0f) }
    val redAlpha = remember { Animatable(0f) }
    val spriteAlpha = remember { Animatable(1f) }

    LaunchedEffect(damageTrigger) {
        if (damageTrigger == 0) return@LaunchedEffect
        coroutineScope {
            launch {
                repeat(DAMAGE_WIGGLE_CYCLES) {
                    offsetX.animateTo(DAMAGE_WIGGLE_AMPLITUDE_PX, tween(DAMAGE_WIGGLE_STEP_MS))
                    offsetX.animateTo(-DAMAGE_WIGGLE_AMPLITUDE_PX, tween(DAMAGE_WIGGLE_STEP_MS))
                }
                offsetX.animateTo(0f, tween(DAMAGE_WIGGLE_STEP_MS))
            }
            launch {
                repeat(DAMAGE_FLASH_CYCLES) {
                    redAlpha.snapTo(DAMAGE_FLASH_RED_ALPHA)
                    spriteAlpha.snapTo(DAMAGE_FLASH_DIM_ALPHA)
                    delay(DAMAGE_FLASH_STEP_MS)
                    redAlpha.snapTo(0f)
                    spriteAlpha.snapTo(1f)
                    delay(DAMAGE_FLASH_STEP_MS)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Kiwi_Image(
            painterResourceId = resolveEnemySprite(enemySprite, context),
            alt = "Enemy sprite",
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxHeight(SPRITE_HEIGHT_FRACTION)
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .alpha(spriteAlpha.value),
            contentScale = ContentScale.Fit,
            colorFilter =
                if (redAlpha.value > 0f) {
                    ColorFilter.tint(
                        Color.Red.copy(alpha = redAlpha.value),
                        BlendMode.SrcAtop,
                    )
                } else {
                    null
                },
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = getResponsiveSizeHeight(Spacing.small)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(getResponsiveSizeHeight(Spacing.xSmall)),
        ) {
            CombatHealthBar(
                currentHp = currentHp,
                maxHp = maxHp,
                modifier = Modifier.fillMaxWidth(HEALTH_BAR_WIDTH_FRACTION),
            )

            CombatTimer(endsAt = endsAt)
        }
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

@Composable
private fun LogDimOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .background(Color.Black.copy(alpha = LOG_DIM_ALPHA))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss,
                ),
    )
}

private class PlayerDamageVfx(
    val shakeOffsetX: Animatable<Float, *>,
    val flashAlpha: Animatable<Float, *>,
    val vignetteAlpha: Animatable<Float, *>,
)

@Composable
private fun rememberPlayerDamageVfx(
    currentHp: Int,
    key: Any,
): PlayerDamageVfx {
    val shakeOffsetX = remember(key) { Animatable(0f) }
    val flashAlpha = remember(key) { Animatable(0f) }
    val vignetteAlpha = remember(key) { Animatable(0f) }
    var previousHp by remember(key) { mutableIntStateOf(currentHp) }
    var trigger by remember(key) { mutableIntStateOf(0) }

    LaunchedEffect(currentHp) {
        if (currentHp < previousHp) trigger++
        previousHp = currentHp
    }

    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        coroutineScope {
            launch {
                repeat(PLAYER_SHAKE_CYCLES) {
                    shakeOffsetX.animateTo(PLAYER_SHAKE_AMPLITUDE_PX, tween(PLAYER_SHAKE_STEP_MS))
                    shakeOffsetX.animateTo(-PLAYER_SHAKE_AMPLITUDE_PX, tween(PLAYER_SHAKE_STEP_MS))
                }
                shakeOffsetX.animateTo(0f, tween(PLAYER_SHAKE_STEP_MS))
            }
            launch {
                repeat(PLAYER_FLASH_CYCLES) {
                    flashAlpha.snapTo(PLAYER_FLASH_ALPHA)
                    delay(PLAYER_FLASH_STEP_MS)
                    flashAlpha.snapTo(0f)
                    delay(PLAYER_FLASH_STEP_MS)
                }
            }
            launch {
                vignetteAlpha.animateTo(PLAYER_VIGNETTE_PEAK_ALPHA, tween(PLAYER_VIGNETTE_RISE_MS))
                vignetteAlpha.animateTo(0f, tween(PLAYER_VIGNETTE_FALL_MS))
            }
        }
    }
    return PlayerDamageVfx(shakeOffsetX, flashAlpha, vignetteAlpha)
}

@Composable
private fun PlayerDamageOverlays(vfx: PlayerDamageVfx) {
    if (vfx.flashAlpha.value > 0f) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Red.copy(alpha = vfx.flashAlpha.value)),
        )
    }
    if (vfx.vignetteAlpha.value > 0f) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    Color.Red.copy(alpha = 0f),
                                    Color.Red.copy(alpha = vfx.vignetteAlpha.value),
                                ),
                        ),
                    ),
        )
    }
}

@Composable
private fun CombatBackground(backgroundId: Long?) {
    val colors = LocalKiwiColors.current
    val resId = resolveBackground(backgroundId) ?: return
    Kiwi_Image(
        painterResourceId = resId,
        alt = "Combat background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        colorFilter =
            ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(BACKGROUND_SATURATION) },
            ),
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

@Composable
private fun ColumnScope.CombatBattleArea(
    combat: CombatDomain,
    isLogOpen: Boolean,
    onDismissLog: () -> Unit,
    logEntries: List<CombatLogEntry>,
    context: Context,
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth(),
    ) {
        EnemyArena(
            enemySprite = combat.enemySprite,
            currentHp = combat.enemy.stats.currentHp,
            maxHp = combat.enemy.stats.maxHp,
            endsAt = combat.endsAt,
            context = context,
        )

        if (isLogOpen) {
            LogDimOverlay(
                modifier = Modifier.fillMaxSize(),
                onDismiss = onDismissLog,
            )

            CombatLog(
                entries = logEntries,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            horizontal = getResponsiveSizeWidth(Spacing.medium),
                            vertical = getResponsiveSizeHeight(Spacing.small),
                        ).fillMaxHeight(LOG_HEIGHT_FRACTION)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {},
                        ),
            )
        }
    }
}

private fun resolveEnemySprite(
    spriteName: String,
    context: Context,
): Int {
    val resolved =
        context.resources.getIdentifier(
            spriteName,
            "drawable",
            context.packageName,
        )
    return if (resolved != 0) resolved else R.drawable.liria_neutral
}

private fun resolveBackground(backgroundId: Long?): Int? =
    when (backgroundId) {
        1L -> R.drawable.background_mindveil
        else -> null
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
