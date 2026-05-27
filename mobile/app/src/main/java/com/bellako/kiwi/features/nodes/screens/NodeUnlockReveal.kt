package com.bellako.kiwi.features.nodes.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Same timings as the initial reveal so the two sequences read as the same
// animation — a completion just re-fires the cascade locally.
private const val UNLOCK_NODE_POP_MS = 300f
private const val UNLOCK_EDGE_LERP_MS = 240f
private const val UNLOCK_LABEL_FADE_MS = 220f

/**
 * One in-flight "node was just played, neighbours unlocked" cascade.
 *
 * The completed source is intentionally NOT in this cascade's scope — it was
 * already visible (and just confirmed completed) so re-popping it would feel
 * wrong. Only the edges from the source and the newly-reachable neighbours
 * animate, mirroring the initial-reveal pop on those neighbours.
 *
 * Multiple cascades can run in parallel (each has its own clock) when two
 * completions land back-to-back.
 */
internal class UnlockCascade(
    val sourceId: Long,
    val unlockedIds: Set<Long>,
    val clock: Animatable<Float, *>,
) {
    // Edge lerps first (from the already-visible source), then each
    // newly-unlocked neighbour pops in sync with the lerp reaching it.
    private val popStart: Map<Long, Float> =
        unlockedIds.associateWith { UNLOCK_EDGE_LERP_MS }

    val totalDurationMs: Float =
        UNLOCK_EDGE_LERP_MS + UNLOCK_NODE_POP_MS + UNLOCK_LABEL_FADE_MS

    // Source is unaffected — the steady-state schedule keeps it at scale 1.
    fun affectsNode(id: Long): Boolean = id in unlockedIds

    fun affectsEdge(
        fromId: Long,
        toId: Long,
    ): Boolean =
        (fromId == sourceId && toId in unlockedIds) ||
            (toId == sourceId && fromId in unlockedIds)

    fun nodeScale(id: Long): Float {
        val start = popStart[id] ?: return 1f
        val p = (clock.value - start) / UNLOCK_NODE_POP_MS
        return when {
            p <= 0f -> 0f
            p >= 1f -> 1f
            else -> EaseOutBack.transform(p).coerceAtLeast(0f)
        }
    }

    fun labelAlpha(id: Long): Float {
        val start = popStart[id] ?: return 1f
        return ((clock.value - (start + UNLOCK_NODE_POP_MS)) / UNLOCK_LABEL_FADE_MS)
            .coerceIn(0f, 1f)
    }

    fun edgeReveal(
        fromId: Long,
        toId: Long,
    ): EdgeReveal? {
        // The wave always travels FROM the just-completed source TO the newly
        // unlocked neighbour — flip `reversed` when the edge was declared the
        // other way round in the topology.
        val reversed =
            when {
                fromId == sourceId && toId in unlockedIds -> false
                toId == sourceId && fromId in unlockedIds -> true
                else -> return null
            }
        // The source doesn't pop, so the edge starts lerping the instant the
        // cascade clock starts.
        val fraction = (clock.value / UNLOCK_EDGE_LERP_MS).coerceIn(0f, 1f)
        return EdgeReveal(fraction, reversed)
    }
}

/**
 * Overlay queried by the map UI on top of the initial reveal. Returns null
 * when no cascade currently covers the requested node/edge so the caller can
 * fall back to the steady-state (fully-revealed) value.
 */
class UnlockRevealOverlay internal constructor(
    private val cascades: List<UnlockCascade>,
) {

    fun nodeScale(id: Long): Float? = cascades.firstOrNull { it.affectsNode(id) }?.nodeScale(id)

    fun labelAlpha(id: Long): Float? = cascades.firstOrNull { it.affectsNode(id) }?.labelAlpha(id)

    fun edgeReveal(
        fromId: Long,
        toId: Long,
    ): EdgeReveal? = cascades.firstNotNullOfOrNull { it.edgeReveal(fromId, toId) }
}

/**
 * Diffs the [nodes] map for status transitions. Each time a node flips to
 * COMPLETED, kicks off a cascade for whichever neighbours also just became
 * reachable (LOCKED/INACCESSIBLE → OPEN or COMPLETED). The returned overlay
 * lets the map UI override the steady-state node/edge values during the run.
 *
 * - [enabled]: gate from the caller. The initial map reveal owns the first
 *   pass; this overlay should only start tracking diffs once that has saturated,
 *   otherwise the first cascade racing the initial reveal would scale the
 *   first node to zero just after the reveal popped it to one.
 * - [awaitReady]: each queued cascade waits for this to return before its
 *   clock starts ticking. Used to hold the cascade behind the node-entry
 *   veil — state updates land while the screen is covered, but the visible
 *   pop/lerp/pop only plays once the veil has fully lifted. While the cascade
 *   sits queued (clock = 0), every node it owns reads as scale 0, so the new
 *   icon (e.g. the tick on the just-played node) is hidden until the
 *   animation actually begins.
 */
@Composable
fun rememberUnlockRevealOverlay(
    nodes: Map<Long, NodesDomain>,
    enabled: Boolean,
    awaitReady: suspend () -> Unit = {},
): UnlockRevealOverlay {
    val activeCascades = remember { mutableStateListOf<UnlockCascade>() }
    var previousStatuses by remember { mutableStateOf<Map<Long, NodeStatus>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(nodes, enabled) {
        if (!enabled) {
            previousStatuses = nodes.mapValues { it.value.status }
            return@LaunchedEffect
        }

        val newStatuses = nodes.mapValues { it.value.status }
        val prior = previousStatuses

        if (prior.isEmpty()) {
            previousStatuses = newStatuses
            return@LaunchedEffect
        }

        // Per-emission diff only. The cascade fires when a single state
        // update brings BOTH a node's OPEN→COMPLETED transition AND one or
        // more of its connected neighbours' LOCKED→OPEN transition — i.e.
        // the backend's completeNode response unlocked the neighbours
        // atomically with the completion. A later manual unlock by the user
        // (spending points to flip LOCKED→OPEN on its own) is NOT treated
        // as a delayed cascade trigger — that would over-fire whenever the
        // user navigates the map.
        nodes.values
            .asSequence()
            .filter { it.status == NodeStatus.COMPLETED && prior[it.id] != NodeStatus.COMPLETED }
            .forEach { source ->
                val unlocked =
                    source.connectedNodeIds
                        .asSequence()
                        .mapNotNull { nodes[it] }
                        .filter { neighbour ->
                            val wasReachable =
                                prior[neighbour.id] == NodeStatus.OPEN ||
                                    prior[neighbour.id] == NodeStatus.COMPLETED
                            val isReachable =
                                neighbour.status == NodeStatus.OPEN ||
                                    neighbour.status == NodeStatus.COMPLETED
                            !wasReachable && isReachable
                        }.map { it.id }
                        .toSet()

                if (unlocked.isEmpty()) return@forEach

                val cascade =
                    UnlockCascade(
                        sourceId = source.id,
                        unlockedIds = unlocked,
                        clock = Animatable(0f),
                    )
                activeCascades.add(cascade)
                scope.launch {
                    // Park the cascade — its clock stays at 0, so every
                    // newly-unlocked neighbour reads as scale 0 — until the
                    // gate (veil lift) opens.
                    awaitReady()
                    cascade.clock.animateTo(
                        targetValue = cascade.totalDurationMs,
                        animationSpec =
                            tween(
                                durationMillis = cascade.totalDurationMs.roundToInt(),
                                easing = LinearEasing,
                            ),
                    )
                    activeCascades.remove(cascade)
                }
            }

        previousStatuses = newStatuses
    }

    return UnlockRevealOverlay(activeCascades)
}
