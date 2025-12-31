package com.bellako.kiwi.features.nodes.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.R
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_Display2
import com.bellako.kiwi.common.screens.components.Kiwi_HoldButton
import com.bellako.kiwi.common.screens.components.Kiwi_Image
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlin.math.roundToInt

@Composable
fun NodeOnMap(
    node: NodesDomain,
    mapState: MapState,
    isSelected: Boolean,
    onNodeClick: (Float, Float, Int) -> Unit,
    onUnlockNode: (Int) -> Unit,
    onCompleteNode: (Int) -> Unit,
) {
    val mapX = node.cordX * mapState.mapWidthPx - mapState.mapWidthPx / 2
    val mapY = (1f - node.cordY) * mapState.mapHeightPx - mapState.mapHeightPx / 2
    val scaledX = (mapX * mapState.scale) + mapState.offset.x
    val scaledY = (mapY * mapState.scale) + mapState.offset.y

    Box(
        modifier =
            Modifier
                .offset { IntOffset(scaledX.roundToInt(), scaledY.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures { onNodeClick(node.cordX, node.cordY, node.id) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Node(true, isSelected, node.status, mapState.scale, node.displayName)

        if (isSelected) {
            Box(
                modifier = Modifier.offset(y = -getResponsiveSizeHeight(64.dp)),
            ) {
                when (node.status) {
                    NodeStatus.LOCKED -> UnlockButton("Unlock") { onUnlockNode(node.id) }
                    NodeStatus.OPEN -> PlayButton("Complete") { onCompleteNode(node.id) }
                    NodeStatus.COMPLETED -> PlayButton("Replay") { /* TODO */ }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun Node(
    isPlayer: Boolean,
    isSelected: Boolean,
    nodeStatus: NodeStatus,
    mapScale: Float,
    displayName: String,
) {
    val kiwiColors = LocalKiwiColors.current

    @Suppress("MagicNumber")
    val selectedScale = if (isSelected) 1.6f else 1f

    @Suppress("MagicNumber")
    val baseScale = 0.1f

    val nodeHeight = getResponsiveSizeHeight(30.dp)
    val offset = getResponsiveSizeHeight(12.dp)

    val indicatorOffset = offset + nodeHeight * selectedScale / 2
    val displayOffset = offset + nodeHeight + nodeHeight * selectedScale / 2

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .scale(mapScale * baseScale),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .scale(selectedScale)
                    .then(
                        if (isPlayer) {
                            Modifier.border(
                                width = getResponsiveSizeHeight(2.dp),
                                color = kiwiColors.colorF,
                                shape = CircleShape,
                            )
                        } else {
                            Modifier
                        },
                    ),
        ) {
            Kiwi_Image(
                nodeIcon(nodeStatus),
                "node icon",
                modifier =
                    Modifier
                        .size(getResponsiveSizeHeight(30.dp)),
            )
        }

        if (isPlayer) {
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
    val shape = RoundedCornerShape(getResponsiveSizeHeight(30.dp))

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .offset(
                    y = displayOffset,
                ).background(
                    color = kiwiColors.color1B,
                    shape = shape,
                ).border(
                    width = getResponsiveSizeHeight(3.dp),
                    color = kiwiColors.colorF,
                    shape = shape,
                ).padding(
                    horizontal = getResponsiveSizeHeight(16.dp),
                    vertical = getResponsiveSizeHeight(6.dp),
                ),
    ) {
        Kiwi_Display2(
            KiwiTextArguments(
                text,
                color = kiwiColors.colorF,
            ),
        )
    }
}

@Composable
fun PlayButton(
    text: String,
    onClick: () -> Unit,
) {
    val kiwiColors = LocalKiwiColors.current

    Kiwi_Button(
        textArguments =
            KiwiTextArguments(
                text,
                color = kiwiColors.color7,
                bold = true,
            ),
        onClick = onClick,
        color = kiwiColors.color5A,
        modifier =
            Modifier
                .padding(getResponsiveSizeHeight(Spacing.large)),
    )
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
                color = kiwiColors.color7,
                bold = true,
            ),
        onHoldComplete = onHoldComplete,
        color = kiwiColors.color8,
        fillColor = kiwiColors.color8A,
        modifier =
            Modifier
                .padding(getResponsiveSizeHeight(Spacing.large)),
    )
}

@DrawableRes
private fun nodeIcon(
    nodeStatus: NodeStatus,
    // TODO nodeType
): Int =
    when (nodeStatus) {
        NodeStatus.LOCKED -> R.drawable.node_locked
        NodeStatus.OPEN -> R.drawable.node_base
        NodeStatus.COMPLETED -> R.drawable.node_completed
        else -> R.drawable.node_blocked
    }
