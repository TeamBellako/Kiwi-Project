package com.bellako.kiwi.features.nodes.screens

import androidx.compose.animation.core.Animatable
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Source-icon-swap leading phase. Node's local iconPopScale shrinks the source
// icon (ICON_SHRINK_MS) and pops it back (ICON_POP_MS) — ~500ms total. The
// schedule's edge phase doesn't start until source.popStart + NODE_POP_MS in
// schedule-time, which would land at 300ms; delaying the cascade clock by this
// offset shifts that to real-time 500ms, exactly when the source's icon swap
// finishes. So the visible order becomes: source icon swap → edge lerp →
// neighbour pop, matching the initial reveal's vocabulary.
private const val SOURCE_ICON_SWAP_MS = 500f
private val CASCADE_LEADING_DELAY_MS = (SOURCE_ICON_SWAP_MS - NODE_POP_MS).toLong()

/**
 * One in-flight "node just completed, neighbours newly stepped out of the
 * mist" cascade.
 *
 * Powered by the same [NodeRevealSchedule] the initial map reveal uses — built
 * over a sub-graph containing the just-completed source and its newly-visible
 * neighbours, rooted at the source. The schedule's pop times and edge starts
 * are reused verbatim so this animation reads identically to the initial
 * reveal wave.
 *
 * The source is excluded from the scaled scope: [Node]'s local iconPopScale
 * already plays a shrink/swap/pop on the source the moment its status flips,
 * and the cascade's leading delay aligns the schedule's edge phase with the
 * end of that local animation.
 */
internal class CompletionCascade(
    val sourceId: Long,
    val schedule: NodeRevealSchedule,
    val clock: Animatable<Float, *>,
) {
    fun nodeScale(id: Long): Float? {
        if (id == sourceId) return null
        if (id !in schedule.coveredNodes) return null
        return schedule.nodeScale(id, clock.value)
    }

    fun labelAlpha(id: Long): Float? {
        if (id == sourceId) return null
        if (id !in schedule.coveredNodes) return null
        return schedule.labelAlpha(id, clock.value)
    }

    fun edgeReveal(
        fromId: Long,
        toId: Long,
    ): EdgeReveal? {
        // The cascade only paints edges where both endpoints are in scope —
        // i.e. source ↔ newly-visible neighbour. Everything else falls through
        // to the (saturated) initial schedule.
        if (fromId !in schedule.coveredNodes || toId !in schedule.coveredNodes) return null
        return schedule.edgeReveal(fromId, toId, clock.value)
    }
}

/**
 * Overlay queried by the map UI on top of the initial reveal. Returns null
 * when no cascade currently covers the requested node/edge so the caller can
 * fall back to the steady-state (fully-revealed) value.
 */
class UnlockRevealOverlay internal constructor(
    private val cascades: List<CompletionCascade>,
) {
    fun nodeScale(id: Long): Float? =
        cascades.asSequence().mapNotNull { it.nodeScale(id) }.firstOrNull()

    fun labelAlpha(id: Long): Float? =
        cascades.asSequence().mapNotNull { it.labelAlpha(id) }.firstOrNull()

    fun edgeReveal(
        fromId: Long,
        toId: Long,
    ): EdgeReveal? = cascades.firstNotNullOfOrNull { it.edgeReveal(fromId, toId) }
}

/**
 * Diffs the [nodes] map for status transitions. Each time a node flips to
 * COMPLETED, kicks off a cascade for whichever neighbours stepped out of
 * INACCESSIBLE in the same emission (the mist that hid them just got a hole
 * punched). The returned overlay lets the map UI override the steady-state
 * node/edge values during the run.
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
    val activeCascades = remember { mutableStateListOf<CompletionCascade>() }
    val previousStatusesRef = remember { mutableStateOf<Map<Long, NodeStatus>>(emptyMap()) }
    val scope = rememberCoroutineScope()

    // SYNCHRONOUS detection during composition. Newly-completed sources +
    // their newly-visible neighbours are turned into CompletionCascade
    // instances and pushed into activeCascades inside this remember() block,
    // so by the time NodeOnMap/NodeConnections read the overlay later in the
    // same composition pass, the cascade is already in place with clock=0 —
    // its nodeScale/edgeReveal return 0, hiding the new neighbour and its
    // edge on the very first frame. Deferring detection to a LaunchedEffect
    // would let one frame draw at the schedule's saturated values first.
    //
    // Per-emission diff only. The cascade fires when a single state update
    // brings BOTH a node's flip to COMPLETED AND one or more of its connected
    // neighbours stepping out of INACCESSIBLE. The backend's getNodesForUser
    // only returns nodes with a UserNodeStatus row, so INACCESSIBLE nodes
    // simply aren't in the response — they appear (as LOCKED) only when a
    // predecessor's completion calls lockNode on them. "First time we've seen
    // this neighbour" (prior absent) and "was INACCESSIBLE" mean the same
    // thing for the purposes of this cascade.
    val newCascades =
        remember(nodes, enabled) {
            if (!enabled) {
                previousStatusesRef.value = nodes.mapValues { it.value.status }
                return@remember emptyList<CompletionCascade>()
            }
            val prior = previousStatusesRef.value
            val newStatuses = nodes.mapValues { it.value.status }
            if (prior.isEmpty()) {
                previousStatusesRef.value = newStatuses
                return@remember emptyList<CompletionCascade>()
            }

            val cascades = mutableListOf<CompletionCascade>()
            nodes.values
                .asSequence()
                .filter { it.status == NodeStatus.COMPLETED && prior[it.id] != NodeStatus.COMPLETED }
                .forEach { source ->
                    val newlyVisible =
                        source.connectedNodeIds
                            .mapNotNull { nodes[it] }
                            .filter { neighbour ->
                                val priorStatus = prior[neighbour.id]
                                (priorStatus == null || priorStatus == NodeStatus.INACCESSIBLE) &&
                                    neighbour.status != NodeStatus.INACCESSIBLE
                            }

                    if (newlyVisible.isEmpty()) return@forEach

                    // Sub-graph for the cascade: the source + its newly-visible
                    // neighbours, rooted at the source so the BFS pop times put
                    // source at popStart=0 and each neighbour at NODE_POP_MS +
                    // EDGE_LERP_MS afterward — exactly the initial reveal's
                    // shape, locally.
                    val subGraph = (listOf(source) + newlyVisible).associateBy { it.id }
                    val subSchedule = buildNodeRevealSchedule(subGraph, source.id)

                    cascades.add(
                        CompletionCascade(
                            sourceId = source.id,
                            schedule = subSchedule,
                            clock = Animatable(0f),
                        ),
                    )
                }

            activeCascades.addAll(cascades)
            previousStatusesRef.value = newStatuses
            cascades.toList()
        }

    // Kick off the animation for each newly-added cascade. The for-loop body
    // doesn't suspend, so all scope.launch coroutines are dispatched before
    // any cancellation point — if this LaunchedEffect re-fires for a later
    // completion, the previously-launched animations keep running on the
    // remembered scope.
    LaunchedEffect(newCascades) {
        for (cascade in newCascades) {
            scope.launch {
                awaitReady()
                // Wait for the source's iconPopScale (shrink → swap → pop)
                // to finish before the schedule's edge phase begins. Without
                // this delay the edge would start lerping at schedule-time
                // NODE_POP_MS = 300ms while the source icon is still mid-swap.
                if (CASCADE_LEADING_DELAY_MS > 0L) {
                    delay(CASCADE_LEADING_DELAY_MS)
                }
                cascade.clock.animateTo(
                    targetValue = cascade.schedule.totalDurationMs,
                    animationSpec =
                        tween(
                            durationMillis = cascade.schedule.totalDurationMs.roundToInt(),
                            easing = LinearEasing,
                        ),
                )
                activeCascades.remove(cascade)
            }
        }
    }

    return UnlockRevealOverlay(activeCascades)
}
