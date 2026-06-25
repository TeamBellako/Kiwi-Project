package com.bellako.kiwi.features.nodes.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.bellako.kiwi.common.services.eventbus.EventType
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
private const val ICON_SHRINK_MS = 180
private const val ICON_POP_MS = 320

// Idle animation knobs — kept gentle so the map still reads as "alive but
// calm" rather than busy; pulling these any higher quickly looks distracting.
private const val INDICATOR_BOB_AMPLITUDE_DP = 4f
private const val INDICATOR_BOB_DURATION_MS = 850
private const val PLAY_WIGGLE_MIN_SCALE = 1.0f
private const val PLAY_WIGGLE_MAX_SCALE = 1.04f
private const val PLAY_WIGGLE_DURATION_MS = 900
private const val PLAY_GLOW_MIN_ALPHA = 0.1f
private const val PLAY_GLOW_MAX_ALPHA = 0.35f
private const val PLAY_GLOW_DURATION_MS = 1600
private const val PLAY_GLOW_SCALE = 1.12f
private const val UNLOCK_HOLD_DURATION_MS = 1000L

@Composable
fun Node(
    isPlayerNode: Boolean,
    isSelected: Boolean,
    nodeStatus: NodeStatus,
    nodeIcon: Int,
    mapScale: Float,
    displayName: String,
    revealScale: Float = 1f,
    nameAlpha: Float = 1f,
    iconAnimationReady: Boolean = true,
) {
    val kiwiColors = LocalKiwiColors.current

    val nodeScale = if (isSelected) NODE_SELECTED_SCALE else 1f

    val nodeHeight = getResponsiveSizeHeight(34.dp)

    val indicatorOffset = getResponsiveSizeHeight(14.dp) + nodeHeight * nodeScale / 2
    val displayOffset = getResponsiveSizeHeight(10.dp) + nodeHeight * nodeScale / 2

    // Shrink to 0, swap the drawable at the bottom of the pop, then pop back —
    // so the locked→unlocked icon swap happens behind a scale of 0 instead of
    // a hard cut.
    var displayedStatus by remember { mutableStateOf(nodeStatus) }
    val iconPopScale = remember { Animatable(1f) }
    // Status changes triggered by a completion event land while the entry
    // veil is fully opaque, so without a gate the icon shrink-and-pop would
    // play under the veil and the user would only see the new icon already
    // settled in place. Wait until the veil has cleared before starting the
    // animation so the swap reads on screen.
    val iconAnimationReadyState = rememberUpdatedState(iconAnimationReady)
    LaunchedEffect(nodeStatus) {
        if (nodeStatus == displayedStatus) return@LaunchedEffect
        snapshotFlow { iconAnimationReadyState.value }.first { it }
        iconPopScale.animateTo(0f, tween(durationMillis = ICON_SHRINK_MS, easing = EaseInBack))
        displayedStatus = nodeStatus
        iconPopScale.animateTo(1f, tween(durationMillis = ICON_POP_MS, easing = EaseOutBack))
    }

    Box(
        modifier =
            Modifier
                .scale(mapScale * NODE_BASE_SCALE * revealScale),
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
                nodeIcon(displayedStatus, nodeIcon),
                "node icon",
                modifier =
                    Modifier
                        .size(nodeHeight)
                        .scale(iconPopScale.value),
            )
        }

        if (isPlayerNode) {
            // Gentle idle bob so the indicator reads as alive while the map is
            // sitting still — keeps the screen from feeling frozen on first open.
            val indicatorTransition = rememberInfiniteTransition(label = "player_indicator_idle")
            val bobDp by indicatorTransition.animateFloat(
                initialValue = -INDICATOR_BOB_AMPLITUDE_DP,
                targetValue = INDICATOR_BOB_AMPLITUDE_DP,
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(durationMillis = INDICATOR_BOB_DURATION_MS, easing = EaseInOut),
                        repeatMode = RepeatMode.Reverse,
                    ),
                label = "player_indicator_bob",
            )
            Kiwi_Image(
                R.drawable.ic_player_indicator,
                "player indicator",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(18.dp))
                        .offset(y = -indicatorOffset + bobDp.dp),
            )
        }

        if (displayName.isNotEmpty()) {
            DisplayName(
                text = displayName,
                displayOffset = displayOffset,
                alpha = nameAlpha,
            )
        }
    }
}

@Composable
fun DisplayName(
    text: String,
    displayOffset: Dp,
    alpha: Float = 1f,
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
                .alpha(alpha)
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
    revealScale: Float = 1f,
    nameAlpha: Float = 1f,
    iconAnimationReady: Boolean = true,
) {
    val centered = nodeViewportOffset(node, mapState)

    // Only map-portal nodes keep their name label pinned on the map. Every
    // other node's name shouldn't persist on the map screen, so it's dropped
    // here — leaving the label exclusively for SWITCH_MAP nodes, which act as
    // gateways the player needs to be able to identify at a glance.
    val persistentName =
        node.displayName.takeIf { node.onExecutionEvent == EventType.SWITCH_MAP.name }.orEmpty()

    Box(
        modifier =
            Modifier
                .offset { IntOffset(centered.x.roundToInt(), centered.y.roundToInt()) },
        contentAlignment = Alignment.Center,
    ) {
        Node(
            isPlayerNode,
            isSelected,
            node.status,
            node.icon,
            mapState.scale,
            persistentName,
            revealScale,
            nameAlpha,
            iconAnimationReady,
        )
    }
}

@Composable
fun NodeConnections(
    nodes: Map<Long, NodesDomain>,
    mapState: MapState,
    modifier: Modifier = Modifier,
    edgeReveal: (fromId: Long, toId: Long) -> EdgeReveal = { _, _ -> EdgeReveal(1f, reversed = false) },
) {
    val kiwiColors = LocalKiwiColors.current

    Canvas(
        modifier = modifier,
    ) {
        nodes.values.forEach { from ->
            from.connectedNodeIds.forEach { toId ->
                val to = nodes[toId] ?: return@forEach

                val reveal = edgeReveal(from.id, toId)
                if (reveal.fraction <= 0f) return@forEach

                val fromPos = nodeToScreen(from, mapState)
                val toPos = nodeToScreen(to, mapState)

                // The wave can reach an edge from either endpoint; grow the line
                // from the origin endpoint toward the one being revealed.
                val originPos = if (reveal.reversed) toPos else fromPos
                val targetPos = if (reveal.reversed) fromPos else toPos
                val endPos =
                    Offset(
                        originPos.x + (targetPos.x - originPos.x) * reveal.fraction,
                        originPos.y + (targetPos.y - originPos.y) * reveal.fraction,
                    )

                val color =
                    when (to.status) {
                        NodeStatus.COMPLETED -> kiwiColors.colorF
                        NodeStatus.OPEN -> kiwiColors.color7E
                        else -> kiwiColors.color0C
                    }

                drawLine(
                    color = color,
                    start = originPos,
                    end = endPos,
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
private const val NODE_ACTION_FADE_MS = 240

@Composable
fun NodeAction(
    node: NodesDomain,
    onUnlockNode: (Long) -> Unit,
    onCompleteNode: (Long) -> Unit,
    onRetryNode: (Long) -> Unit,
    currentPoints: Int = 0,
    modifier: Modifier = Modifier,
) {
    val kiwiColors = LocalKiwiColors.current
    val hasName = node.displayName.isNotEmpty()

    val isBlankNode = node.onExecutionEvent == "_"
    if (isBlankNode && node.status != NodeStatus.LOCKED) return

    val showsPlayButton = node.status == NodeStatus.OPEN || node.status == NodeStatus.COMPLETED
    val idleTransition = rememberInfiniteTransition(label = "node_action_idle")
    val glowAlpha by idleTransition.animateFloat(
        initialValue = PLAY_GLOW_MIN_ALPHA,
        targetValue = PLAY_GLOW_MAX_ALPHA,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = PLAY_GLOW_DURATION_MS, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "play_container_glow",
    )
    // Wiggle lives at NodeAction level so it covers every action button
    // (Unlock / Play / Replay) without each button caring about it.
    val wiggleTransition = rememberInfiniteTransition(label = "node_action_wiggle_idle")
    val wiggleScale by wiggleTransition.animateFloat(
        initialValue = PLAY_WIGGLE_MIN_SCALE,
        targetValue = PLAY_WIGGLE_MAX_SCALE,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = PLAY_WIGGLE_DURATION_MS, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "node_action_wiggle",
    )

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
            // Soft halo behind the container — scaled past the image bounds so
            // the glow bleeds slightly outside the card outline. Only shown when
            // the play button is actually present.
            if (showsPlayButton) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .scale(PLAY_GLOW_SCALE)
                            .background(
                                brush =
                                    Brush.radialGradient(
                                        colors =
                                            listOf(
                                                kiwiColors.color7C.copy(alpha = glowAlpha),
                                                Color.Transparent,
                                            ),
                                    ),
                            ),
                )
            }

            Kiwi_Image(
                if (hasName) R.drawable.node_button_big else R.drawable.node_button_small,
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
                Crossfade(
                    targetState = node.status,
                    modifier = Modifier.scale(wiggleScale),
                    animationSpec = tween(durationMillis = NODE_ACTION_FADE_MS),
                    label = "nodeActionButton",
                ) { status ->
                    when (status) {
                        NodeStatus.LOCKED -> {
                            UnlockButton("Unlock", currentPoints >= node.price) {
                                onUnlockNode(
                                    node.id,
                                )
                            }
                        }

                        NodeStatus.OPEN -> {
                            PlayButton("Play") {
                                onCompleteNode(node.id)
                            }
                        }

                        NodeStatus.COMPLETED -> {
                            PlayButton("Replay") {
                                onRetryNode(node.id)
                                replayFirebaseEvent(node.id)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
        if (node.status == NodeStatus.LOCKED) {
            NodeCostBubble(currentPoints = currentPoints, price = node.price)
        }
    }
}

@Composable
private fun NodeCostBubble(
    currentPoints: Int,
    price: Int,
) {
    val kiwiColors = LocalKiwiColors.current

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
                    append("$currentPoints/$price")
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

@Composable
fun UnlockButton(
    text: String,
    hasEnoughPoints: Boolean = true,
    onHoldComplete: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_HoldButton(
        enabled = hasEnoughPoints,
        holdDurationMillis = UNLOCK_HOLD_DURATION_MS,
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
        NodeStatus.LOCKED -> {
            R.drawable.node_locked
        }

        NodeStatus.OPEN -> {
            when (nodeIcon) {
                1 -> R.drawable.node_main_quest
                2 -> R.drawable.node_side_quest
                3 -> R.drawable.node_combat
                4 -> R.drawable.node_tip
                else -> R.drawable.node_base
            }
        }

        NodeStatus.COMPLETED -> {
            R.drawable.node_completed
        }

        else -> {
            R.drawable.node_blocked
        }
    }

fun nodeToScreen(
    node: NodesDomain,
    mapState: MapState,
): Offset {
    val x = node.cordX * mapState.mapWidthPx
    val y = (1f - node.cordY) * mapState.mapHeightPx
    return Offset(x, y)
}

// Where a node sits in the viewport, expressed as an offset from the centered
// outer Box (matches the math NodeOnMap applies to position itself). Reused
// by MapMist so the fog-of-war holes line up exactly with the node icons.
fun nodeViewportOffset(
    node: NodesDomain,
    mapState: MapState,
): Offset {
    val mapX = node.cordX * mapState.mapWidthPx - mapState.mapWidthPx / 2f
    val mapY = (1f - node.cordY) * mapState.mapHeightPx - mapState.mapHeightPx / 2f
    return Offset(
        x = (mapX * mapState.scale) + mapState.offset.x,
        y = (mapY * mapState.scale) + mapState.offset.y,
    )
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
                        currentPoints = 50,
                    )
                }
            }
        }
    }
}
