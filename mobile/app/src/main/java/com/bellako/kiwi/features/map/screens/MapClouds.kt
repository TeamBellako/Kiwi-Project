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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodesDomain
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// Atmospheric cloud sprites layered above the map. Two fleets:
//
//   (a) STATIONARY COVER — a dense grid of clouds that stay fixed at their
//       initial map position (only a very subtle wiggle), forming opaque
//       coverage across the whole map. The fade-near-revealed-geometry logic
//       carves them away inside the mist's punched holes, so the visual cloud
//       cover matches the mist's coverage.
//
//   (b) DRIFTING CLOUDS — a small number of clouds that travel horizontally
//       across the map. Where they pass through a revealed area they remain
//       visible (no mist hides them) until they approach a node icon, where
//       the same fade pulls their opacity down so they don't obscure the node.
//
// Anchored to the map (the caller wraps this in a graphicsLayer matching the
// map's pan/zoom). Sized to the full map image so coverage spans the whole
// world.

private const val CLOUD_LAYOUT_SEED = 0xC10D5L

// One entry per drawable in the sprite list below. Each Cloud is assigned a
// variant index at init via the layout seed, which maps 1:1 to a Painter.
private const val CLOUD_SPRITE_COUNT = 3

// Stationary cover — a COLS x ROWS grid of clouds with per-cell jitter for
// variety. Bump these if you see gaps; bigger maps may need denser grids.
private const val STATIONARY_GRID_COLS = 8
private const val STATIONARY_GRID_ROWS = 12

@Suppress("MagicNumber")
private const val STATIONARY_GRID_JITTER_FRACTION = 0.25f

// Drifting clouds — enough that one is usually in the player's view at the
// right Y to pass through a revealed area, but not so many they read as a
// constant procession.
private const val DRIFTING_CLOUD_COUNT = 16

// Stationary clouds are scaled bigger than drifting ones so the grid cells
// overlap and the cover reads as continuous.
@Suppress("MagicNumber")
private const val STATIONARY_MIN_SCALE = 1.0f

@Suppress("MagicNumber")
private const val STATIONARY_MAX_SCALE = 1.4f

@Suppress("MagicNumber")
private const val DRIFTING_MIN_SCALE = 0.6f

@Suppress("MagicNumber")
private const val DRIFTING_MAX_SCALE = 1.0f

// Base alpha range — final alpha is this multiplied by the per-frame fade
// factor (1 far from revealed geometry, 0 inside the mist's hole radius).
private const val CLOUD_MIN_ALPHA = 1f
private const val CLOUD_MAX_ALPHA = 1f

@Suppress("MagicNumber")
private const val CLOUD_MIN_SPEED_DP_S = 1f

@Suppress("MagicNumber")
private const val CLOUD_MAX_SPEED_DP_S = 4f

private val CLOUD_BASE_WIDTH_DP = 180.dp

// Per-axis sinusoidal wiggle so clouds breathe in place. Amplitude is per
// axis; frequency varies per cloud so they don't all bob in lockstep.
@Suppress("MagicNumber")
private val WIGGLE_AMPLITUDE_DP = 2.dp

@Suppress("MagicNumber")
private const val WIGGLE_FREQ_MIN_HZ = 0.025f

@Suppress("MagicNumber")
private const val WIGGLE_FREQ_MAX_HZ = 0.07f

// Vertical band the clouds live in, as fractions of the cloud canvas height.
@Suppress("MagicNumber")
private const val CLOUD_Y_TOP_FRACTION = 0.02f

@Suppress("MagicNumber")
private const val CLOUD_Y_BOTTOM_FRACTION = 0.98f

// Phase staggers initial X so clouds don't enter the viewport in lockstep.
@Suppress("MagicNumber")
private const val PHASE_MAX_SECONDS = 60f

// Hole-fade tuning — must roughly track MapMist's hole radius so clouds clear
// out where the mist already has punched a hole around a revealed node.
@Suppress("MagicNumber")
private val FADE_HOLE_BASE_RADIUS_DP = 50.dp

@Suppress("MagicNumber")
private val FADE_HOLE_ZOOM_RADIUS_DP = 22.dp

// Cloud fade zone for STATIONARY clouds, expressed as multiples of MapMist's
// hole radius. The inner factor sits *outside* the mist hole so there's a
// clear gap between the mist's punched hole and where the cloud cover starts
// — stationary clouds are 0 alpha within this radius, ramping up to full
// alpha at FADE_OUTER_FACTOR.
@Suppress("MagicNumber")
private const val FADE_INNER_FACTOR = 1.2f

@Suppress("MagicNumber")
private const val FADE_OUTER_FACTOR = 1.7f

// Drifting-cloud fade — measured against node *centers* only (not segments).
// Drifting clouds are allowed to pass directly over node icons: they only
// DIM (down to DRIFTING_MIN_ALPHA, never fully transparent) so the player
// can see them sweeping across. Inner/outer factors define where the cloud
// reaches min alpha (on the node) and returns to full alpha (away from it).
@Suppress("MagicNumber")
private const val DRIFTING_FADE_INNER_FACTOR = 0.3f

@Suppress("MagicNumber")
private const val DRIFTING_FADE_OUTER_FACTOR = 1.0f

// Minimum alpha for drifting clouds passing over a node — keeps them
// visible (so the user sees the drift) while keeping the icon readable.
@Suppress("MagicNumber")
private const val DRIFTING_MIN_ALPHA = 0.4f

// Zoom-dependent boost on the fade radii so that the cloud-free zone around
// nodes grows at high zoom (clouds more transparent around nodes when the
// player is focused on a single area) and shrinks at low zoom (cloud cover
// reads more uniformly opaque in the overview). Boost = 1.0 at scale = 1.0;
// linear in scale, clamped to [MIN, MAX] so very low / very high zoom don't
// blow the fade out of proportion.
@Suppress("MagicNumber")
private const val ZOOM_FADE_SLOPE = 0.5f

@Suppress("MagicNumber")
private const val ZOOM_FADE_MIN = 0.7f

@Suppress("MagicNumber")
private const val ZOOM_FADE_MAX = 2.0f

// Fraction of cloud width treated as the "solid visible body" — the fade
// distance check measures from the cloud's edge (center − this × cloudW)
// rather than its center, so big sprites (high zoom) don't visually touch
// nodes even when their centers are far. Sprite PNGs have soft alpha at
// the edges, so a fraction of the half-width captures the opaque core.
// (Only applied to STATIONARY clouds — drifting clouds use center distance
// so they're permitted to pass over node icons.)
@Suppress("MagicNumber")
private const val CLOUD_EDGE_FRACTION = 0.2f

@Suppress("MagicNumber")
private const val NANOS_PER_SECOND = 1_000_000_000f

private const val TWO_PI = (2.0 * PI).toFloat()

private const val MIN_SCALE_GUARD = 0.0001f

private data class Cloud(
    // Stationary clouds use baseXFraction; drifting clouds set it to 0 and
    // ignore it (their X is computed from speed * elapsed + phase).
    val baseXFraction: Float,
    val baseYFraction: Float,
    val scale: Float,
    val alpha: Float,
    // 0f means stationary (no horizontal drift); >0 means drifting.
    val speedDpPerSec: Float,
    val phaseSeconds: Float,
    val variant: Int,
    val wiggleFreqX: Float,
    val wiggleFreqY: Float,
    val wigglePhaseX: Float,
    val wigglePhaseY: Float,
)

private data class RevealedGeometry(
    val nodes: List<Offset>,
    val segments: List<Pair<Offset, Offset>>,
)

@Composable
fun MapClouds(
    nodes: Map<Long, NodesDomain>,
    mapState: MapState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val clouds = remember { buildClouds() }
    val baseWidthPx = with(density) { CLOUD_BASE_WIDTH_DP.toPx() }
    val wiggleAmpPx = with(density) { WIGGLE_AMPLITUDE_DP.toPx() }
    val holeBasePx = with(density) { FADE_HOLE_BASE_RADIUS_DP.toPx() }
    val holeZoomPx = with(density) { FADE_HOLE_ZOOM_RADIUS_DP.toPx() }
    val painters: List<Painter> =
        listOf(
            painterResource(R.drawable.map_cloud_a),
            painterResource(R.drawable.map_cloud_b),
            painterResource(R.drawable.map_cloud_c),
        )

    val geometry =
        remember(nodes, mapState.mapWidthPx, mapState.mapHeightPx) {
            computeRevealedGeometry(nodes, mapState.mapWidthPx, mapState.mapHeightPx)
        }

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

        val scale = mapState.scale.coerceAtLeast(MIN_SCALE_GUARD)
        // Hole radius is defined in screen pixels (matches MapMist). The
        // cloud canvas is in unscaled map space — divide by scale to compare.
        val canvasHoleRadius = holeBasePx / scale + holeZoomPx
        val zoomFadeBoost =
            (1f + (scale - 1f) * ZOOM_FADE_SLOPE).coerceIn(ZOOM_FADE_MIN, ZOOM_FADE_MAX)
        val canvasFadeInner = canvasHoleRadius * FADE_INNER_FACTOR * zoomFadeBoost
        val canvasFadeOuter = canvasHoleRadius * FADE_OUTER_FACTOR * zoomFadeBoost
        val driftingFadeInner = canvasHoleRadius * DRIFTING_FADE_INNER_FACTOR * zoomFadeBoost
        val driftingFadeOuter = canvasHoleRadius * DRIFTING_FADE_OUTER_FACTOR * zoomFadeBoost

        clouds.forEach { cloud ->
            val cloudW = baseWidthPx * cloud.scale
            val isDrifting = cloud.speedDpPerSec > 0f
            val driftX =
                if (!isDrifting) {
                    cloud.baseXFraction * viewportW - cloudW / 2f
                } else {
                    val speedPxPerSec = cloud.speedDpPerSec * density.density
                    val travelDistance = viewportW + cloudW
                    val phasePx = cloud.phaseSeconds * speedPxPerSec
                    val raw = (phasePx + speedPxPerSec * elapsedSeconds) % travelDistance
                    val wrapped = if (raw < 0f) raw + travelDistance else raw
                    wrapped - cloudW
                }

            val wiggleX =
                (sin(elapsedSeconds * cloud.wiggleFreqX * TWO_PI + cloud.wigglePhaseX) * wiggleAmpPx)
            val wiggleY =
                (sin(elapsedSeconds * cloud.wiggleFreqY * TWO_PI + cloud.wigglePhaseY) * wiggleAmpPx)

            val cloudCenterX = driftX + cloudW / 2f + wiggleX
            val cloudCenterY = cloud.baseYFraction * viewportH + wiggleY

            val fade =
                if (isDrifting) {
                    computeDriftingFade(
                        cloudCenterX,
                        cloudCenterY,
                        geometry,
                        driftingFadeInner,
                        driftingFadeOuter,
                    )
                } else {
                    val cloudRadius = cloudW * CLOUD_EDGE_FRACTION
                    computeFade(
                        cloudCenterX,
                        cloudCenterY,
                        cloudRadius,
                        geometry,
                        canvasFadeInner,
                        canvasFadeOuter,
                    )
                }
            val effectiveAlpha = cloud.alpha * fade
            if (effectiveAlpha <= 0f) return@forEach

            drawCloud(
                cloud = cloud,
                screenX = driftX + wiggleX,
                screenY = cloudCenterY,
                cloudW = cloudW,
                alpha = effectiveAlpha,
                painters = painters,
            )
        }
    }
}

private fun computeFade(
    px: Float,
    py: Float,
    cloudRadius: Float,
    geometry: RevealedGeometry,
    innerRadius: Float,
    outerRadius: Float,
): Float {
    if (geometry.nodes.isEmpty()) return 1f
    var nearest = Float.MAX_VALUE
    for (n in geometry.nodes) {
        val d = hypot(px - n.x, py - n.y)
        if (d < nearest) nearest = d
    }
    for ((a, b) in geometry.segments) {
        val d = pointToSegmentDistance(px, py, a, b)
        if (d < nearest) nearest = d
    }
    // Measure from the cloud's visible edge, not its center — keeps large
    // sprites at high zoom from visually touching nodes.
    val effective = (nearest - cloudRadius).coerceAtLeast(0f)
    return when {
        effective <= innerRadius -> 0f
        effective >= outerRadius -> 1f
        else -> (effective - innerRadius) / (outerRadius - innerRadius)
    }
}

// Fade applied to drifting clouds. Unlike the stationary fade, this only
// measures distance to revealed-node *centers* (not segments) and uses the
// cloud's CENTER (not its visible edge) — drifting clouds are allowed to
// physically pass over a node icon. The result floors at DRIFTING_MIN_ALPHA
// rather than 0, so the cloud stays visible while sweeping across; only its
// brightness dips so the node icon remains readable underneath.
private fun computeDriftingFade(
    px: Float,
    py: Float,
    geometry: RevealedGeometry,
    innerRadius: Float,
    outerRadius: Float,
): Float {
    if (geometry.nodes.isEmpty()) return 1f
    var nearest = Float.MAX_VALUE
    for (n in geometry.nodes) {
        val d = hypot(px - n.x, py - n.y)
        if (d < nearest) nearest = d
    }
    return when {
        nearest <= innerRadius -> DRIFTING_MIN_ALPHA
        nearest >= outerRadius -> 1f
        else -> {
            val t = (nearest - innerRadius) / (outerRadius - innerRadius)
            DRIFTING_MIN_ALPHA + (1f - DRIFTING_MIN_ALPHA) * t
        }
    }
}

private fun pointToSegmentDistance(
    px: Float,
    py: Float,
    a: Offset,
    b: Offset,
): Float {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val len2 = abx * abx + aby * aby
    if (len2 == 0f) return hypot(px - a.x, py - a.y)
    val t = (((px - a.x) * abx + (py - a.y) * aby) / len2).coerceIn(0f, 1f)
    val cx = a.x + t * abx
    val cy = a.y + t * aby
    return hypot(px - cx, py - cy)
}

private fun computeRevealedGeometry(
    nodes: Map<Long, NodesDomain>,
    mapWidthPx: Float,
    mapHeightPx: Float,
): RevealedGeometry {
    if (nodes.isEmpty() || mapWidthPx <= 0f || mapHeightPx <= 0f) {
        return RevealedGeometry(emptyList(), emptyList())
    }
    val revealedIds = computeRevealedIds(nodes)
    val positionsById = buildRevealedPositions(nodes, revealedIds, mapWidthPx, mapHeightPx)
    val segments = buildRevealedSegments(nodes, positionsById)
    return RevealedGeometry(positionsById.values.toList(), segments)
}

private fun buildRevealedPositions(
    nodes: Map<Long, NodesDomain>,
    revealedIds: Set<Long>,
    mapWidthPx: Float,
    mapHeightPx: Float,
): Map<Long, Offset> {
    val positions = HashMap<Long, Offset>(revealedIds.size)
    for (id in revealedIds) {
        val node = nodes[id] ?: continue
        positions[id] = Offset(node.cordX * mapWidthPx, (1f - node.cordY) * mapHeightPx)
    }
    return positions
}

private fun buildRevealedSegments(
    nodes: Map<Long, NodesDomain>,
    positionsById: Map<Long, Offset>,
): List<Pair<Offset, Offset>> {
    val segments = ArrayList<Pair<Offset, Offset>>()
    for ((id, pos) in positionsById) {
        val node = nodes[id] ?: continue
        for (otherId in node.connectedNodeIds) {
            // Dedupe: only emit a segment once per node pair.
            if (otherId > id) {
                val otherPos = positionsById[otherId] ?: continue
                segments += pos to otherPos
            }
        }
    }
    return segments
}

private fun DrawScope.drawCloud(
    cloud: Cloud,
    screenX: Float,
    screenY: Float,
    cloudW: Float,
    alpha: Float,
    painters: List<Painter>,
) {
    val painter = painters[cloud.variant]
    val intrinsic = painter.intrinsicSize
    val cloudH = cloudW * (intrinsic.height / intrinsic.width)
    translate(left = screenX, top = screenY - cloudH / 2f) {
        with(painter) {
            draw(size = Size(cloudW, cloudH), alpha = alpha)
        }
    }
}

private fun buildClouds(): List<Cloud> {
    val rng = Random(CLOUD_LAYOUT_SEED)
    val result = ArrayList<Cloud>(STATIONARY_GRID_COLS * STATIONARY_GRID_ROWS + DRIFTING_CLOUD_COUNT)
    result += buildStationaryClouds(rng)
    result += buildDriftingClouds(rng)
    return result
}

private fun buildStationaryClouds(rng: Random): List<Cloud> {
    val cellW = 1f / STATIONARY_GRID_COLS
    val cellH = 1f / STATIONARY_GRID_ROWS
    // Iterate one cell beyond each edge (-1 and COUNT) so the outermost
    // clouds sit slightly off-canvas — their sprites' inner halves reach
    // across the map's actual border, eliminating the bare edge gap.
    val extendedCols = STATIONARY_GRID_COLS + 2
    val extendedRows = STATIONARY_GRID_ROWS + 2
    val list = ArrayList<Cloud>(extendedCols * extendedRows)
    for (row in -1..STATIONARY_GRID_ROWS) {
        for (col in -1..STATIONARY_GRID_COLS) {
            val jitterX = (rng.nextFloat() - HALF) * cellW * STATIONARY_GRID_JITTER_FRACTION
            val jitterY = (rng.nextFloat() - HALF) * cellH * STATIONARY_GRID_JITTER_FRACTION
            list += Cloud(
                baseXFraction = (col + HALF) * cellW + jitterX,
                baseYFraction = (row + HALF) * cellH + jitterY,
                scale = lerpFloat(STATIONARY_MIN_SCALE, STATIONARY_MAX_SCALE, rng.nextFloat()),
                alpha = lerpFloat(CLOUD_MIN_ALPHA, CLOUD_MAX_ALPHA, rng.nextFloat()),
                speedDpPerSec = 0f,
                phaseSeconds = 0f,
                variant = rng.nextInt(CLOUD_SPRITE_COUNT),
                wiggleFreqX = lerpFloat(WIGGLE_FREQ_MIN_HZ, WIGGLE_FREQ_MAX_HZ, rng.nextFloat()),
                wiggleFreqY = lerpFloat(WIGGLE_FREQ_MIN_HZ, WIGGLE_FREQ_MAX_HZ, rng.nextFloat()),
                wigglePhaseX = rng.nextFloat() * TWO_PI,
                wigglePhaseY = rng.nextFloat() * TWO_PI,
            )
        }
    }
    return list
}

private fun buildDriftingClouds(rng: Random): List<Cloud> {
    val bandHeight = (CLOUD_Y_BOTTOM_FRACTION - CLOUD_Y_TOP_FRACTION) / DRIFTING_CLOUD_COUNT
    return List(DRIFTING_CLOUD_COUNT) { i ->
        val bandStart = CLOUD_Y_TOP_FRACTION + i * bandHeight
        Cloud(
            baseXFraction = 0f,
            baseYFraction = bandStart + rng.nextFloat() * bandHeight,
            scale = lerpFloat(DRIFTING_MIN_SCALE, DRIFTING_MAX_SCALE, rng.nextFloat()),
            alpha = lerpFloat(CLOUD_MIN_ALPHA, CLOUD_MAX_ALPHA, rng.nextFloat()),
            speedDpPerSec = lerpFloat(CLOUD_MIN_SPEED_DP_S, CLOUD_MAX_SPEED_DP_S, rng.nextFloat()),
            phaseSeconds = rng.nextFloat() * PHASE_MAX_SECONDS,
            variant = rng.nextInt(CLOUD_SPRITE_COUNT),
            wiggleFreqX = lerpFloat(WIGGLE_FREQ_MIN_HZ, WIGGLE_FREQ_MAX_HZ, rng.nextFloat()),
            wiggleFreqY = lerpFloat(WIGGLE_FREQ_MIN_HZ, WIGGLE_FREQ_MAX_HZ, rng.nextFloat()),
            wigglePhaseX = rng.nextFloat() * TWO_PI,
            wigglePhaseY = rng.nextFloat() * TWO_PI,
        )
    }
}

private const val HALF = 0.5f

private fun lerpFloat(
    a: Float,
    b: Float,
    t: Float,
): Float = a + (b - a) * t
