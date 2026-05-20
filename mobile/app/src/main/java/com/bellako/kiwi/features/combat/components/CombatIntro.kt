package com.bellako.kiwi.features.combat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

// Per-stage timings for the combat intro. Tweak here to adjust pacing.
private const val BG_FADE_MS = 1_100
private const val ENEMY_FADE_MS = 1_100
private const val HEALTH_MASK_MS = 700
private const val HEALTH_NUMBERS_FADE_MS = 500
private const val TIMER_SLIDE_MS = 700
private const val SKILL_POP_MS = 550
private const val SKILL_STAGGER_MS = 220
private const val TURN_INDICATOR_FADE_MS = 550

// Minimum scale a deck slot keeps before its pop starts. Kept above zero so
// the slot retains positive boundsInWindow — UI tests that assert
// `isDisplayed` aren't blocked by a scale-0 graphicsLayer transform. The slot
// is faded with `skillSlotAlpha` separately, so this tiny base scale is
// visually invisible at intro-start.
private const val SKILL_SLOT_MIN_SCALE = 0.5f

// Breath between stages so the sequence reads as a series of beats rather than
// one continuous slide.
private const val STAGE_GAP_MS = 350L
private const val SUB_STAGE_GAP_MS = 180L

/**
 * Centralised animation state for the combat intro. Each stage exposes a
 * `Float` (0f → 1f, or 0f → totalMs for the skills clock) that components read
 * to drive their own piece of the entrance. The intro runs sequentially in
 * [play], and individual stages can be skipped instantly by calling [snapToEnd].
 *
 * Defaults at construction time are 0f, so until [play] runs, nothing is
 * visible — that's how the screen starts blank under the node-entry white
 * veil. After [play] completes everything sits at 1f / max clock and reads as
 * a fully-presented combat layout.
 */
@Stable
class CombatIntroController internal constructor(
    private val backgroundAlphaAnim: Animatable<Float, AnimationVector1D>,
    private val enemyAlphaAnim: Animatable<Float, AnimationVector1D>,
    private val enemyHealthMaskAnim: Animatable<Float, AnimationVector1D>,
    private val enemyHealthNumbersAnim: Animatable<Float, AnimationVector1D>,
    private val timerSlideAnim: Animatable<Float, AnimationVector1D>,
    private val skillClockAnim: Animatable<Float, AnimationVector1D>,
    private val playerHealthMaskAnim: Animatable<Float, AnimationVector1D>,
    private val playerHealthNumbersAnim: Animatable<Float, AnimationVector1D>,
    private val turnIndicatorAnim: Animatable<Float, AnimationVector1D>,
) {
    val backgroundAlpha: Float get() = backgroundAlphaAnim.value
    val enemyAlpha: Float get() = enemyAlphaAnim.value
    val enemyHealthMaskProgress: Float get() = enemyHealthMaskAnim.value
    val enemyHealthNumbersAlpha: Float get() = enemyHealthNumbersAnim.value
    val timerSlideProgress: Float get() = timerSlideAnim.value
    val skillClockMs: Float get() = skillClockAnim.value
    val playerHealthMaskProgress: Float get() = playerHealthMaskAnim.value
    val playerHealthNumbersAlpha: Float get() = playerHealthNumbersAnim.value
    val turnIndicatorAlpha: Float get() = turnIndicatorAnim.value

    /** True once [play] has settled. Use to gate UI that should only appear
     * after the intro is over (e.g. enemy barks). */
    var isCompleted: Boolean by mutableStateOf(false)
        private set

    /**
     * Plays the full intro sequence. Suspends until everything has settled.
     * [onBackgroundShown] fires the moment the background fade-in completes —
     * that's when the caller can safely dismiss any cover (e.g. the node-entry
     * white veil) without the player glimpsing what's behind.
     */
    suspend fun play(
        skillSlotCount: Int,
        onBackgroundShown: () -> Unit = {},
    ) {
        isCompleted = false
        backgroundAlphaAnim.animateTo(1f, tween(BG_FADE_MS, easing = LinearEasing))
        onBackgroundShown()
        delay(STAGE_GAP_MS)

        enemyAlphaAnim.animateTo(1f, tween(ENEMY_FADE_MS, easing = LinearEasing))
        delay(STAGE_GAP_MS)

        enemyHealthMaskAnim.animateTo(1f, tween(HEALTH_MASK_MS, easing = FastOutSlowInEasing))
        delay(SUB_STAGE_GAP_MS)
        enemyHealthNumbersAnim.animateTo(1f, tween(HEALTH_NUMBERS_FADE_MS, easing = LinearEasing))
        delay(STAGE_GAP_MS)

        timerSlideAnim.animateTo(1f, tween(TIMER_SLIDE_MS, easing = FastOutSlowInEasing))
        delay(STAGE_GAP_MS)

        val skillTotalMs = skillStaggerTotalMs(skillSlotCount)
        skillClockAnim.animateTo(
            targetValue = skillTotalMs.toFloat(),
            animationSpec = tween(skillTotalMs, easing = LinearEasing),
        )
        delay(STAGE_GAP_MS)

        playerHealthMaskAnim.animateTo(1f, tween(HEALTH_MASK_MS, easing = FastOutSlowInEasing))
        delay(SUB_STAGE_GAP_MS)
        playerHealthNumbersAnim.animateTo(1f, tween(HEALTH_NUMBERS_FADE_MS, easing = LinearEasing))
        delay(STAGE_GAP_MS)

        turnIndicatorAnim.animateTo(1f, tween(TURN_INDICATOR_FADE_MS, easing = LinearEasing))
        isCompleted = true
    }

    /** Snaps every stage to its final value, e.g. when previewing without animation. */
    suspend fun snapToEnd(skillSlotCount: Int) {
        backgroundAlphaAnim.snapTo(1f)
        enemyAlphaAnim.snapTo(1f)
        enemyHealthMaskAnim.snapTo(1f)
        enemyHealthNumbersAnim.snapTo(1f)
        timerSlideAnim.snapTo(1f)
        skillClockAnim.snapTo(skillStaggerTotalMs(skillSlotCount).toFloat())
        playerHealthMaskAnim.snapTo(1f)
        playerHealthNumbersAnim.snapTo(1f)
        turnIndicatorAnim.snapTo(1f)
        isCompleted = true
    }

    /**
     * Pop scale for a deck slot (0-indexed), driven by [skillClockMs]. Stays
     * at [SKILL_SLOT_MIN_SCALE] until the slot's stagger kicks in so the
     * layout bounds are never collapsed to a point — pair with [skillSlotAlpha]
     * for the actual visibility.
     */
    fun skillSlotScale(slotIndex: Int): Float {
        val itemStart = slotIndex * SKILL_STAGGER_MS
        val rawP = ((skillClockMs - itemStart) / SKILL_POP_MS).coerceIn(0f, 1f)
        val curve = if (rawP <= 0f) 0f else EaseOutBack.transform(rawP).coerceAtLeast(0f)
        return SKILL_SLOT_MIN_SCALE + (1f - SKILL_SLOT_MIN_SCALE) * curve
    }

    /** Fade-in alpha for a deck slot (0-indexed), in lockstep with [skillSlotScale]. */
    fun skillSlotAlpha(slotIndex: Int): Float {
        val itemStart = slotIndex * SKILL_STAGGER_MS
        return ((skillClockMs - itemStart) / SKILL_POP_MS).coerceIn(0f, 1f)
    }
}

@Composable
fun rememberCombatIntroController(): CombatIntroController {
    val bg = remember { Animatable(0f) }
    val enemy = remember { Animatable(0f) }
    val enemyMask = remember { Animatable(0f) }
    val enemyNums = remember { Animatable(0f) }
    val timer = remember { Animatable(0f) }
    val skills = remember { Animatable(0f) }
    val playerMask = remember { Animatable(0f) }
    val playerNums = remember { Animatable(0f) }
    val turn = remember { Animatable(0f) }
    return remember {
        CombatIntroController(
            backgroundAlphaAnim = bg,
            enemyAlphaAnim = enemy,
            enemyHealthMaskAnim = enemyMask,
            enemyHealthNumbersAnim = enemyNums,
            timerSlideAnim = timer,
            skillClockAnim = skills,
            playerHealthMaskAnim = playerMask,
            playerHealthNumbersAnim = playerNums,
            turnIndicatorAnim = turn,
        )
    }
}

private fun skillStaggerTotalMs(slotCount: Int): Int {
    if (slotCount <= 0) return SKILL_POP_MS
    return SKILL_POP_MS + (slotCount - 1) * SKILL_STAGGER_MS
}
