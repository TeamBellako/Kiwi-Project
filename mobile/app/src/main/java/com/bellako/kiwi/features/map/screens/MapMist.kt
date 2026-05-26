package com.bellako.kiwi.features.map.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.screens.nodeViewportOffset
import kotlinx.coroutines.launch

// Screen-space "mist of war" layer that sits above the map background, node
// connections, and node icons. The base is a fully opaque rect — the map is
// completely hidden away from cleared nodes — with a subtle drifting wispy
// texture painted on top so the mist still reads as alive. Circular holes are
// punched via BlendMode.DstOut so the underlying art shows through around
// revealed nodes.
//
// "Revealed" = OPEN/COMPLETED nodes plus their still-LOCKED neighbours via
// connectedNodeIds, so the player can always see the frontier (where they can
// go next) without seeing distant locked content yet.
//
// Holes track pan/zoom because their centers come from the same MapState
// transform NodeOnMap uses. Radius is base + a small zoom-proportional term
// (NOT linear in scale) so the cleared area stays tight even at max zoom —
// linear scaling previously grew the hole large enough to wipe mist off
// the visible viewport entirely.
//
// topInsetPx lets the caller position the canvas full-screen (so mist covers
// the title/points-indicator area too) while still aligning holes with the
// InteractiveMap content center, which sits below that inset.

private const val REVEAL_ANIM_MS = 700
private const val DRIFT_PERIOD_A_MS = 24_000
private const val DRIFT_PERIOD_B_MS = 31_000
private const val BREATH_PERIOD_MS = 9_000

// Drifting wispy texture alpha range. The base mist below is always fully
// opaque, so these only control how visible the moving wisps read on top.
private const val WISPY_ALPHA_MIN = 0.04f
private const val WISPY_ALPHA_MAX = 0.14f

// Inner fraction of the hole that is fully transparent; the outer 1 - this
// is a soft feather so the edge does not read as a hard circle. Lower values
// give a smoother, more gradual fade up to the mist's full opacity.
private const val HOLE_FEATHER_INNER = 0.25f

// Per-layer alpha multipliers for the two drifting wispy layers.
private const val LAYER_A_ALPHA = 1.0f
private const val LAYER_B_ALPHA = 0.7f

// Drift amplitudes (multiples of grid spacing). Two layers move on different
// axes/speeds so the texture never reads as static or as a regular grid.
private const val DRIFT_A_AMPLITUDE = 2.0f
private const val DRIFT_B_AMPLITUDE_X = 2.4f
private const val DRIFT_B_AMPLITUDE_Y = 1.6f

// Cloud-blob grid spacing as a fraction of blob radius — < 1 = blobs overlap
// so the texture stays seamless without a visible tiling pattern.
private const val BLOB_SPACING_FACTOR = 1.2f

// Hole radius = BASE + ZOOM * mapState.scale. Bigger base + per-scale term
// give a more generous cleared area around each node while still keeping the
// mist visible on the map outside the cleared circle at every zoom level.
private val BASE_REVEAL_RADIUS_DP = 50.dp
private val ZOOM_REVEAL_RADIUS_DP = 22.dp

// Soft cloud-blob radius for the drifting wispy texture.
private val MIST_BLOB_RADIUS_DP = 240.dp

// Default mist tint — dark blue, slightly translucent (0xE6 alpha ≈ 90%) so
// the map silhouette underneath barely reads through. Override via the
// mistColor parameter if a future map wants a different biome look.
@Suppress("MagicNumber")
private val DEFAULT_MIST_COLOR = Color(0xE61F2D4D)

// Pale tint for the drifting wispy texture that sits on top of the solid
// base. White at very low alpha reads as soft fog without affecting opacity.
private val WISPY_COLOR = Color.White

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun MapMist(
    nodes: Map<Long, NodesDomain>,
    mapState: MapState,
    modifier: Modifier = Modifier,
    topInsetPx: Float = 0f,
    mistColor: Color = DEFAULT_MIST_COLOR,
) {
    val density = LocalDensity.current
    val baseRevealPx = with(density) { BASE_REVEAL_RADIUS_DP.toPx() }
    val zoomRevealPx = with(density) { ZOOM_REVEAL_RADIUS_DP.toPx() }
    val blobRadiusPx = with(density) { MIST_BLOB_RADIUS_DP.toPx() }

    val infinite = rememberInfiniteTransition(label = "map_mist")
    val driftA by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = DRIFT_PERIOD_A_MS, easing = LinearEasing),
            ),
        label = "map_mist_drift_a",
    )
    val driftB by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = DRIFT_PERIOD_B_MS, easing = LinearEasing),
            ),
        label = "map_mist_drift_b",
    )
    val wispyAlpha by infinite.animateFloat(
        initialValue = WISPY_ALPHA_MIN,
        targetValue = WISPY_ALPHA_MAX,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = BREATH_PERIOD_MS, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "map_mist_wispy_alpha",
    )

    val revealedIds = remember(nodes) { computeRevealedIds(nodes) }

    val animatables = remember { mutableStateMapOf<Long, Animatable<Float, AnimationVector1D>>() }
    // First time we see a non-empty reveal set, snap existing entries to fully
    // revealed (no animation). After that, any new id animates from 0 → 1.
    var firstNonEmpty by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(revealedIds) {
        val snapInitial = firstNonEmpty && revealedIds.isNotEmpty()
        revealedIds.forEach { id ->
            if (id !in animatables) {
                val initial = if (snapInitial) 1f else 0f
                val anim = Animatable(initial)
                animatables[id] = anim
                if (initial == 0f) {
                    scope.launch {
                        anim.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(REVEAL_ANIM_MS, easing = EaseOutCubic),
                        )
                    }
                }
            }
        }
        if (revealedIds.isNotEmpty()) firstNonEmpty = false
    }

    Canvas(
        modifier = modifier.graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // Solid base — fully opaque so the map underneath is completely
        // hidden everywhere except the cleared circles.
        drawRect(color = mistColor)

        // Drifting wispy texture on top of the solid base for a sense of
        // motion. White at low alpha reads as soft fog and never punches
        // through the opacity of the base.
        drawDriftingMist(
            color = WISPY_COLOR,
            blobRadiusPx = blobRadiusPx,
            driftA = driftA,
            driftB = driftB,
            wispyAlpha = wispyAlpha,
        )

        // Holes are positioned relative to the InteractiveMap content center,
        // which sits below the title overlay's top inset when the canvas is
        // drawn full-screen.
        val mapCenterX = w / 2f
        val mapCenterY = (h + topInsetPx) / 2f
        val holeRadius = baseRevealPx + zoomRevealPx * mapState.scale

        revealedIds.forEach { id ->
            val node = nodes[id] ?: return@forEach
            val radiusFraction = animatables[id]?.value ?: return@forEach
            if (radiusFraction <= 0f) return@forEach

            val centered = nodeViewportOffset(node, mapState)
            val center =
                Offset(
                    x = mapCenterX + centered.x,
                    y = mapCenterY + centered.y,
                )
            val effective = holeRadius * radiusFraction

            drawCircle(
                brush =
                    Brush.radialGradient(
                        colorStops =
                            arrayOf(
                                0f to Color.Black,
                                HOLE_FEATHER_INNER to Color.Black,
                                1f to Color.Transparent,
                            ),
                        center = center,
                        radius = effective,
                    ),
                radius = effective,
                center = center,
                blendMode = BlendMode.DstOut,
            )
        }
    }
}

@Suppress("MagicNumber")
private fun DrawScope.drawDriftingMist(
    color: Color,
    blobRadiusPx: Float,
    driftA: Float,
    driftB: Float,
    wispyAlpha: Float,
) {
    val w = size.width
    val h = size.height
    val spacing = blobRadiusPx * BLOB_SPACING_FACTOR
    val cols = (w / spacing).toInt() + 3
    val rows = (h / spacing).toInt() + 3

    // driftA/driftB ∈ [0, 1] — center on 0.5 so the offset oscillates around
    // zero rather than always pushing in one direction.
    val offsetAX = (driftA - 0.5f) * spacing * DRIFT_A_AMPLITUDE
    val offsetAY = (driftA - 0.5f) * spacing * DRIFT_A_AMPLITUDE
    val offsetBX = -(driftB - 0.5f) * spacing * DRIFT_B_AMPLITUDE_X
    val offsetBY = (driftB - 0.5f) * spacing * DRIFT_B_AMPLITUDE_Y

    val colorA = color.copy(alpha = wispyAlpha * LAYER_A_ALPHA)
    val colorB = color.copy(alpha = wispyAlpha * LAYER_B_ALPHA)

    for (i in -1..cols) {
        for (j in -1..rows) {
            val center = Offset(i * spacing + offsetAX, j * spacing + offsetAY)
            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors = listOf(colorA, Color.Transparent),
                        center = center,
                        radius = blobRadiusPx,
                    ),
                radius = blobRadiusPx,
                center = center,
            )
        }
    }
    // Layer B uses a half-step grid offset so the two layers never align.
    for (i in -1..cols) {
        for (j in -1..rows) {
            val center =
                Offset(
                    x = i * spacing + spacing / 2f + offsetBX,
                    y = j * spacing + spacing / 2f + offsetBY,
                )
            drawCircle(
                brush =
                    Brush.radialGradient(
                        colors = listOf(colorB, Color.Transparent),
                        center = center,
                        radius = blobRadiusPx,
                    ),
                radius = blobRadiusPx,
                center = center,
            )
        }
    }
}

private fun computeRevealedIds(nodes: Map<Long, NodesDomain>): Set<Long> {
    if (nodes.isEmpty()) return emptySet()
    val openOrCompleted =
        nodes.values.filter { it.status == NodeStatus.OPEN || it.status == NodeStatus.COMPLETED }
    val frontier =
        openOrCompleted
            .flatMap { it.connectedNodeIds }
            .filter { nodes[it]?.status == NodeStatus.LOCKED }
    return openOrCompleted.map { it.id }.toSet() + frontier
}
