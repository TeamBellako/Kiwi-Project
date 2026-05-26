package com.bellako.kiwi.features.map.screens

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// Screen-fixed atmospheric clouds drifting across the map. Sits above the map
// image and node connections but below the interactive nodes, so it never
// obscures gameplay. Procedural for now (overlapping translucent circles) —
// `drawCloud` is the single swap point when artist sprites arrive.

private const val CLOUD_COUNT = 8
private const val CLOUD_LAYOUT_SEED = 0xC10D5L

@Suppress("MagicNumber")
private const val CLOUD_MIN_SCALE = 0.4f

@Suppress("MagicNumber")
private const val CLOUD_MAX_SCALE = 0.9f

@Suppress("MagicNumber")
private const val CLOUD_MIN_ALPHA = 0.35f

@Suppress("MagicNumber")
private const val CLOUD_MAX_ALPHA = 0.70f

@Suppress("MagicNumber")
private const val CLOUD_MIN_SPEED_DP_S = 2f

@Suppress("MagicNumber")
private const val CLOUD_MAX_SPEED_DP_S = 7f

private val CLOUD_BASE_WIDTH_DP = 180.dp

// Vertical band the clouds live in, as fractions of the cloud canvas height.
// The canvas is sized to the full map image, so this spans the entire map
// world — clouds are visible wherever the player pans/zooms.
@Suppress("MagicNumber")
private const val CLOUD_Y_TOP_FRACTION = 0.02f

@Suppress("MagicNumber")
private const val CLOUD_Y_BOTTOM_FRACTION = 0.98f

// Puff layout — each cloud is built from a few overlapping circles. Offsets
// and radii are expressed as fractions of the cloud's pre-scale width.
private const val PUFFS_PER_CLOUD = 5

@Suppress("MagicNumber")
private const val PUFF_MIN_RADIUS_FRACTION = 0.22f

@Suppress("MagicNumber")
private const val PUFF_MAX_RADIUS_FRACTION = 0.34f

@Suppress("MagicNumber")
private const val PUFF_X_SPREAD_FRACTION = 0.35f

@Suppress("MagicNumber")
private const val PUFF_Y_SPREAD_FRACTION = 0.10f

// Fraction of `step` that horizontal jitter is allowed to wander; keeps puffs
// roughly evenly spaced while still varying their exact positions.
@Suppress("MagicNumber")
private const val PUFF_X_JITTER_FRACTION = 0.5f

// Center-to-edge spread doubled = full spread; used as both step normalization
// (puffs span [-spread, +spread]) and the y-jitter range.
private const val SPREAD_TO_STEP_RATIO = 2f

// Recenter Random.nextFloat() (range 0..1) around zero so jitter is signed.
private const val HALF = 0.5f

@Suppress("MagicNumber")
private const val NANOS_PER_SECOND = 1_000_000_000f

private data class Cloud(
    val baseYFraction: Float,
    val scale: Float,
    val alpha: Float,
    val speedDpPerSec: Float,
    val phaseSeconds: Float,
    val widthDp: Float,
    val puffOffsets: List<Offset>,
    val puffRadii: List<Float>,
)

@Composable
fun MapClouds(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val clouds = remember { buildClouds() }
    val baseWidthPx = with(density) { CLOUD_BASE_WIDTH_DP.toPx() }

    var elapsedSeconds by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                if (lastNanos != 0L) {
                    elapsedSeconds += (nanos - lastNanos) / NANOS_PER_SECOND
                }
                lastNanos = nanos
            }
        }
    }

    Canvas(modifier = modifier) {
        val viewportW = size.width
        val viewportH = size.height
        if (viewportW <= 0f || viewportH <= 0f) return@Canvas

        clouds.forEach { cloud ->
            val cloudW = baseWidthPx * cloud.scale
            val speedPxPerSec = cloud.speedDpPerSec * density.density
            val travelDistance = viewportW + cloudW
            val phasePx = cloud.phaseSeconds * speedPxPerSec
            val raw = (phasePx + speedPxPerSec * elapsedSeconds) % travelDistance
            val wrapped = if (raw < 0f) raw + travelDistance else raw
            val x = wrapped - cloudW
            val y = cloud.baseYFraction * viewportH
            drawCloud(cloud, x, y, baseWidthPx)
        }
    }
}

// Single swap point for sprite-based rendering. Replace the body with a
// `drawImage(painter)` call once artist sprites arrive — the per-cloud state,
// motion math, and `MapClouds` composable above stay untouched.
private fun DrawScope.drawCloud(
    cloud: Cloud,
    screenX: Float,
    screenY: Float,
    baseWidthPx: Float,
) {
    val cloudW = baseWidthPx * cloud.scale
    val centerX = screenX + cloudW / 2f
    val centerY = screenY
    scale(scaleX = cloud.scale, scaleY = cloud.scale, pivot = Offset(centerX, centerY)) {
        cloud.puffOffsets.forEachIndexed { i, off ->
            drawCircle(
                color = Color.White.copy(alpha = cloud.alpha),
                radius = cloud.puffRadii[i] * baseWidthPx,
                center = Offset(centerX + off.x * baseWidthPx, centerY + off.y * baseWidthPx),
            )
        }
    }
}

private fun buildClouds(): List<Cloud> {
    val rng = Random(CLOUD_LAYOUT_SEED)
    val bandHeight = (CLOUD_Y_BOTTOM_FRACTION - CLOUD_Y_TOP_FRACTION) / CLOUD_COUNT
    return List(CLOUD_COUNT) { i ->
        val bandStart = CLOUD_Y_TOP_FRACTION + i * bandHeight
        val baseY = bandStart + rng.nextFloat() * bandHeight
        val scale = lerpFloat(CLOUD_MIN_SCALE, CLOUD_MAX_SCALE, rng.nextFloat())
        val alpha = lerpFloat(CLOUD_MIN_ALPHA, CLOUD_MAX_ALPHA, rng.nextFloat())
        val speed = lerpFloat(CLOUD_MIN_SPEED_DP_S, CLOUD_MAX_SPEED_DP_S, rng.nextFloat())
        // Phase staggers initial X so clouds don't enter the viewport in lockstep.
        @Suppress("MagicNumber")
        val phase = rng.nextFloat() * 60f
        val (offsets, radii) = buildPuffs(rng)
        Cloud(
            baseYFraction = baseY,
            scale = scale,
            alpha = alpha,
            speedDpPerSec = speed,
            phaseSeconds = phase,
            widthDp = CLOUD_BASE_WIDTH_DP.value,
            puffOffsets = offsets,
            puffRadii = radii,
        )
    }
}

private fun buildPuffs(rng: Random): Pair<List<Offset>, List<Float>> {
    val offsets = ArrayList<Offset>(PUFFS_PER_CLOUD)
    val radii = ArrayList<Float>(PUFFS_PER_CLOUD)
    // First puff is the central body — large and centered.
    offsets += Offset(0f, 0f)
    radii += PUFF_MAX_RADIUS_FRACTION
    // Remaining puffs scatter horizontally with small vertical jitter.
    val step = PUFF_X_SPREAD_FRACTION * SPREAD_TO_STEP_RATIO / (PUFFS_PER_CLOUD - 1)
    for (i in 1 until PUFFS_PER_CLOUD) {
        val baseX = -PUFF_X_SPREAD_FRACTION + step * i
        val jitterX = (rng.nextFloat() - HALF) * step * PUFF_X_JITTER_FRACTION
        val jitterY = (rng.nextFloat() - HALF) * PUFF_Y_SPREAD_FRACTION * SPREAD_TO_STEP_RATIO
        offsets += Offset(baseX + jitterX, jitterY)
        radii += lerpFloat(PUFF_MIN_RADIUS_FRACTION, PUFF_MAX_RADIUS_FRACTION, rng.nextFloat())
    }
    return offsets to radii
}

private fun lerpFloat(
    a: Float,
    b: Float,
    t: Float,
): Float = a + (b - a) * t
