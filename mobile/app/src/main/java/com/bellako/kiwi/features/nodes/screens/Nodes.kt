package com.bellako.kiwi.features.nodes.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.common.screens.components.KiwiTextArguments
import com.bellako.kiwi.common.screens.components.Kiwi_Button
import com.bellako.kiwi.common.screens.components.Kiwi_HoldButton
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.ui.LocalKiwiColors
import com.bellako.kiwi.ui.Spacing
import com.bellako.kiwi.ui.getResponsiveSizeHeight
import kotlin.math.roundToInt

@Composable
fun Node(
    node: NodesDomain,
    size: Dp,
) {
    val kiwiColors = LocalKiwiColors.current

    val color =
        when (node.status) {
            NodeStatus.INACCESSIBLE -> kiwiColors.color0A
            NodeStatus.LOCKED -> kiwiColors.colorR
            NodeStatus.OPEN -> kiwiColors.color8A
            NodeStatus.COMPLETED -> kiwiColors.color7B
        }

    Box(
        modifier =
            Modifier
                .size(size)
                .clip(RoundedCornerShape(getResponsiveSizeHeight(20.dp)))
                .background(color = color)
                .border(
                    width = getResponsiveSizeHeight(2.dp),
                    color = kiwiColors.color0,
                    shape = RoundedCornerShape(getResponsiveSizeHeight(20.dp)),
                ),
    ) {
    }
}

@Composable
fun NodeOnMap(
    node: NodesDomain,
    mapState: MapState,
    isSelected: Boolean,
    onNodeClick: (Float, Float, Int) -> Unit,
    onUnlockNode: (Int) -> Unit,
    onCompleteNode: (Int) -> Unit,
) {
    val density = LocalDensity.current

    @Suppress("MagicNumber")
    val scaleSelected = if (isSelected) 1.3f else 1f

    val baseNodeSizeDP = getResponsiveSizeHeight(4.dp)
    val baseNodeSizePx = with(density) { baseNodeSizeDP.toPx() }
    val scaledNodeSize = baseNodeSizePx * mapState.scale * scaleSelected

    val mapX = node.cordX * mapState.mapWidthPx - mapState.mapWidthPx / 2
    val mapY = (1f - node.cordY) * mapState.mapHeightPx - mapState.mapHeightPx / 2
    val scaledX = (mapX * mapState.scale) + mapState.offset.x
    val scaledY = (mapY * mapState.scale) + mapState.offset.y

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .offset { IntOffset(scaledX.roundToInt(), scaledY.roundToInt()) }
                .pointerInput(Unit) {
                    detectTapGestures { onNodeClick(node.cordX, node.cordY, node.id) }
                },
        contentAlignment = Alignment.Center,
    ) {
        Node(node, with(density) { scaledNodeSize.toDp() })
    }

    val buttonYPadding = getResponsiveSizeHeight(8.dp)

    if (isSelected) {
        Box(
            modifier =
                Modifier
                    .offset {
                        IntOffset(
                            scaledX.roundToInt(),
                            (scaledY + scaledNodeSize + buttonYPadding.toPx()).roundToInt(),
                        )
                    },
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
