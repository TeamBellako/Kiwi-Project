package com.bellako.kiwi.features.nodes.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.bellako.kiwi.R
import com.bellako.kiwi.analytics.FirebaseEventNames
import com.bellako.kiwi.analytics.firebaseLogEvent
import com.bellako.kiwi.common.screens.components.KiwiAnnotatedStringArguments
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_AnnotatedString_P2
import com.bellako.kiwi.common.screens.components.Kiwi_FixedSizeButton
import com.bellako.kiwi.common.screens.components.Kiwi_H1
import com.bellako.kiwi.common.screens.components.Kiwi_H2
import com.bellako.kiwi.common.screens.components.Kiwi_HoldButton
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.features.appbar.screens.AppBarScreen
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.features.nodes.tests.NodesTestFactory
import com.bellako.kiwi.ui.Kiwi_Theme
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import com.bellako.kiwi.ui.getResponsiveSizeWidth
import kotlin.math.roundToInt

private const val NODE_SELECTED_SCALE = 1.6f
private const val NODE_BASE_SCALE = 0.1f

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

    val nodeScale = if (isSelected) NODE_SELECTED_SCALE else 1f

    val nodeHeight = getResponsiveSizeHeight(34.dp)

    val indicatorOffset = getResponsiveSizeHeight(14.dp) + nodeHeight * nodeScale / 2
    val displayOffset = getResponsiveSizeHeight(10.dp) + nodeHeight * nodeScale / 2

    Box(
        modifier =
            Modifier
                .scale(mapScale * NODE_BASE_SCALE),
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

// NODE ACTION BUTTON

private val SMALL_NODE_BUTTON = 240.dp
private val BIG_NODE_BUTTON = 310.dp

@Composable
fun NodeAction(
    node: NodesDomain,
    onUnlockNode: (Long) -> Unit,
    onCompleteNode: (Long) -> Unit,
    onRetryNode: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current
    val hasName = node.displayName.isNotEmpty()

    val isBlankNode = node.onExecutionEvent == "_"
    if (isBlankNode && node.status != NodeStatus.LOCKED) return

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier =
                Modifier
                    .width(IntrinsicSize.Min),
            contentAlignment = Alignment.Center,
        ) {
            Kiwi_Image(
                if (hasName)R.drawable.node_button_big else R.drawable.node_button_small,
                "Node action background",
                modifier =
                    Modifier
                        .width(getResponsiveSizeWidth(if (hasName) BIG_NODE_BUTTON else SMALL_NODE_BUTTON)),
            )
            Column(
                modifier = Modifier.matchParentSize().padding(getResponsiveSizeWidth(Spacing.small)),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (hasName) {
                    Kiwi_H2(
                        KiwiTextArguments(
                            node.displayName,
                            color = kiwiColors.colorF,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
                when (node.status) {
                    NodeStatus.LOCKED ->
                        UnlockButton("Unlock") {
                            onUnlockNode(
                                node.id,
                            )
                        }

                    NodeStatus.OPEN ->
                        PlayButton("Play") {
                            onCompleteNode(node.id)
                        }

                    NodeStatus.COMPLETED ->
                        PlayButton("Replay") {
                            onRetryNode(node.id)
                            replayFirebaseEvent(node.id)
                        }

                    else -> {}
                }
            }
        }
        if (node.status == NodeStatus.LOCKED) {
            Box(
                modifier =
                    Modifier
                        .offset(y = -getResponsiveSizeHeight(2.dp))
                        .clip(
                            RoundedCornerShape(
                                0.dp,
                                0.dp,
                                getResponsiveSizeHeight(22.dp),
                                getResponsiveSizeHeight(22.dp),
                            ),
                        ).background(kiwiColors.colorF),
                contentAlignment = Alignment.Center,
            ) {
                val annotatedString =
                    buildAnnotatedString {
                        withStyle(
                            style =
                                SpanStyle(
                                    color = kiwiColors.color1B,
                                ),
                        ) {
                            append("Cost: ")
                        }
                        withStyle(
                            style =
                                SpanStyle(
                                    color = kiwiColors.color1B,
                                    fontWeight = FontWeight.Bold,
                                ),
                        ) {
                            append(node.price.toString())
                        }
                    }

                Kiwi_AnnotatedString_P2(
                    KiwiAnnotatedStringArguments(
                        annotatedString,
                        TextAlign.Center,
                        modifier =
                            Modifier
                                .padding(
                                    vertical = getResponsiveSizeHeight(Spacing.xSmall),
                                    horizontal = getResponsiveSizeHeight(Spacing.large),
                                ),
                    ),
                )
            }
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
                fontWeight = FontWeight.Bold,
            ),
        horizontalMargin = 46.dp,
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

    Kiwi_FixedSizeButton(
        textArguments =
            KiwiTextArguments(
                text,
                color = kiwiColors.colorF,
                fontWeight = FontWeight.Bold,
            ),
        contentPaddingVertical = 6.dp,
        horizontalMargin = 46.dp,
        color = kiwiColors.color7C,
        onClick = onClick,
        sound = R.raw.snd_node_execution,
    )
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

private fun replayFirebaseEvent(nodeId: Long) {
    firebaseLogEvent(
        FirebaseEventNames.NODES_REPLAY_COMPLETED_NODE,
        mapOf(
            "node_id" to nodeId.toString(),
        ),
    )
}

@Suppress("MagicNumber")
@Preview(name = "Small Phone", widthDp = 320, heightDp = 640)
@Preview(name = "Medium Phone", widthDp = 392, heightDp = 800)
@Preview(name = "Large Phone", widthDp = 480, heightDp = 900)
@Composable
fun Node_Preview() {
    val nav = rememberNavController()
    Kiwi_Theme {
        Scaffold(
            bottomBar = {
                AppBarScreen(navController = nav)
            },
        ) { paddingValues ->
            Box(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(LocalKiwiColors.current.color2),
                contentAlignment = Alignment.Center,
            ) {
                val nodeDomain =
                    NodesTestFactory.validNodeDomain(
                        3L,
                        NodeStatus.LOCKED,
                        0.6f,
                        0.65f,
                        3,
                        100,
                        "node3",
                        "VIGILARIS CITY",
                        listOf(4L),
                    )

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Node(
                        isPlayerNode = true,
                        isSelected = true,
                        nodeStatus = nodeDomain.status,
                        nodeIcon = nodeDomain.icon,
                        mapScale = 8f,
                        displayName = nodeDomain.displayName,
                    )
                    NodeAction(
                        node = nodeDomain,
                        onUnlockNode = { id ->
                        },
                        onCompleteNode = { id ->
                        },
                        onRetryNode = { id ->
                        },
                    )
                }
            }
        }
    }
}
