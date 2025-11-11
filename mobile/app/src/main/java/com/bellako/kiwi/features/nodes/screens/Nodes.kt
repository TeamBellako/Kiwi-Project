package com.bellako.kiwi.features.nodes.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bellako.kiwi.features.map.data.MapState
import com.bellako.kiwi.features.nodes.data.NodeStatus
import com.bellako.kiwi.features.nodes.data.NodesDomain
import com.bellako.kiwi.ui.LocalKiwiColors
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
        modifier = Modifier.size(size).background(color),
    ) {
    }
}

@Composable
fun NodeOnMap(
    node: NodesDomain,
    mapState: MapState,
) {
    val baseNodeSizeDP = getResponsiveSizeHeight(5.dp)
    val density = LocalDensity.current
    val baseNodeSizePx = with(density) { baseNodeSizeDP.toPx() }

    val mapX = node.posX * mapState.mapWidthPx - mapState.mapWidthPx / 2
    val mapY = (1f - node.posY) * mapState.mapHeightPx - mapState.mapHeightPx / 2

    val scaledX = (mapX * mapState.scale) + mapState.offset.x
    val scaledY = (mapY * mapState.scale) + mapState.offset.y

    val scaledNodeSize = baseNodeSizePx * mapState.scale

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .offset {
                    IntOffset(
                        scaledX.roundToInt(),
                        scaledY.roundToInt(),
                    )
                },
    ) {
        Node(node, with(density) { scaledNodeSize.toDp() })
    }
}
