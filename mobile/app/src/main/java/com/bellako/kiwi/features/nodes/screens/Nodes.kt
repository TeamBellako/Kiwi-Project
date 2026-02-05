package com.bellako.kiwi.features.nodes.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AdaptableSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_HoldButton
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlin.math.roundToInt

@Suppress("MagicNumber")
private val selectedScale = 1.6f

@Composable
fun Node(
    isPlayerNode: Boolean,
    isSelected: Boolean,
    nodeStatus: NodeStatus,
    nodeIcon: Int,
    mapScale: Float,
    displayName: String,
) {
    val kiwiColors = LocalKiwiColors.current

    val nodeScale = if (isSelected) selectedScale else 1f

    @Suppress("MagicNumber")
    val baseScale = 0.1f

    val nodeHeight = getResponsiveSizeHeight(34.dp)

    val indicatorOffset = getResponsiveSizeHeight(14.dp) + nodeHeight * nodeScale / 2
    val displayOffset = getResponsiveSizeHeight(10.dp) + nodeHeight * nodeScale / 2

    Box(
        modifier =
            Modifier
                .scale(mapScale * baseScale),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .scale(nodeScale)
                    .then(
                        if (isPlayerNode) {
                            Modifier
                                .border(
                                    width = getResponsiveSizeHeight(2.dp),
                                    color = kiwiColors.colorF,
                                    shape = CircleShape,
                                ).padding(getResponsiveSizeHeight(2.dp))
                        } else {
                            Modifier
                                .padding(getResponsiveSizeHeight(2.dp))
                        },
                    ),
        ) {
            Kiwi_Image(
                nodeIcon(nodeStatus, nodeIcon),
                "node icon",
                modifier =
                    Modifier
                        .size(nodeHeight),
            )
        }

        if (isPlayerNode) {
            Kiwi_Image(
                R.drawable.ic_player_indicator,
                "player indicator",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(18.dp))
                        .offset(y = -indicatorOffset),
            )
        }

        if (displayName.isNotEmpty()) {
            DisplayName(
                text = displayName,
                displayOffset = displayOffset,
            )
        }
    }
}

@Composable
fun DisplayName(
    text: String,
    displayOffset: Dp,
) {
    val kiwiColors = LocalKiwiColors.current
    val shape = RoundedCornerShape(getResponsiveSizeHeight(60.dp))

    var heightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    val correctedOffset =
        with(density) {
            displayOffset + (heightPx / 2).toDp()
        }

    Box(
        modifier =
            Modifier
                .offset(y = correctedOffset)
                .onSizeChanged { heightPx = it.height }
                .widthIn(max = getResponsiveSizeHeight(260.dp))
                .background(
                    color = kiwiColors.color1B,
                    shape = shape,
                ).border(
                    width = getResponsiveSizeHeight(4.dp),
                    color = kiwiColors.colorF,
                    shape = shape,
                ).padding(
                    horizontal = getResponsiveSizeHeight(30.dp),
                    vertical = getResponsiveSizeHeight(6.dp),
                ),
    ) {
        Kiwi_H1(
            KiwiTextArguments(
                text,
                color = kiwiColors.colorF,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
fun NodeOnMap(
    node: NodesDomain,
    mapState: MapState,
    isPlayerNode: Boolean,
    isSelected: Boolean,
) {
    val mapX = node.cordX * mapState.mapWidthPx - mapState.mapWidthPx / 2
    val mapY = (1f - node.cordY) * mapState.mapHeightPx - mapState.mapHeightPx / 2
    val scaledX = (mapX * mapState.scale) + mapState.offset.x
    val scaledY = (mapY * mapState.scale) + mapState.offset.y

    Box(
        modifier =
            Modifier
                .offset { IntOffset(scaledX.roundToInt(), scaledY.roundToInt()) },
        contentAlignment = Alignment.Center,
    ) {
        Node(
            isPlayerNode,
            isSelected,
            node.status,
            node.icon,
            mapState.scale,
            node.displayName,
        )
    }
}

@Composable
fun NodeAction(
    isPlayerNode: Boolean,
    node: NodesDomain,
    onUnlockNode: (Long) -> Unit,
    onCompleteNode: (Long) -> Unit,
) {
    val offset = if (isPlayerNode) 64.dp else 48.dp
    Box(
        modifier =
            Modifier
                .offset(y = -getResponsiveSizeHeight(offset)),
    ) {
        when (node.status) {
            NodeStatus.LOCKED -> UnlockButton("Unlock (" + node.price + ")") { onUnlockNode(node.id) }
            NodeStatus.OPEN ->
                PlayButton("Play") {
                    onCompleteNode(node.id)
                }
            NodeStatus.COMPLETED -> PlayButton("Replay") { replayFirebaseEvent(node.id) }
            else -> {}
        }
    }
}

@Composable
fun UnlockButton(
    text: String,
    hasEnoughPoints: Boolean = true,
    onHoldComplete: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_HoldButton(
        enabled = hasEnoughPoints,
        textArguments =
            KiwiTextArguments(
                text,
                color = kiwiColors.colorF,
                bold = true,
            ),
        contentPaddingHorizontal = Spacing.xLarge,
        color = kiwiColors.color8,
        fillColor = kiwiColors.color8A,
        onHoldComplete = onHoldComplete,
        sound = R.raw.snd_node_unlocked,
    )
}

@Composable
fun PlayButton(
    text: String,
    onClick: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_AdaptableSizeButton(
        textArguments =
            KiwiTextArguments(
                text,
                color = kiwiColors.colorF,
                bold = true,
            ),
        contentPaddingHorizontal = Spacing.xLarge,
        color = kiwiColors.color7D,
        onClick = onClick,
        sound = R.raw.snd_node_execution,
    )
}

@Suppress("UnusedParameter")
private fun replayFirebaseEvent(id: Long) {
 /*   firebaseLogEvent(
        FirebaseEventNames.NODES_REPLAY_COMPLETED_NODE,
        mapOf(
            "node_id" to Long,
        ),
    ) // Uncomment when added event on firebase server */
}

@Composable
fun NodeConnections(
    nodes: Map<Long, NodesDomain>,
    mapState: MapState,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current

    Canvas(
        modifier = modifier,
    ) {
        nodes.values.forEach { from ->
            from.connectedNodeIds.forEach { toId ->
                val to = nodes[toId] ?: return@forEach

                val fromPos = nodeToScreen(from, mapState)
                val toPos = nodeToScreen(to, mapState)

                val color =
                    when (to.status) {
                        NodeStatus.COMPLETED -> kiwiColors.colorF
                        NodeStatus.OPEN -> kiwiColors.color7E
                        else -> kiwiColors.color0C
                    }

                drawLine(
                    color = color,
                    start = fromPos,
                    end = toPos,
                    strokeWidth = 2.0f,
                    cap = StrokeCap.Butt,
                )
            }
        }
    }
}

// HELPERS
@Suppress("MagicNumber")
@DrawableRes
private fun nodeIcon(
    nodeStatus: NodeStatus,
    nodeIcon: Int,
): Int =
    when (nodeStatus) {
        NodeStatus.LOCKED -> R.drawable.node_locked
        NodeStatus.OPEN ->
            when (nodeIcon) {
                1 -> R.drawable.node_main_quest
                2 -> R.drawable.node_side_quest
                3 -> R.drawable.node_combat
                4 -> R.drawable.node_tip
                else -> R.drawable.node_base
            }
        NodeStatus.COMPLETED -> R.drawable.node_completed
        else -> R.drawable.node_blocked
    }

fun nodeToScreen(
    node: NodesDomain,
    mapState: MapState,
): Offset {
    val x = node.cordX * mapState.mapWidthPx
    val y = (1f - node.cordY) * mapState.mapHeightPx
    return Offset(x, y)
}

fun screenToMap(
    tap: Offset,
    mapState: MapState,
): Offset {
    val normalizedX = tap.x / mapState.mapWidthPx
    val normalizedY = 1f - (tap.y / mapState.mapHeightPx)

    return Offset(normalizedX, normalizedY)
}

fun distance(
    p1: Offset,
    p2: Offset,
): Float {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return kotlin.math.sqrt(dx * dx + dy * dy)
}
