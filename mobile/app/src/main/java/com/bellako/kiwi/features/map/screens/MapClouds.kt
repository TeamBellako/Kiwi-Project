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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import kotlin.random.Random

// Atmospheric cloud sprites drifting across the map. Anchored to the map (the
// caller wraps this in a graphicsLayer matching the map's pan/zoom), rendered
// above the nodes so they read as overhead sky. The top-level MapMist still
// covers everything in fog-of-war regions.

private const val CLOUD_COUNT = 16
private const val CLOUD_LAYOUT_SEED = 0xC10D5L

// One entry per drawable in the sprite list below. Each Cloud is assigned a
// variant index at init via the layout seed, which maps 1:1 to a Painter.
private const val CLOUD_SPRITE_COUNT = 3

@Suppress("MagicNumber")
private const val CLOUD_MIN_SCALE = 0.4f

@Suppress("MagicNumber")
private const val CLOUD_MAX_SCALE = 0.9f

private const val CLOUD_MIN_ALPHA = 1f

private const val CLOUD_MAX_ALPHA = 1f

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

// Phase staggers initial X so clouds don't enter the viewport in lockstep.
@Suppress("MagicNumber")
private const val PHASE_MAX_SECONDS = 60f

@Suppress("MagicNumber")
private const val NANOS_PER_SECOND = 1_000_000_000f

private data class Cloud(
    val baseYFraction: Float,
    val scale: Float,
    val alpha: Float,
    val speedDpPerSec: Float,
    val phaseSeconds: Float,
    val variant: Int,
)

@Composable
fun MapClouds(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val clouds = remember { buildClouds() }
    val baseWidthPx = with(density) { CLOUD_BASE_WIDTH_DP.toPx() }
    val painters: List<Painter> =
        listOf(
            painterResource(R.drawable.map_cloud_a),
            painterResource(R.drawable.map_cloud_b),
            painterResource(R.drawable.map_cloud_c),
        )

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
            drawCloud(cloud, x, y, cloudW, painters)
        }
    }
}

private fun DrawScope.drawCloud(
    cloud: Cloud,
    screenX: Float,
    screenY: Float,
    cloudW: Float,
    painters: List<Painter>,
) {
    val painter = painters[cloud.variant]
    val intrinsic = painter.intrinsicSize
    val cloudH = cloudW * (intrinsic.height / intrinsic.width)
    translate(left = screenX, top = screenY - cloudH / 2f) {
        with(painter) {
            draw(size = Size(cloudW, cloudH), alpha = cloud.alpha)
        }
    }
}

private fun buildClouds(): List<Cloud> {
    val rng = Random(CLOUD_LAYOUT_SEED)
    val bandHeight = (CLOUD_Y_BOTTOM_FRACTION - CLOUD_Y_TOP_FRACTION) / CLOUD_COUNT
    return List(CLOUD_COUNT) { i ->
        val bandStart = CLOUD_Y_TOP_FRACTION + i * bandHeight
        Cloud(
            baseYFraction = bandStart + rng.nextFloat() * bandHeight,
            scale = lerpFloat(CLOUD_MIN_SCALE, CLOUD_MAX_SCALE, rng.nextFloat()),
            alpha = lerpFloat(CLOUD_MIN_ALPHA, CLOUD_MAX_ALPHA, rng.nextFloat()),
            speedDpPerSec = lerpFloat(CLOUD_MIN_SPEED_DP_S, CLOUD_MAX_SPEED_DP_S, rng.nextFloat()),
            phaseSeconds = rng.nextFloat() * PHASE_MAX_SECONDS,
            variant = rng.nextInt(CLOUD_SPRITE_COUNT),
        )
    }
}

private fun lerpFloat(
    a: Float,
    b: Float,
    t: Float,
): Float = a + (b - a) * t
