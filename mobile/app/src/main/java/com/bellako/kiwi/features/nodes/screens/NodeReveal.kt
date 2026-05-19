package com.bellako.kiwi.features.nodes.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bellako.kiwi.features.nodes.data.NodesDomain
import kotlin.math.roundToInt

private const val NODE_POP_MS = 300f
private const val EDGE_LERP_MS = 240f

/** How a single connecting edge should be drawn at a given moment. */
data class EdgeReveal(
    val fraction: Float,
    // When true the wave reaches this edge from its `to` endpoint, so it must be
    // drawn growing from `to` toward `from` instead of the usual `from` -> `to`.
    val reversed: Boolean,
)

/**
 * Precomputed reveal timing for a set of nodes. The wave starts at a chosen
 * root node and cascades outward across the connection graph: a node pops, the
 * edge to a neighbour lerps, and that neighbour pops when the edge reaches it.
 */
class NodeRevealSchedule(
    private val nodePopStartMs: Map<Long, Float>,
    val totalDurationMs: Float,
) {
    fun nodeScale(
        id: Long,
        clockMs: Float,
    ): Float {
        val start = nodePopStartMs[id] ?: return 1f
        val p = (clockMs - start) / NODE_POP_MS
        return when {
            p <= 0f -> 0f
            p >= 1f -> 1f
            else -> EaseOutBack.transform(p).coerceAtLeast(0f)
        }
    }

    fun edgeReveal(
        fromId: Long,
        toId: Long,
        clockMs: Float,
    ): EdgeReveal {
        val fromStart = nodePopStartMs[fromId]
        val toStart = nodePopStartMs[toId]
        if (fromStart == null || toStart == null) return EdgeReveal(1f, reversed = false)

        // The wave travels away from whichever endpoint pops first.
        val reversed = toStart < fromStart
        val originStart = if (reversed) toStart else fromStart
        val edgeStart = originStart + NODE_POP_MS
        val fraction = ((clockMs - edgeStart) / EDGE_LERP_MS).coerceIn(0f, 1f)
        return EdgeReveal(fraction, reversed)
    }
}

private fun buildNodeRevealSchedule(
    nodes: Map<Long, NodesDomain>,
    rootNodeId: Long?,
): NodeRevealSchedule {
    if (nodes.isEmpty()) return NodeRevealSchedule(emptyMap(), 0f)

    // Undirected adjacency so the wave can spread across a connection regardless
    // of the direction it was declared in.
    val adjacency = HashMap<Long, MutableSet<Long>>()
    nodes.values.forEach { node ->
        node.connectedNodeIds.forEach { other ->
            if (other in nodes) {
                adjacency.getOrPut(node.id) { mutableSetOf() }.add(other)
                adjacency.getOrPut(other) { mutableSetOf() }.add(node.id)
            }
        }
    }

    val root =
        rootNodeId?.takeIf { it in nodes }
            ?: nodes.keys
                .filter { id -> nodes.values.none { id in it.connectedNodeIds } }
                .minOrNull()
            ?: nodes.keys.min()

    val popStart = HashMap<Long, Float>()
    val queue = ArrayDeque<Long>()
    popStart[root] = 0f
    queue.add(root)

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val currentPop = popStart[current] ?: continue
        val neighbourPop = currentPop + NODE_POP_MS + EDGE_LERP_MS
        adjacency[current]?.forEach { neighbour ->
            val existing = popStart[neighbour]
            if (existing == null || neighbourPop < existing) {
                popStart[neighbour] = neighbourPop
                queue.add(neighbour)
            }
        }
    }

    // Nodes in a disconnected component still appear with the first wave.
    nodes.keys.forEach { id -> popStart.putIfAbsent(id, 0f) }

    val total = (popStart.values.maxOrNull() ?: 0f) + NODE_POP_MS
    return NodeRevealSchedule(popStart, total)
}

/**
 * Builds the reveal schedule cascading from [rootNodeId] and drives the clock.
 *
 * - [alreadyPlayed]: the reveal has run once this login session, so show every
 *   node/edge fully with no animation (e.g. returning from Settings).
 * - [started]: the gating signal (loading screen finished its exit). Until it
 *   is true the map reads as empty.
 * - [onRevealConsumed]: invoked the moment the sequence begins so it is marked
 *   as played for the rest of the session, even if interrupted mid-animation.
 */
@Composable
fun rememberNodeReveal(
    nodes: Map<Long, NodesDomain>,
    rootNodeId: Long?,
    started: Boolean,
    alreadyPlayed: Boolean,
    onRevealConsumed: () -> Unit,
): Pair<NodeRevealSchedule, Float> {
    val topologyKey =
        nodes.entries
            .sortedBy { it.key }
            .map { it.key to it.value.connectedNodeIds } to rootNodeId

    val schedule = remember(topologyKey) { buildNodeRevealSchedule(nodes, rootNodeId) }
    val clock = remember(topologyKey) { Animatable(0f) }
    var clockMs by remember(topologyKey) { mutableFloatStateOf(0f) }
    // Snapshot once: flipping the VM flag when we start must NOT restart this.
    val playedAtStart = remember(topologyKey) { alreadyPlayed }

    LaunchedEffect(topologyKey, started) {
        if (playedAtStart) {
            clockMs = schedule.totalDurationMs
            return@LaunchedEffect
        }
        if (!started || schedule.totalDurationMs <= 0f) {
            clock.snapTo(0f)
            clockMs = 0f
            return@LaunchedEffect
        }
        // Mark consumed as soon as it starts so it never replays or re-hides
        // later in the session, even if this animation is cut short.
        onRevealConsumed()
        clock.snapTo(0f)
        clock.animateTo(
            targetValue = schedule.totalDurationMs,
            animationSpec =
                tween(
                    durationMillis = schedule.totalDurationMs.roundToInt(),
                    easing = LinearEasing,
                ),
        ) {
            clockMs = value
        }
    }

    return schedule to clockMs
}
