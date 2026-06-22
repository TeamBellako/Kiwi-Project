package com.bellako.kiwi.features.map.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import kotlinx.coroutines.delay
import kotlin.random.Random

// The three reversed foam frames. Each is authored at the same dimensions as
// the map image (4000 x 3796), so drawn at the map's transform they register
// pixel-for-pixel with the rivers. Cycled at random so the foam reads as
// shifting rather than a static texture.
private val FOAM_FRAMES =
    listOf(
        R.drawable.mindveil_rivers_01,
        R.drawable.mindveil_rivers_02,
        R.drawable.mindveil_rivers_03,
    )

// How long a frame holds before the next swap. Randomised within this range so
// the rhythm never feels mechanical. Lower both to speed the foam up, raise
// them to slow it down.
private const val FOAM_HOLD_MIN_MS = 1_000L
private const val FOAM_HOLD_MAX_MS = 2_000L

private fun nextFoamIndex(current: Int): Int =
    if (FOAM_FRAMES.size <= 1) {
        0
    } else {
        // Offset by 1..size-1 so the new frame is never the current one.
        (current + 1 + Random.nextInt(FOAM_FRAMES.size - 1)) % FOAM_FRAMES.size
    }

/**
 * Animated river-foam overlay for the map. Hard-pops between random [FOAM_FRAMES]
 * on a randomised beat — mimicking foam shifting along the rivers. Each frame is
 * drawn at the map's exact transform (no extra scale or offset), so the foam
 * stays registered with the rivers underneath. The loop pauses while
 * [LocalMapVfxEnabled] is false (e.g. a full-screen conversation/combat is
 * covering the map).
 *
 * Expected to be placed inside the map's transformed Box (so it pans/zooms with
 * the map) and below the node connections (so nodes stay visible on top).
 */
@Composable
fun MapWaterFoam(modifier: Modifier = Modifier) {
    val vfxEnabled = LocalMapVfxEnabled.current

    var frameIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(vfxEnabled) {
        if (!vfxEnabled) return@LaunchedEffect
        while (true) {
            delay(Random.nextLong(FOAM_HOLD_MIN_MS, FOAM_HOLD_MAX_MS))
            frameIndex = nextFoamIndex(frameIndex)
        }
    }

    // fillMaxSize + FillBounds (via the passed-in modifier) makes the frame
    // cover exactly the same rect as the map image in this transformed Box, so
    // the two line up. The swap is an instant pop — no fade.
    Kiwi_Image(
        painterResourceId = FOAM_FRAMES[frameIndex],
        alt = "River foam",
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
    )
}
